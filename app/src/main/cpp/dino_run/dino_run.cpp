//
// Created by Tristan on 2025-03-11.
//

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include "dino_run.h"
#include "dino_run_setup.h"
#include "dino_run_actions.h"

#define LOG_TAG "DINORUN"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)



//todo: these two should be part of a larger GAME-OBJECT
TransformShader* shaders = new TransformShader();
Actions* actions = new Actions(shaders);




extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_init(JNIEnv *env, jobject thiz,jint width, jint height) {
    LOGI("APSECTrATIOtESTINGaGAIN", "INIT");
    float aspectRatio = (float)width / (float)height;
    LOGI("APSECTrATIOtESTINGaGAIN", "ratio -->%f",aspectRatio);

    shaders->setupGraphics(width, height);

    shaders->addToVector(aspectRatio);

}






//need a function called set to call `renderFrame();`
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_step(JNIEnv *env, jobject thiz) {
    std::vector<GLfloat>& vertices = shaders->getSquareVertices();
    actions->jump(shaders->getSquareVertices());
    LOGI("JUMPINGCHECK", "STEP!!!!!!!!!");

    switch(actions->getGameValue()){
        case init  : break;
        case start: actions->moveSecondSquare(shaders->getSquareVertices(),env); break;
        case stop :break;
    }

    shaders->renderFrame();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_jump(JNIEnv *env,jobject thiz) {
    actions->setStartJumpTrue();
    LOGI("JUMPINGCHECK", "JUMP");
    switch(actions->getGameValue()){
        case init  : actions->removeStartUI(env);   break;
        case start: break;
        case stop : actions->removeStartUI(env); break;
    }



}


