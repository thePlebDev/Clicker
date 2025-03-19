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



class Actions {
private:
    bool startJump;
    bool hitTop;
    float velocity;
    void resetSquare(std::vector<GLfloat>& vertices);

public:
    Actions();
    void jump(std::vector<GLfloat>& vertices);
    void setStartJumpTrue();



};