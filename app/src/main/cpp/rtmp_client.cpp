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
int RTMP_ParseURL(const char *url, int *protocol, AVal *host, unsigned int *port,
                  AVal *playpath, AVal *app)
{
    return RTMP_ERROR_UNKNOWN_RTMP_AMF_TYPE;
}
RTMPResult RTMP_SetupURL(RTMP *r, const char *url){
    AVal opt, arg;
    char *p1, *p2;
    int ret, len;
    unsigned int port = 0;



    len = strlen(url);
    ret = RTMP_ParseURL(url, &r->Link.protocol, &r->Link.hostname,
                        &port, &r->Link.playpath0, &r->Link.app);
    if (ret != RTMP_SUCCESS)
    {
        return (RTMPResult) ret;
    }
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

    RTMPResult ret = RTMP_SetupURL(rtmp, url);


    return (int) ret;
}


