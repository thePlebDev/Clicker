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
    width = w;
    height = h;
    simpleTriangleProgram = createProgram(m_glVertexShader, m_glFragmentShader);
    if (!simpleTriangleProgram) {
        LOGE("Could not create program");
        return false;
    }
    float aspectRatio = (float)w / (float)h;
    LOGI("setupGraphicsTestingRatio", "aspectRatio--> %f",aspectRatio);

    if(w>h){//determine if portrait or landscape
        float scaleFactor = 0.5f; // reduction
        // width of the first square
        float squareWidth = (-0.650f - (-0.800f)) / aspectRatio * scaleFactor;

        // Shift the first square to the left boundary (-1.0)y
        float leftBoundary = -1.0f;
        float newLeftX = leftBoundary;
        float newRightX = leftBoundary + squareWidth;

        // Move the square 20% to the right
        float shiftAmount = 0.4f;
        newLeftX += shiftAmount;
        newRightX += shiftAmount;

        std::vector<GLfloat> newOneToEdit = {
                // First square shifted 20% to the right
                newLeftX, (-0.0375f), // Bottom-left vertex
                newRightX, (-0.0375f), // Bottom-right vertex
                newRightX, (0.0375f), // Top-right vertex
                newLeftX, (-0.0375f), // Bottom-left vertex (repeated for the second triangle)
                newRightX, (0.0375f), // Top-right vertex
                newLeftX, (0.0375f), // Top-left vertex

                // Second square not shifted
                (0.85f/aspectRatio) * scaleFactor, (-0.0375f), (1.0f/aspectRatio) * scaleFactor, (-0.0375f), (1.0f/aspectRatio) * scaleFactor, ( 0.0375f),
                (0.85f/aspectRatio) * scaleFactor, (-0.0375f), (1.0f/aspectRatio) * scaleFactor,  (0.0375f), ( 0.85f/aspectRatio) * scaleFactor,  (0.0375f)
        };
        m_squareVertices.clear();
        m_squareVertices.insert(m_squareVertices.end(), newOneToEdit.begin(), newOneToEdit.end());

    }else{
        // vertical display
        std::vector<GLfloat> newOneToEdit = {
                -0.800f, (-0.0375f) , -0.650f, (-0.0375f), -0.650f,  (0.0375f) ,
                -0.800f, (-0.0375f) , -0.650f,  (0.0375f) , -0.800f,  (0.0375f) ,
                0.85f, (-0.0375f) , 1.0f, (-0.0375f), 1.0f, ( 0.0375f) ,
                0.85f, (-0.0375f) , 1.0f,  (0.0375f), 0.85f,  (0.0375f)
        };
        m_squareVertices.clear();
        m_squareVertices.insert(m_squareVertices.end(), newOneToEdit.begin(), newOneToEdit.end());
    }


    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    glViewport(0, 0, w, h);

    // Set up orthographic projection matrix


    glUseProgram(simpleTriangleProgram);


    LOGI("setupGraphicsTesting", "-----------------------END-----------------------------");
    return true;
}

void TransformShader::renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Clear screen with black
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    glUseProgram(simpleTriangleProgram); // Use our shader program

    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, getSquareVertices().data());



    glEnableVertexAttribArray(vPosition);

    glDrawArrays(GL_TRIANGLES, 0, 12); // Draw the two triangles that make up the square
    if(showCoin){
        glDrawArrays(GL_TRIANGLE_FAN, 13, 18);
    }



}


void TransformShader::addToVector(float aspectRatio) {
    const int NUM_SEGMENTS = 16;
    const float RADIUS = 0.05f;
    const float SCREEN_RIGHT_EDGE = 1.0f;  // Assuming the screen width ranges from -1 to 1 in OpenGL coordinates
    GLfloat circleVertices[(NUM_SEGMENTS + 2) * 2];  // (x, y) pairs

    // Center the circle just beyond the right edge of the screen
    circleVertices[0] = SCREEN_RIGHT_EDGE + RADIUS;  // Center X
    circleVertices[1] = 0.0f;  // Center Y

    for (int i = 0; i <= NUM_SEGMENTS; i++) {
        float theta = (2.0f * M_PI * i) / NUM_SEGMENTS;
        float x = (RADIUS * cosf(theta) / aspectRatio) + (SCREEN_RIGHT_EDGE + RADIUS);  // Adjust for aspect ratio and shift right
        float y = RADIUS * sinf(theta) + 0.3f;  // Y remains unchanged
        circleVertices[(i + 1) * 2] = x;
        circleVertices[(i + 1) * 2 + 1] = y;
    }

    if (m_squareVertices.size() > 24) {
        LOGI("addToVectorTesting", "greater -->%u", m_squareVertices.size());
        LOGI("addToVectorTesting", "greater");
        m_squareVertices.erase(m_squareVertices.begin() + 24, m_squareVertices.end());
    }

    m_squareVertices.insert(m_squareVertices.end(), circleVertices, circleVertices + (NUM_SEGMENTS + 2) * 2);
}








