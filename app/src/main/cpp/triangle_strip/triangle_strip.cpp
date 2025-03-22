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
        "  gl_Position = uRotationMatrix * vPosition;\n"
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
float time = 0.0f;  // Global time variable
float globalHeight = 0.0f;
float globalWidth = 0.0f;
bool setupGraphics(int w, int h) {
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

    // Initialize time
    time = 0.0f;

    return true;
}


const int NUM_SEGMENTS = 26;
const float RADIUS = 0.08f;
GLfloat circleVertices[(NUM_SEGMENTS + 2) * 2];  // (x, y) pairs


void generateCircleVerticesAspectRatioAdjusted(float radius, int numSegments, float aspectRatio, float time) {
    circleVertices[0] = 0.0f;  // Center X
    circleVertices[1] = 0.0f;  // Center Y

    for (int i = 0; i <= numSegments; i++) {
        float theta = (2.0f * M_PI * i) / numSegments;
        float x = radius * cosf(theta) / aspectRatio;  // Adjust x by aspect ratio
        float y = radius * sinf(theta) + 0.02f * sinf(time);  // Add up-and-down motion
        circleVertices[(i + 1) * 2] = x;
        circleVertices[(i + 1) * 2 + 1] = y;
    }
}




void renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    glUseProgram(simpleTriangleProgram);

    // Update the rotation angle
    angle += 0.03f;  // Adjust speed as needed
    if (angle > 2.0f * M_PI) angle -= 2.0f * M_PI;

    // Update time for up-and-down motion
    time += 0.05f;  // Adjust speed as needed
    if (time > 2.0f * M_PI) time -= 2.0f * M_PI;

    // Regenerate circle vertices with up-and-down motion
    float aspectRatio = (float)globalWidth / (float)globalHeight;  // Assuming width and height are global or accessible
    generateCircleVerticesAspectRatioAdjusted(RADIUS, NUM_SEGMENTS, aspectRatio, time);

    // Compute rotation matrix
    GLfloat rotationMatrix[16] = {
            cosf(angle),  0.0f, sinf(angle), 0.0f,
            0.0f,         1.0f, 0.0f,        0.0f,
            -sinf(angle), 0.0f, cosf(angle), 0.0f,
            0.0f,         0.0f, 0.0f,        1.0f
    };

    // Pass matrix to shader
    glUniformMatrix4fv(uRotationMatrix, 1, GL_FALSE, rotationMatrix);

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
    globalWidth = width;
    globalHeight = height;
    setupGraphics(width, height);

    //generateCircleVertices(RADIUS, NUM_SEGMENTS);

    float aspectRatio = (float)width / (float)height;

    LOGI("APSECTrATIOtESTINGaGAIN", "ratio -->%f",aspectRatio);
    generateCircleVerticesAspectRatioAdjusted(RADIUS, NUM_SEGMENTS,aspectRatio,time);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_TriangleStripJNI_step(JNIEnv *env,
                                                                              jobject thiz) {
    renderFrame();
}