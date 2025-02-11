#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h> //socket.h includes a number of definitions of structures needed for sockets.
#include <netinet/in.h> //in.h contains constants and structures needed for internet domain addresses.
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/tcp.h>
#include <ctime>


#include <stdarg.h>

#include <assert.h>
#include <ctype.h>
#include <unistd.h>


#include <errno.h>
#include "rtmp_client.h"

#define GetSockError() errno
#define OBS_OUTPUT_BAD_PATH -1
#define RTMP_DEFAULT_CHUNKSIZE	128
#define TRUE	1
#define FALSE	0




static inline bool dstr_is_empty(const struct dstr *str)
{
    if (!str->array || !str->len)
        return true;
    if (!*str->array)
        return true;

    return false;
}

void RTMP_Reset(RTMP *r)
{
    r->m_inChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_outChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_bSendChunkSizeInfo = 1;
    r->m_nBufferMS = 30000;
    r->m_nClientBW = 2500000;
    r->m_nClientBW2 = 2;
    r->m_nServerBW = 2500000;
    r->m_fAudioCodecs = 3191.0;
    r->m_fVideoCodecs = 252.0;
    r->Link.curStreamIdx = 0;
    r->Link.nStreams = 0;
    r->Link.receiveTimeout = 30;
    r->Link.sendTimeout = 15;
    r->Link.swfAge = 30;
}
extern const char RTMPProtocolStringsLower[][7];
const char RTMPProtocolStringsLower[][7] =
        {
                "rtmp",
                "rtmpt",
                "rtmpe",
                "rtmpte",
                "rtmps",
                "rtmpts",
                "",
                "",
                "rtmfp"
        };

void RTMP_Init(RTMP *r)
{
    memset(r, 0, sizeof(RTMP));
    r->m_sb.sb_socket = -1;
    RTMP_Reset(r);
   // RTMP_TLS_Init(r); TODO: MIGHT NOT NEED THIS
}
static void
SocksSetup(RTMP *r, AVal *sockshost)
{
    if (sockshost->av_len)
    {
        const char *socksport = strchr(sockshost->av_val, ':');
        char *hostname = strdup(sockshost->av_val);

        if (socksport)
            hostname[socksport - sockshost->av_val] = '\0';
        r->Link.sockshost.av_val = hostname;
        r->Link.sockshost.av_len = (int)strlen(hostname);

        r->Link.socksport = socksport ? atoi(socksport + 1) : 1080;

        LOGI("SocksSetup","Connecting via SOCKS proxy: %s:%d", r->Link.sockshost.av_val,
             r->Link.socksport);
    }
    else{
        LOGI("SocksSetup","NO PROXY?");
        r->Link.sockshost.av_val = NULL;
        r->Link.sockshost.av_len = 0;
        r->Link.socksport = 0;
    }
}

int RTMP_SetupURL(RTMP *r, char *url)
{
    int ret, len;
    unsigned int port = 0;

    len = (int)strlen(url);
    ret = RTMP_ParseURL(url, &r->Link.protocol, &r->Link.hostname,
                        &port, &r->Link.app);
    if (!ret)
        return ret;
    r->Link.port = port;

    if (!r->Link.tcUrl.av_len)
    {
        r->Link.tcUrl.av_val = url;
        if (r->Link.app.av_len)
        {
            if (r->Link.app.av_val < url + len)
            {
                /* if app is part of original url, just use it */
                r->Link.tcUrl.av_len = r->Link.app.av_len + (r->Link.app.av_val - url);
            }
            else
            {
                len = r->Link.hostname.av_len + r->Link.app.av_len +
                      sizeof("rtmpte://:65535/");
                r->Link.tcUrl.av_val = static_cast<char *>(malloc(len));
                r->Link.tcUrl.av_len = snprintf(r->Link.tcUrl.av_val, len,
                                                "%s://%.*s:%d/%.*s",
                                                RTMPProtocolStringsLower[r->Link.protocol],
                                                r->Link.hostname.av_len, r->Link.hostname.av_val,
                                                r->Link.port,
                                                r->Link.app.av_len, r->Link.app.av_val);
                r->Link.lFlags |= RTMP_LF_FTCU;
            }
        }
        else
        {
            r->Link.tcUrl.av_len = (int)strlen(url);
        }
    }

#ifdef CRYPTO
    if ((r->Link.lFlags & RTMP_LF_SWFV) && r->Link.swfUrl.av_len)
#ifdef USE_HASHSWF
        RTMP_HashSWF(r->Link.swfUrl.av_val, &r->Link.SWFSize,
        (unsigned char *)r->Link.SWFHash, r->Link.swfAge);
#else
        return FALSE;
#endif
#endif

    SocksSetup(r, &r->Link.sockshost);

    if (r->Link.port == 0)
    {
        if (r->Link.protocol & RTMP_FEATURE_SSL)
            r->Link.port = 443;
        else if (r->Link.protocol & RTMP_FEATURE_HTTP)
            r->Link.port = 80;
        else
            r->Link.port = 1935;
    }
    LOGI("SocksSetup","port -> %d",r->Link.port );
    return TRUE;
}

