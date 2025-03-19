//
// Created by Tristan on 2025-03-19.
//
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <string>
#include <stdlib.h>
#include <vector>

#ifndef CLICKER_DINO_RUN_ACTIONS_H
#define CLICKER_DINO_RUN_ACTIONS_H

#endif //CLICKER_DINO_RUN_ACTIONS_H

enum GameStatus { init, start, stop };



class Actions {
private:
    bool startJump;
    bool hitTop;
    float velocity;
    void resetSquare(std::vector<GLfloat>& vertices);
    float secondSquareMovementSpeed = -0.02f;
    int successfulJumps = 0;
    GameStatus gameValue = init;

public:
    Actions();
    void jump(std::vector<GLfloat>& vertices);
    void setStartJumpTrue();
    void moveSecondSquare(std::vector<GLfloat>& vertices,JNIEnv *env);
    void updateTextFromNative(const char *message,JNIEnv *env);
    void showGameOverUI(JNIEnv *env);
    void resetSecondSquare(std::vector<GLfloat>& vertices);
    void showSpeedIncrease(JNIEnv *env);
    void removeStartUI(JNIEnv *env);

    //todo: this needs to be looked into
    GameStatus getGameValue() {
        return gameValue;
    }



};