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

#define LOG_TAG "DINORUN"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)



//todo: READ UP ON POINTERS
TransformShader* shaders = new TransformShader();




// This should probably be its own class
enum GameStatus { init, start, stop };
GameStatus gameValue = init;

void removeStartUI(JNIEnv *env){
    gameValue = start;
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateRemoveStartGameFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }



}
void showGameOverUI(JNIEnv *env){
    gameValue = stop;
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateShowGameOverFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }
}


void resetSquare(std::vector<GLfloat>& vertices){

    //this is a reset on the y-values
    vertices[1] = -0.0375f;
    vertices[3] =-0.0375f;
    vertices[5] =0.0375f;
    vertices[7] = -0.0375f;
    vertices[9] = 0.0375f;
    vertices[11] =0.0375f;

}

bool startJump = false;
bool hitTop = false;
//todo: velocity is causing some deformation issues
// by I have created a temporary fix with resetSquare()
float velocity = 0.0f;

void jump(std::vector<GLfloat>& vertices) {
    float highestPosition = 0.4f;
    float lowestPosition = -0.0375f;

    // highest and lowest points on the square
    float verticesHighPoint = vertices[5]; // top-right Y position
    float verticesLowPoint = vertices[1];  // bottom-left Y position




    if (startJump) {
        if (!hitTop) {

            if (verticesHighPoint < highestPosition) {
                float distanceToTop = highestPosition - verticesHighPoint;

                velocity += -0.0025f * distanceToTop; // Loss of momentum as it gets closer
                // Apply the movement to all vertices
                for (int i = 1; i <= 12; i += 2) {
                    if (vertices[i] < highestPosition) {
                        vertices[i] += 0.025f + velocity;  // Apply velocity
                    } else {
                        vertices[i] = highestPosition; // Stop at the highest position
                    }
                }

            } else {
                velocity=0.0f;
                hitTop = true;
            }
        } else {
            // Simulate downward acceleration


            if (verticesLowPoint > lowestPosition) {
                for (int i = 1; i <= 12; i += 2) {

                    if (vertices[i] > lowestPosition) {
                        // velocity += -0.0001f;
                        vertices[i] += (-0.035f); // Move down
                    } else {
                        vertices[i] = lowestPosition; // Stop at lowest position
                    }
                }
            } else {


                hitTop = false;
                startJump = false;
                velocity =0.0f;
                resetSquare(vertices); // this is preventing the positioning issue that needs to be tackled
            }
        }

    }
}

void resetSecondSquare(std::vector<GLfloat>& vertices){
    vertices[12] = 0.85f;
    vertices[14] =1.0f;
    vertices[16] =1.0f;
    vertices[18] = 0.85f;
    vertices[20] = 1.0f;
    vertices[22] =0.85f;
}
static JavaVM *javaVM = nullptr;


// Store JVM reference when the game initializes
extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    return JNI_VERSION_1_6;
}

//todo: we should be able to just pass in a JNIEnv *env instance
//by just passing it to moveSecondSquare()
void updateTextFromNative(const char *message) {
    if (!javaVM) return;


    //----------IGNORE BELOW------------
    JNIEnv *env;
    bool didAttach = false;

    // Attach current thread if needed
    if (javaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        javaVM->AttachCurrentThread(&env, nullptr);
        didAttach = true;
    }
    //----------IGNORE ABOVE------------


    //todo: need to figure out more about this
        jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
        jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateTextFromNative", "(Ljava/lang/String;)V");

        if (updateTextMethod) {

            jstring jMessage = env->NewStringUTF(message);
            env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod, jMessage);
            env->DeleteLocalRef(jMessage);
        }



    if (didAttach) {
        javaVM->DetachCurrentThread();
    }
}

void showSpeedIncrease(JNIEnv *env){
    LOGI("showSpeedIncreaseTest",  "INCREASE THE SPEED");
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateOnSpeedIncreaseFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }


}
float secondSquareMovementSpeed = -0.02f;
int successfulJumps = 0;


