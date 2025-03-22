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
Actions* actions = new Actions();




//this is not working as I expected
void updateVerticesForAspectRatio(GLfloat* vertices, float aspectRatio) {
    for (int i = 0; i < 13; i += 2) { // Iterate over each pair (X, Y)
        vertices[i] /= 4.0f;  // Correct X scaling
      //  vertices[i + 1] *= 2.5f;     // Increase Y by 10%
    }
    //todo: this final half is being negated by the reset function
    for (int i = 12; i < 24; i += 2) { // Iterate over each pair (X, Y)
        vertices[i] /= 4.0f;  // Correct X scaling
        //  vertices[i + 1] *= 2.5f;     // Increase Y by 10%
    }
}

int horizontalAspectRatio = 99;
static int finalWidth = 0;
static int finalHeight = 0;


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_init(JNIEnv *env, jobject thiz,jint width, jint height) {
    LOGI("APSECTrATIOtESTINGaGAIN", "INIT");
    float aspectRatio = (float)width / (float)height;

    shaders->setupGraphics(width, height);

    shaders->addToVector(aspectRatio);
//    // Now this check will work as expected
//    if (width != finalWidth || height != finalHeight) {
//
//        if(width==finalWidth){
//            LOGI("horizontalAspectRatio", "SECOND CALL DO NOTHING");
//        }
//        else{
//            if(horizontalAspectRatio== 99){
//                //this is the initial call, so do nothing
//                // decreaseVerticesForAspectRatio(squareVertices,2.000000f);
//                LOGI("horizontalAspectRatio", "initial call");
//                //todo: JUST GET THE INITIAL VALUES AND ASSIGN THEM TO A GLOBAL VALUE
//
//                horizontalAspectRatio=0;
//            }else{
//                if(horizontalAspectRatio == width/height){
//                    //this is the weird second call that I am ignoring
//                    LOGI("horizontalAspectRatio", "horizontalAspectRatio == width/height");
//                }else{
//                    horizontalAspectRatio=  width / height;
//                    if(horizontalAspectRatio ==0){
//                        LOGI("horizontalAspectRatio", "VERTICAL UPDATE");
//                        //shaders->aspectUpdate(aspectRatio);
//                        //todo: update the verticies to the
//                    }else{
//                        LOGI("horizontalAspectRatio", "HORIZONTAL UPDATE");
//                        shaders->aspectUpdate(aspectRatio);
//
//                    }
//
//                }
//
//
//            }
//
//
//        }
//
//    }





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


