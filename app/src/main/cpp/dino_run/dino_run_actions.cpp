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


Actions::Actions() {
    startJump = false;
    hitTop = false;
    velocity = 0.0f;

}

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
