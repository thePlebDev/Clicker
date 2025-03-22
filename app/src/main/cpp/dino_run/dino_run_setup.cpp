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

GLuint TransformShader::createProgram(std::string vertexSource, std::string fragmentSource){
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    if (!vertexShader)
    {
        return 0;
    }
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    if (!fragmentShader)
    {
        return 0;
    }
    GLuint program = glCreateProgram();
    if (program)
    {
        glAttachShader(program , vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program , GL_LINK_STATUS, &linkStatus);
        if( linkStatus != GL_TRUE)
        {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength)
            {
                char* buf = (char*) malloc(bufLength);
                if (buf)
                {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOGE("Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

GLuint TransformShader::loadShader(GLenum shaderType, std::string shaderSource) {
    GLuint shader = glCreateShader(shaderType);
    if (shader){
        const GLchar* pCode = shaderSource.c_str();
        GLint length = shaderSource.length();

        glShaderSource(shader, 1, &pCode, &length);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled)
        {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen)
            {
                char * buf = (char*) malloc(infoLen);
                if (buf)
                {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("Could not Compile Shader %d:\n%s\n", shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

bool TransformShader::setupGraphics(int w, int h) {

    simpleTriangleProgram = createProgram(m_glVertexShader, m_glFragmentShader);

    if (!simpleTriangleProgram)
    {
        LOGE ("Could not create program");
        return false;
    }
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    glViewport(0, 0, w, h);


    LOGI("setupGraphicsTesting","-----------------------END-----------------------------");
    return true;
}

void TransformShader::renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Clear screen with black
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    glUseProgram(simpleTriangleProgram); // Use our shader program

    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, getSquareVertices().data());



    glEnableVertexAttribArray(vPosition);

    glDrawArrays(GL_TRIANGLES, 0, 12); // Draw the two triangles that make up the square
    glDrawArrays(GL_TRIANGLE_FAN, 13, 18); // Draw the two triangles that make up the square


}
//TODO: technically this works for the horizontal reset
void TransformShader::aspectUpdate(float aspectRatio) {

   // LOGI("setupGraphicsTesting","ratio -->%f",aspectRatio);
    float newRatio = aspectRatio * 2.0f;
    LOGI("setupGraphicsTesting","ratio -->%f",newRatio);

    for(int i =0; i<m_squareVertices.size();i+=2){
        m_squareVertices[i]  /=newRatio;
       // LOGI("setupGraphicsTesting","setupGraphics -->%f",m_squareVertices[i]);
    }

}

void TransformShader::addToVector(float aspectRatio) {


    const int NUM_SEGMENTS = 16;
    const float RADIUS = 0.05f;
    GLfloat circleVertices[(NUM_SEGMENTS + 2) * 2];  // (x, y) pairs
    circleVertices[0] = 0.0f;  // Center X with horizontal offset
    circleVertices[1] = 0.0f;  // Center Y

    // Create the circle vertices
    for (int i = 0; i <= NUM_SEGMENTS; i++) {
        float theta = (2.0f * M_PI * i) / NUM_SEGMENTS;
        float x = (RADIUS * cosf(theta) / aspectRatio);  // Add horizontal offset
        float y = RADIUS * sinf(theta);  // Y remains unchanged
        circleVertices[(i + 1) * 2] = x;
        circleVertices[(i + 1) * 2 + 1] = y;
    }

    // Insert circleVertices into m_squareVertices
    m_squareVertices.insert(m_squareVertices.end(), circleVertices, circleVertices + (NUM_SEGMENTS + 2) * 2);
}








