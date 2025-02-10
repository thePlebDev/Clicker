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
#define GetSockError() errno


#include "rtmp_client.h"
RTMP_LogLevel RTMP_debuglevel = RTMP_LOGERROR;



#define RTMP_DEFAULT_CHUNKSIZE	128

#define OFF(x)	offsetof(struct RTMP,x)
#define TRUE	1
#define FALSE	0
#undef closesocket
#define closesocket(s)	close(s)


int RTMP_ctrlC;

static const char *RTMPT_cmds[] = {
        "open",
        "send",
        "idle",
        "close"
};
#define SAVC(x)	static const AVal av_##x = AVC(#x)


//todo: read up on arrays
const char RTMPProtocolStringsLower[][7] = {
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
static const int packetSize[] = { 12, 8, 4, 1 };





void RTMP_Init(RTMP *r){


    //memset(r, 0, sizeof(RTMP));
    r->m_sb.sb_socket = -1;
    r->m_inChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_outChunkSize = RTMP_DEFAULT_CHUNKSIZE;
    r->m_nBufferMS = 30000;
    r->m_nClientBW = 2500000;
    r->m_nClientBW2 = 2;
    r->m_nServerBW = 2500000;
    r->m_fAudioCodecs = 3191.0;
    r->m_fVideoCodecs = 252.0;
    r->Link.receiveTimeoutInMs = 10000;
    r->Link.swfAge = 30;
}

enum { OPT_STR=0, OPT_INT, OPT_BOOL, OPT_CONN };
#define AVC(str) {str, sizeof(str) - 1}




static const char hexdig[] = "0123456789abcdef";
static const AVal truth[] = {
        AVC("1"),
        AVC("on"),
        AVC("yes"),
        AVC("true"),
        {0, 0}
};
static const char *optinfo[] = {
        "string", "integer", "boolean", "AMF" };
static struct urlopt {
    AVal name;
    off_t off;
    int otype;
    int omisc;
    const char *use;
} options[] = {
        { AVC("socks"),     OFF(Link.sockshost),     OPT_STR, 0,
                "Use the specified SOCKS proxy" },
        { AVC("app"),       OFF(Link.app),           OPT_STR, 0,
                "Name of target app on server" },
        { AVC("tcUrl"),     OFF(Link.tcUrl),         OPT_STR, 0,
                "URL to played stream" },
        { AVC("pageUrl"),   OFF(Link.pageUrl),       OPT_STR, 0,
                "URL of played media's web page" },
        { AVC("swfUrl"),    OFF(Link.swfUrl),        OPT_STR, 0,
                "URL to player SWF file" },
        { AVC("flashver"),  OFF(Link.flashVer),      OPT_STR, 0,
                "Flash version string (default )" },
        { AVC("conn"),      OFF(Link.extras),        OPT_CONN, 0,
                "Append arbitrary AMF data to Connect message" },
        { AVC("playpath"),  OFF(Link.playpath),      OPT_STR, 0,
                "Path to target media on server" },
        { AVC("playlist"),  OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_PLST,
                "Set playlist before play command" },
        { AVC("live"),      OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_LIVE,
                "Stream is live, no seeking possible" },
        { AVC("subscribe"), OFF(Link.subscribepath), OPT_STR, 0,
                "Stream to subscribe to" },
        { AVC("jtv"), OFF(Link.usherToken),          OPT_STR, 0,
                "Justin.tv authentication token" },
        { AVC("token"),     OFF(Link.token),	       OPT_STR, 0,
                "Key for SecureToken response" },
        { AVC("swfVfy"),    OFF(Link.lFlags),        OPT_BOOL, RTMP_LF_SWFV,
                "Perform SWF Verification" },
        { AVC("swfAge"),    OFF(Link.swfAge),        OPT_INT, 0,
                "Number of days to use cached SWF hash" },
        { AVC("start"),     OFF(Link.seekTime),      OPT_INT, 0,
                "Stream start position in milliseconds" },
        { AVC("stop"),      OFF(Link.stopTime),      OPT_INT, 0,
                "Stream stop position in milliseconds" },
        { AVC("buffer"),    OFF(m_nBufferMS),        OPT_INT, 0,
                "Buffer time in milliseconds" },
        { AVC("timeout"),   OFF(Link.receiveTimeoutInMs),       OPT_INT, 0,
                "Session timeout in seconds" },
        { AVC("pubUser"),   OFF(Link.pubUser),       OPT_STR, 0,
                "Publisher username" },
        { AVC("pubPasswd"), OFF(Link.pubPasswd),     OPT_STR, 0,
                "Publisher password" },
        { {NULL,0}, 0, 0}
};
SAVC(FCUnpublish);
SAVC(deleteStream);





//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,jobject thiz,jstring url_,jboolean is_publish_mode,jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {




    return -1;
}


