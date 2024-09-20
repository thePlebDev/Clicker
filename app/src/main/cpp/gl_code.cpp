//
// Created by thePlebDev on 2024-09-20.
//


#include <jni.h>

#include <android/log.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-lib", __VA_ARGS__))


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeLoading_init(JNIEnv *env, jobject thiz) {
   // LOGI("Hello From the Native Side!!");
   // std::string hello = "Hello from C++";
    LOGI("int %s,",  "--------------THIS IS A LOG FROM THE NATIVE SIDE ----------------------");

}