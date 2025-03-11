//
// Created by Tristan on 2025-03-11.
//
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <GLES2/gl2.h>
#include "ping_pong_movement.h"


#define LOG_TAG "pingPong"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))

void moveTopPaddleXAxis(GLfloat *vertices, GLfloat x) {
    // Indices of X-coordinates for the top paddle
    int indices[] = {0, 3, 6, 9, 12, 15};

    // Get the paddle's half-width
    float paddleHalfWidth = (vertices[6] - vertices[0]) / 2.0f;

    // Compute current paddle center
    float currentCenterX = (vertices[0] + vertices[6]) / 2.0f;

    // Clamp x value to prevent going out of bounds
    float minX = -1.0f + paddleHalfWidth;  // Left boundary
    float maxX = 1.0f - paddleHalfWidth;   // Right boundary
    float clampedX = fmaxf(minX, fminf(x, maxX)); // Ensure x stays within bounds

    // Compute movement amount (delta)
    float deltaX = clampedX - currentCenterX;

    // Move the paddle without stretching
    for (int i = 0; i < 6; i++) {
        vertices[indices[i]] += deltaX;
    }
}
/**
 * moveBottomPaddleXAxis is moving the paddle acrosse the screen
 * */
void moveBottomPaddleXAxis(GLfloat *vertices, GLfloat x) {
    moveTopPaddleXAxis(
            vertices,(x)
    );
    // Indices of X-coordinates for the bottom paddle
    float paddleHalfWidth = (vertices[42] - vertices[36]) / 2.0f;
    float minX = -1.0f + paddleHalfWidth;
    float maxX = 1.0f - paddleHalfWidth;
    float clampedX = fmaxf(minX, fminf(x, maxX));
    int indices[] = {36, 39, 42, 45, 48, 51};

    // 36 and 42 are the left/right most boundaries
    float currentCenterX = (vertices[36] + vertices[42]) / 2.0f;

    float deltaX = clampedX - currentCenterX;
    for (int i = 0; i < 6; i++) {
        vertices[indices[i]] += deltaX;
    }

}


void resetBall(GLfloat *vertices){
    //x-values
    vertices[18] = 0.08f;
    vertices[21] = 0.08f;
    vertices[24]= -0.08f;
    vertices[27] = 0.08f;
    vertices[30] =-0.08f;
    vertices[33] =-0.08f;

    //y-values
    vertices[19] = 0.04f;
    vertices[22] = -0.04f;
    vertices[25] = 0.04f;
    vertices[28] = -0.04f;
    vertices[31] = -0.04f;
    vertices[34] = 0.04f;


}


bool topHit = false;
float dx = 0.007f;
float newDy = -0.886735f;

void moveBall(GLfloat *vertices, GLfloat dy) {
    LOGI("checkingTheValuesagain", "dy -->%f",dy );

    float newY = (newDy / 80) * -1;
    float heightBoundary = 1.0f;
    float paddleHeight = 0.05f;
    LOGI("sidehittesting", "dy -->%f",dy);

    float newPaddleHeightBoundary = heightBoundary-paddleHeight;
    float ballWidth = 0.16f;

    if (!topHit) {
        // Ball moving upwards
        if (vertices[19] < newPaddleHeightBoundary) {
            // Move the ball vertically
            for (int i = 19; i < 36; i += 3) {
                vertices[i] += newY;
            }


            for (int i = 18; i < 36; i += 3) {

                float newX = vertices[i] + dx;

                if ((newX + ballWidth/2) >= 1.0f || (newX - ballWidth/2) <= -1.0f) {
                    dx =dx*-1; // Reverse direction
                    LOGI("sidehittesting", "SIDE HIT! Reversing direction");
                    break; // Exit loop once a hit is detected
                }
            }

            //  horizontal movement after collision check
            for (int i = 18; i < 36; i += 3) {
                vertices[i] += dx;
            }
        } else {

            // SO THESE DEFINETLY WORK
            float ballTopLeft = vertices[18];
            float ballTopRight = vertices[24];
            float paddleTopLeft = vertices[9];
            float paddleTopRight = vertices[15];

            //literally the same logic, just flip the signs
            if ((ballTopRight <= paddleTopLeft && ballTopRight >= paddleTopRight) ||
                (ballTopLeft <= paddleTopLeft && ballTopLeft >= paddleTopRight)) {
                LOGI("tophitTesting", "HIT!");
                topHit = true;
            }else{
                LOGI("tophitTesting", "MISS");
                resetBall(vertices);
            }


        }
    } else {
        // Ball moving downwards
        //todo: THE MAGIC NUMBER OF -0.87 NEEDS TO BE CHANGED EVENTUALLY
        if (vertices[19] > -0.87) {
            for (int i = 19; i < 36; i += 3) {
                vertices[i] += (newY * -1);
            }
        } else {

            //bottom sections of the ball
            float ballLeft = vertices[33];
            float ballRight = vertices[27];

            // boundaries of the paddle
            float paddleLeft = vertices[36];
            float paddleRight = vertices[42];


            if ((ballRight >= paddleLeft && ballRight <= paddleRight) ||  // Right edge of ball inside paddle
                (ballLeft >= paddleLeft && ballLeft <= paddleRight)) {    // Left edge of ball inside paddle

                LOGI("bottomhittesting", "HIT! Ball bounced");
                topHit = false; // move ball up
            } else {
                resetBall(vertices); // the game is over, try again
            }
        }
    }
}