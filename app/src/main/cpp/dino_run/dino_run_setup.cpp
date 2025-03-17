//
// Created by Tristan on 2025-03-17.
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

//todo: MOVE THIS IMPLEMENTATION TO ITS OWN FILE CALLED SETUP
TransformShader::TransformShader() {
    m_glVertexShader ="attribute vec4 vPosition;\n"
                      "void main()\n"
                      "{\n"
                      "  gl_Position = vPosition;\n"
                      "}\n";

    m_glFragmentShader ="precision mediump float;\n"
                        "void main()\n"
                        "{\n"
                        "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n"
                        "}\n";




}

TransformShader::~TransformShader() {

}








