#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

//Now I need to make the JNI file
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_clicker_presentation_selfStreaming_RTMPNativeClient_nativeOpen(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jstring url,
                                                                                jboolean is_publish_mode,
                                                                                jlong rtmp_pointer,
                                                                                jint send_timeout_in_ms,
                                                                                jint receive_timeout_in_ms) {
    return -1;
}