#include <jni.h>

//
// Created by Tristan on 2024-09-30.
//

#include <jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define LOG_TAG "libNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static const char glVertexShader[] =
        "attribute vec4 vPosition;\n"
        "void main()\n"
        "{\n"
        "  gl_Position = vPosition;\n"
        "}\n";

static const char glFragmentShader[] =
        "precision mediump float;\n"
        "void main()\n"
        "{\n"
        "  gl_FragColor = vec4(1.0, 0.0, 0.0, 0.5);  // Set opacity to 0.5 for transparency\n"
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
bool setupGraphics(int w, int h)
{
    simpleTriangleProgram = createProgram(glVertexShader, glFragmentShader);
    if (!simpleTriangleProgram)
    {
        LOGE ("Could not create program");
        return false;
    }
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");

    // Enable blending for transparency
//    glEnable(GL_BLEND);
//    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // Disable depth testing to ensure correct transparency blending
    glDisable(GL_DEPTH_TEST);

    // Set the viewport
    glViewport(0, 0, w, h);

    return true;
}

// Define vertices for the two triangles making up the rectangle
const GLfloat triangleVertices[] = {
        // Triangle 1
        -1.0f,  1.0f, // Top-left
        -1.0f, -1.0f, // Bottom-left
        1.0f, -1.0f, // Bottom-right

        // Triangle 2
        -1.0f,  1.0f, // Top-left (again)
        1.0f,  1.0f, // Top-right
        1.0f, -1.0f  // Bottom-right (again)
};

void renderFrame()
{
    // Set the clear color to be transparent
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  // Transparent background
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    // Use the shader program and draw the rectangle
    glUseProgram(simpleTriangleProgram);
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, triangleVertices);
    glEnableVertexAttribArray(vPosition);
    glDrawArrays(GL_TRIANGLES, 0, 6);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeBlurEffect_init(JNIEnv *env, jobject thiz,
                                                                    jint width, jint height) {
    // Initialize the graphics
    setupGraphics(width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeBlurEffect_step(JNIEnv *env, jobject thiz) {
    // Render the frame
    renderFrame();
}