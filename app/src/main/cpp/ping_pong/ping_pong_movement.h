//
// Created by 14035 on 2025-03-11.
//

#ifndef CLICKER_PING_PONG_MOVEMENT_H
#define CLICKER_PING_PONG_MOVEMENT_H

#endif //CLICKER_PING_PONG_MOVEMENT_H
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>




void moveTopPaddleXAxis(GLfloat *vertices, GLfloat x);
void moveBottomPaddleXAxis(GLfloat *vertices, GLfloat x);
void resetBall(GLfloat *vertices);
void moveBall(GLfloat *vertices, GLfloat dy);