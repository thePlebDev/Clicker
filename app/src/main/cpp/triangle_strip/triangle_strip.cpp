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

#define LOG_TAG "TRIANGLESTRIP"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)




static const char glVertexShader[] =
        "attribute vec4 vPosition;\n"
        "uniform mat4 uRotationMatrix;\n"
        "void main()\n"
        "{\n"
        "  gl_Position = vPosition;\n"
        "}\n";




static const char glFragmentShader[] =
        "precision mediump float;\n"
        "void main()\n"
        "{\n"
        "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n"
        "}\n";


GLuint loadShader(GLenum shaderType, const char* shaderSource)
{
    GLuint shader = glCreateShader(shaderType);
    if (shader)
    {
        glShaderSource(shader, 1, &shaderSource, NULL);
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

GLuint createProgram(const char* vertexSource, const char * fragmentSource)
{
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
GLuint simpleTriangleProgram;
GLuint vPosition;
GLuint uRotationMatrix;
float angle = 0.0f;
bool setupGraphics(int w, int h)
{
    simpleTriangleProgram = createProgram(glVertexShader, glFragmentShader);
    if (!simpleTriangleProgram)
    {
        LOGE ("Could not create program");
        return false;
    }
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    // Get the uniform location once after linking the program
    uRotationMatrix = glGetUniformLocation(simpleTriangleProgram, "uRotationMatrix");
    glViewport(0, 0, w, h);
    return true;
}


const int NUM_SEGMENTS = 16;
const float RADIUS = 0.2f;
GLfloat circleVertices[(NUM_SEGMENTS + 2) * 2];  // (x, y) pairs

void generateCircleVerticesAspectRatioAdjusted(float radius, int numSegments, float aspectRatio, float offsetX) {
    circleVertices[0] = 0.0f;  // Center X with horizontal offset
    circleVertices[1] = 0.0f;           // Center Y

    for (int i = 0; i <= numSegments; i++) {
        float theta = (2.0f * M_PI * i) / numSegments;
        float x = (radius * cosf(theta) / aspectRatio);  // Add horizontal offset
        float y = radius * sinf(theta);  // Y remains unchanged
        circleVertices[(i + 1) * 2] = x;
        circleVertices[(i + 1) * 2 + 1] = y;
    }
}

float globalWidth = 0.0f;
float globalHeight = 0.0f;
float horizontalOffset = 0.00f;


void renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    glUseProgram(simpleTriangleProgram);

     horizontalOffset -= 0.004f;  // Adjust speed as needed
//    angle += 0.03f;  // Adjust speed as needed
//    if (angle > 2.0f * M_PI) angle -= 2.0f * M_PI;

    // Regenerate circle vertices with horizontal offset
    float aspectRatio = (float)globalWidth / (float)globalHeight;
//    GLfloat rotationMatrix[16] = {
//            cosf(angle),  0.0f, sinf(angle), 0.0f,
//            0.0f,         1.0f, 0.0f,        0.0f,
//            -sinf(angle), 0.0f, cosf(angle), 0.0f,
//            0.0f,         0.0f, 0.0f,        1.0f
//    };

    // Update the rotation angle for more speed
    generateCircleVerticesAspectRatioAdjusted(RADIUS, NUM_SEGMENTS, aspectRatio, horizontalOffset);

    // Pass matrix to shader
  //  glUniformMatrix4fv(uRotationMatrix, 1, GL_FALSE, rotationMatrix);
    // Bind vertex data and draw
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, circleVertices);
    glEnableVertexAttribArray(vPosition);
    glDrawArrays(GL_TRIANGLE_FAN, 0, NUM_SEGMENTS + 2);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_TriangleStripJNI_init(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jint width,
                                                                              jint height) {
    globalHeight = height;
    globalWidth = width;
    setupGraphics(width, height);

    float aspectRatio = (float)width / (float)height;

    LOGI("APSECTrATIOtESTINGaGAIN", "ratio -->%f",aspectRatio);
    generateCircleVerticesAspectRatioAdjusted(RADIUS, NUM_SEGMENTS,aspectRatio,0.0f);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_TriangleStripJNI_step(JNIEnv *env,
                                                                              jobject thiz) {
    renderFrame();
}