void RTMP_EnableWrite(RTMP *r){
    r->Link.protocol |= RTMP_FEATURE_WRITE;
}


static int try_connect(struct rtmp_stream *stream){

    //delete this and just add the path

    if (dstr_is_empty(&stream->path)) {

        LOGI("try_connect","URL is empty");
        return OBS_OUTPUT_BAD_PATH;
    }


    LOGI("try_connect","Connecting to RTMP URL %s...", stream->path.array);

    // free any existing RTMP TLS context
    //I will comment this out now, I this this in only needed when restarting
   // RTMP_TLS_Free(&stream->rtmp);


    RTMP_Init(&stream->rtmp);
//
    if (!RTMP_SetupURL(&stream->rtmp, stream->path.array)){
        return OBS_OUTPUT_BAD_PATH;
    }

    RTMP_EnableWrite(&stream->rtmp);

//    dstr_copy(&stream->encoder_name, "FMLE/3.0 (compatible; FMSc/1.0)");
//
//    set_rtmp_dstr(&stream->rtmp.Link.pubUser, &stream->username);
//    set_rtmp_dstr(&stream->rtmp.Link.pubPasswd, &stream->password);
//    set_rtmp_dstr(&stream->rtmp.Link.flashVer, &stream->encoder_name);
//    stream->rtmp.Link.swfUrl = stream->rtmp.Link.tcUrl;
//
//    if (dstr_is_empty(&stream->bind_ip) || dstr_cmp(&stream->bind_ip, "default") == 0) {
//        memset(&stream->rtmp.m_bindIP, 0, sizeof(stream->rtmp.m_bindIP));
//    } else {
//        bool success = netif_str_to_addr(&stream->rtmp.m_bindIP.addr, &stream->rtmp.m_bindIP.addrLen,
//                                         stream->bind_ip.array);
//        if (success) {
//            int len = stream->rtmp.m_bindIP.addrLen;
//            bool ipv6 = len == sizeof(struct sockaddr_in6);
//            info("Binding to IPv%d", ipv6 ? 6 : 4);
//        }
//    }
//
//    // Only use the IPv4 / IPv6 hint if a binding address isn't specified.
//    if (stream->rtmp.m_bindIP.addrLen == 0)
//        stream->rtmp.m_bindIP.addrLen = stream->addrlen_hint;
//
//    RTMP_AddStream(&stream->rtmp, stream->key.array);
//
//    stream->rtmp.m_outChunkSize = 4096;
//    stream->rtmp.m_bSendChunkSizeInfo = true;
//    stream->rtmp.m_bUseNagle = true;
//
//#ifdef _WIN32
//    win32_log_interface_type(stream);
//#endif
//
//    if (!RTMP_Connect(&stream->rtmp, NULL)) {
//        set_output_error(stream);
//        return OBS_OUTPUT_CONNECT_FAILED;
//    }
//
//    if (!RTMP_ConnectStream(&stream->rtmp, 0))
//        return OBS_OUTPUT_INVALID_STREAM;
//
//    char ip_address[INET6_ADDRSTRLEN] = {0};
//    netif_addr_to_str(&stream->rtmp.m_sb.sb_addr, ip_address, INET6_ADDRSTRLEN);
//    info("Connection to %s (%s) successful", stream->path.array, ip_address);
// todo:
//    return init_send(stream); //this should be the actual return value

    return 55; //this should be removed

}




//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,jobject thiz,jstring url_,jboolean is_publish_mode,jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {


    rtmp_stream *stream = new rtmp_stream();
    char URL[] = "rtmps://ingest.global-contribute.live-video.net:443/app/";

    stream->path = dstr{URL,strlen(URL),3};

    int testing =try_connect(stream);
    LOGI("FINISHEDSETUP","try_connect() -> %d",testing );


    return -1;
}


