#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "rtmp_client.h"




#define RTMP_DEFAULT_CHUNKSIZE	128

#define OFF(x)	offsetof(struct RTMP,x)


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
void AMF_AddProp(AMFObject *obj, const AMFObjectProperty *prop){
    if (!(obj->o_num & 0x0f))
    obj->o_props =
            static_cast<AMFObjectProperty *>(realloc(obj->o_props, (obj->o_num + 16) *
                                                                   sizeof(AMFObjectProperty)));
    memcpy(&obj->o_props[obj->o_num++], prop, sizeof(AMFObjectProperty));
}
static void RTMP_OptUsage(){
    int i;

    LOGI("RTMP_OptUsage", "Valid RTMP options are:");
    for (i=0; options[i].name.av_len; i++) {
        LOGI("RTMP_OptUsage", "%10s %-7s  %s\n", options[i].name.av_val,
             optinfo[options[i].otype], options[i].use);
    }
}
static int
parseAMF(AMFObject *obj, AVal *av, int *depth)
{
    AMFObjectProperty prop = {{0,0}};
    int i;
    char *p, *arg = const_cast<char *>(av->av_val);

    if (arg[1] == ':')
    {
        p = (char *)arg+2;
        switch(arg[0])
        {
            case 'B':
                prop.p_type = AMF_BOOLEAN;
                prop.p_vu.p_number = atoi(p);
                break;
            case 'S':
                prop.p_type = AMF_STRING;
                prop.p_vu.p_aval.av_val = p;
                prop.p_vu.p_aval.av_len = av->av_len - (p-arg);
                break;
            case 'N':
                prop.p_type = AMF_NUMBER;
                prop.p_vu.p_number = strtod(p, NULL);
                break;
            case 'Z':
                prop.p_type = AMF_NULL;
                break;
            case 'O':
                i = atoi(p);
                if (i)
                {
                    prop.p_type = AMF_OBJECT;
                }
                else
                {
                    (*depth)--;
                    return 0;
                }
                break;
            default:
                return -1;
        }
    }
    else if (arg[2] == ':' && arg[0] == 'N')
    {
        p = strchr(arg+3, ':');
        if (!p || !*depth)
            return -1;
        prop.p_name.av_val = (char *)arg+3;
        prop.p_name.av_len = p - (arg+3);

        p++;
        switch(arg[1])
        {
            case 'B':
                prop.p_type = AMF_BOOLEAN;
                prop.p_vu.p_number = atoi(p);
                break;
            case 'S':
                prop.p_type = AMF_STRING;
                prop.p_vu.p_aval.av_val = p;
                prop.p_vu.p_aval.av_len = av->av_len - (p-arg);
                break;
            case 'N':
                prop.p_type = AMF_NUMBER;
                prop.p_vu.p_number = strtod(p, NULL);
                break;
            case 'O':
                prop.p_type = AMF_OBJECT;
                break;
            default:
                return -1;
        }
    }
    else
        return -1;

    if (*depth)
    {
        AMFObject *o2;
        for (i=0; i<*depth; i++)
        {
            o2 = &obj->o_props[obj->o_num-1].p_vu.p_object;
            obj = o2;
        }
    }
    AMF_AddProp(obj, &prop);
    if (prop.p_type == AMF_OBJECT)
        (*depth)++;
    return 0;
}
int RTMP_SetOpt(RTMP *r, const AVal *opt, AVal *arg)
{
    int i;
    void *v;

    for (i=0; options[i].name.av_len; i++) {
        if (opt->av_len != options[i].name.av_len) continue;
        if (strcasecmp(opt->av_val, options[i].name.av_val)) continue;
        v = (char *)r + options[i].off;
        switch(options[i].otype) {
            case OPT_STR: {
                AVal *aptr = static_cast<AVal *>(v);
                *aptr = *arg; }
                break;
            case OPT_INT: {
                long l = strtol(arg->av_val, NULL, 0);
                *(int *)v = l; }
                break;
            case OPT_BOOL: {
                int j, fl;
                fl = *(int *)v;
                for (j=0; truth[j].av_len; j++) {
                    if (arg->av_len != truth[j].av_len) continue;
                    if (strcasecmp(arg->av_val, truth[j].av_val)) continue;
                    fl |= options[i].omisc; break; }
                *(int *)v = fl;
            }
                break;
            case OPT_CONN:
                if (parseAMF(&r->Link.extras, arg, &r->Link.edepth)) {
                    return RTMP_ERROR_UNKNOWN_RTMP_AMF_TYPE;
                }
                break;
        }
        break;
    }
    if (!options[i].name.av_len) {

        LOGI("RTMP_SetOpt", "Unknown option %s", opt->av_val);
        RTMP_OptUsage();
        return RTMP_ERROR_UNKNOWN_RTMP_OPTION;
    }

    return RTMP_SUCCESS;
}

RTMPResult RTMP_SetupURL(RTMP *r,  char *url){
    AVal opt, arg;
    char *p1, *p2, *ptr = strchr(url, ' ');
    int ret, len;
    unsigned int port = 0;



    len = strlen(url);
    ret = RTMP_ParseURL(url, &r->Link.protocol, &r->Link.hostname,
                        &port, &r->Link.playpath0, &r->Link.app);
    if (ret != RTMP_SUCCESS)
    {
        return (RTMPResult)ret;
    }
    r->Link.port = port;
    r->Link.playpath = r->Link.playpath0;

    //the while loop and new variables
    while (ptr) {
        *ptr++ = '\0';
        p1 = ptr;
        p2 = strchr(p1, '=');
        if (!p2)
            break;
        opt.av_val = p1;
        opt.av_len = p2 - p1;
        *p2++ = '\0';
        arg.av_val = p2;
        ptr = strchr(p2, ' ');
        if (ptr) {
            *ptr = '\0';
            arg.av_len = ptr - p2;
            /* skip repeated spaces */
            while(ptr[1] == ' ')
                *ptr++ = '\0';
        } else {
            arg.av_len = strlen(p2);
        }

        /* unescape */
        port = arg.av_len;
        for (p1=p2; port >0;) {
            if (*p1 == '\\') {
                unsigned int c;
                if (port < 3)
                {
                    return RTMP_ERROR_URL_INCORRECT_PORT;
                }
                sscanf(p1+1, "%02x", &c);
                *p2++ = c;
                port -= 3;
                p1 += 3;
            } else {
                *p2++ = *p1++;
                port--;
            }
        }
        arg.av_len = p2 - arg.av_val;

        ret = RTMP_SetOpt(r, &opt, &arg);
        if (ret != RTMP_SUCCESS)
            return (RTMPResult) ret;
    }
    return RTMP_SUCCESS;

}



//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,jobject thiz,jstring url_,jboolean is_publish_mode,jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {

    RTMP *rtmp = new RTMP();
    RTMP_Init(rtmp);
    const char* url = env->GetStringUTFChars(url_, nullptr);

    rtmp->Link.receiveTimeoutInMs = receive_timeout_in_ms;
    rtmp->Link.sendTimeoutInMs = send_timeout_in_ms;

    RTMPResult ret = RTMP_SetupURL(rtmp, "rtmps://ingest.global-contribute.live-video.net:443/app/");


    return (int) ret;
}