void moveSecondSquare(std::vector<GLfloat>& vertices,JNIEnv *env){
    //these are the x-axis boudaries for the second square
    float rightBoundarySquareOne = vertices[14];
    float leftBoundarySquareOne = vertices[12];
    float topBoundarySquareOne = vertices[5];
    float bottomBoundarySquareOne = vertices[3];

    //boundary for the second box
    float rightBoundarySquareTwo = vertices[2];
    float leftBoundarySquareTwo = vertices[0];
    float topBoundarySquareTwo = vertices[17];
    float bottomBoundarySquareTwo = vertices[15];

    //if(secondFarthestRight<=farthestLeft)
    // Corrected hit detection logic (checks for overlap)
    if (!(rightBoundarySquareOne < leftBoundarySquareTwo || leftBoundarySquareOne > rightBoundarySquareTwo)) {
       // LOGI("farthestLeftTesting", "HIT!!!! RESET");

        if (!(topBoundarySquareOne < bottomBoundarySquareTwo || bottomBoundarySquareOne > topBoundarySquareTwo)){
            LOGI("farthestLeftTesting", "Y-RANGE HIT");
            successfulJumps=0;
            secondSquareMovementSpeed = -0.02f;
            updateTextFromNative("HIT");
            //todo: this needs to set gamestatus to over and show the game overUI
            showGameOverUI(env);
            resetSecondSquare(vertices);

            return;
        }

    }

    if(rightBoundarySquareOne <= -1){
        LOGI("farthestLeftTesting", "off screen");
        resetSecondSquare(vertices);
        successfulJumps+=1;
  LOGI("speedUpdateTesting", "increase speed--->%d",(successfulJumps%5 ==0));
  if((successfulJumps%5 ==0)){
      showSpeedIncrease(env);
      secondSquareMovementSpeed += -0.0025f;
  }

    }else{
        for(int i =12; i <23; i +=2){
            vertices[i] += (secondSquareMovementSpeed);
        }

    }



}

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

    shaders->setupGraphics(width, height);

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
//                for(int i =0; i<24;i++){
//                    LOGI("horizontalAspectRatio", "verticie -->%f",squareVertices[i]);
//                }
//                horizontalAspectRatio=0;
//            }else{
//                if(horizontalAspectRatio == width/height){
//                    //this is the weird second call that I am ignoring
//                    LOGI("horizontalAspectRatio", "horizontalAspectRatio == width/height");
//                }else{
//                    horizontalAspectRatio=  width / height;
//                    if(horizontalAspectRatio ==0){
//                        LOGI("horizontalAspectRatio", "VERTICAL UPDATE");
//                        //todo: update the verticies to the
//                    }else{
//                        LOGI("horizontalAspectRatio", "HORIZONTAL UPDATE");
//                        updateVerticesForAspectRatio(squareVertices,horizontalAspectRatio);
//                    }
//
//                }
//
//
//            }
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
    jump(shaders->getSquareVertices());
    LOGI("JUMPINGCHECK", "STEP!!!!!!!!!");

    switch(gameValue){
        case init  : break;
        case start: moveSecondSquare(shaders->getSquareVertices(),env); break;
        case stop :break;
    }

    shaders->renderFrame();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_jump(JNIEnv *env,jobject thiz) {
    startJump = true;
    LOGI("JUMPINGCHECK", "JUMP");
    switch(gameValue){
        case init  : removeStartUI(env);   break;
        case start: break;
        case stop : removeStartUI(env); break;
    }



}
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_triggerUpdate(JNIEnv *env,
//                                                                                 jobject thiz) {
//    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
//
//    if (dinoRunJNIClass == nullptr) {
//        return;
//    }
//
//    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateTextFromNative", "(Ljava/lang/String;)V");
//
//    if (updateTextMethod == nullptr) {
//        return;
//    }
//
//    jstring newText = env->NewStringUTF("MEATBALL");
//    env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod, newText);
//    env->DeleteLocalRef(newText);
//}

