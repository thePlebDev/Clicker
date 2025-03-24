//
// Created by Tristan on 2025-03-19.
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



void Actions::jump(std::vector<GLfloat> &vertices) {
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

void Actions::setStartJumpTrue() {
    startJump=true;
}

void Actions::resetSquare(std::vector<GLfloat> &vertices) {
    //this is a reset on the y-values
    vertices[1] = -0.0375f;
    vertices[3] =-0.0375f;
    vertices[5] =0.0375f;
    vertices[7] = -0.0375f;
    vertices[9] = 0.0375f;
    vertices[11] =0.0375f;

}

void Actions::moveSecondSquare(std::vector<GLfloat> &vertices, JNIEnv *env) {
    //these are the x-axis boudaries for the second square
    float rightBoundarySquareOne = vertices[14];
    float leftBoundarySquareOne = vertices[12];
    float topBoundarySquareOne = vertices[5];
    float bottomBoundarySquareOne = vertices[3];

    //boundary for the second box. THE BOX THAT JUMPS
    float rightBoundarySquareTwo = vertices[2];
    float leftBoundarySquareTwo = vertices[0];
    float topBoundarySquareTwo = vertices[17];
    float bottomBoundarySquareTwo = vertices[15];

    //if(secondFarthestRight<=farthestLeft)
    // Corrected hit detection logic (checks for overlap)
    if (!(rightBoundarySquareOne < leftBoundarySquareTwo || leftBoundarySquareOne > rightBoundarySquareTwo)) {
        // LOGI("farthestLeftTesting", "HIT!!!! RESET");

        //this triggering represents a hit
//        if (!(topBoundarySquareOne < bottomBoundarySquareTwo || bottomBoundarySquareOne > topBoundarySquareTwo)){
//            LOGI("farthestLeftTesting", "Y-RANGE HIT");
//            successfulJumps=0;
//            secondSquareMovementSpeed = -0.02f;
//            updateTextFromNative("HIT",env);
//            //todo: this needs to set gamestatus to over and show the game overUI
//            showGameOverUI(env);
//            resetSecondSquare(vertices);
//            setShowCoin(false);
//            resetCoin();
//
//            return;
//        }

    }


    if(getShowCoin()){
        float circleLeft   = vertices[42];   // Min X (θ = π, i=8 → index 24 + 2 + 16 = 42)
        float circleRight  = vertices[28];   // Max X (θ = 0, i=0 → index 24 + 2 = 26)
        float circleTop    = vertices[35];   // Max Y (θ = π/2, i=4 → index 24 + 11 = 35)
        float circleBottom = vertices[47];


        bool isYOverlap = (circleBottom < topBoundarySquareOne) ;
        //not sure why leftBoundarySquareTwo is registering the hit and not ONE but it works
        bool isXOverlap = !(circleRight < leftBoundarySquareTwo || circleLeft > rightBoundarySquareTwo);
        if(isXOverlap && isYOverlap){
            resetCoin();
        }
        for(int i = 24; i <vertices.size(); i+=2){
            vertices[i] += -0.01f;
            if(vertices[28]<=-1){
                //TODO: DON'T setShowCoin(false); YET, WE SHOULD GET A LOOP GOING FIRST
               // setShowCoin(false);
                resetCoin();

                break;
            }
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
            setShowCoin(true);

        }

    }else{
        for(int i =12; i <23; i +=2){
            vertices[i] += (secondSquareMovementSpeed);
        }

    }



}

void Actions::updateTextFromNative(const char *message, JNIEnv *env) {


    //todo: need to figure out more about this
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateTextFromNative", "(Ljava/lang/String;)V");

    if (updateTextMethod) {

        jstring jMessage = env->NewStringUTF(message);
        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod, jMessage);
        env->DeleteLocalRef(jMessage);
    }

}

void Actions::showGameOverUI(JNIEnv *env) {
    gameValue = stop;
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateShowGameOverFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }

}

void Actions::resetSecondSquare(std::vector<GLfloat> &vertices) {
    vertices[12] = 0.85f;
    vertices[14] =1.0f;
    vertices[16] =1.0f;
    vertices[18] = 0.85f;
    vertices[20] = 1.0f;
    vertices[22] =0.85f;

}

void Actions::showSpeedIncrease(JNIEnv *env) {
    LOGI("showSpeedIncreaseTest",  "INCREASE THE SPEED");
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateOnSpeedIncreaseFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }


}

void Actions::removeStartUI(JNIEnv *env) {
    gameValue = start;
    jclass dinoRunJNIClass = env->FindClass("com/example/clicker/presentation/minigames/dinoRun/DinoRunJNI");
    jmethodID updateTextMethod = env->GetStaticMethodID(dinoRunJNIClass, "updateRemoveStartGameFromNative",
                                                        "()V");

    if (updateTextMethod) {

        env->CallStaticVoidMethod(dinoRunJNIClass, updateTextMethod);

    }
}

Actions::Actions(TransformShader *shader) {
    transformShader = shader;
    startJump = false;
    hitTop = false;
    velocity = 0.0f;

}
