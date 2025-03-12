//
// Created by Tristan on 2025-03-11.
//

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#define LOG_TAG "DINORUN"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
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
bool setupGraphics(int w, int h)
{
    simpleTriangleProgram = createProgram(glVertexShader, glFragmentShader);
    if (!simpleTriangleProgram)
    {
        LOGE ("Could not create program");
        return false;
    }
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    glViewport(0, 0, w, h);
    return true;
}

 GLfloat squareVertices[] = {
        //this triangle is actually the lowest
        -0.075f, -0.0375f,   // Bottom-left corner. this  Y POSITION will act as the lowest point
        0.075f, -0.0375f,    // Bottom-right corner
        0.075f,  0.0375f,    // Top-right corner (First triangle) //this Y POSITION will act as the highest point

        //this triangle is the top one
        -0.075f, -0.0375f,   // Bottom-left corner
        0.075f,  0.0375f,    // Top-right corner
        -0.075f,  0.0375f    // Top-left corner (Second triangle)
};

void renderFrame(){

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Clear screen with black
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    glUseProgram(simpleTriangleProgram); // Use our shader program

    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, squareVertices);
    glEnableVertexAttribArray(vPosition);

    glDrawArrays(GL_TRIANGLES, 0, 6); // Draw the two triangles that make up the square
}
bool startJump = false;
bool hitTop = false;


//todo: THERE IS A MINOR POSITIONING BUG  
void jump(GLfloat *vertices) {
    float highestPosition = 0.4f;
    float lowestPosition = -0.0375f;

    // highest and lowest points on the square
    float verticesHighPoint = vertices[5]; // top-right Y position
    float verticesLowPoint = vertices[1];  // bottom-left Y position

    if (startJump) {
        if (!hitTop) {

            if (verticesHighPoint < highestPosition) {
                for (int i = 1; i <= 12; i += 2) {
                    if (vertices[i] < highestPosition) {
                        vertices[i] += 0.025f; // Move up
                    } else {
                        vertices[i] = highestPosition; // Stop at highest position
                    }
                }
            } else {
                hitTop = true;
            }
        } else {

            if (verticesLowPoint > lowestPosition) {
                for (int i = 1; i <= 12; i += 2) {

                    if (vertices[i] > lowestPosition) {
                        vertices[i] += (-0.035f); // Move down
                    } else {
                        vertices[i] = lowestPosition; // Stop at lowest position
                    }
                }
            } else {
                hitTop = false;
                startJump = false;
            }
        }
    }
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_init(JNIEnv *env, jobject thiz,jint width, jint height) {
    setupGraphics(width, height);

}

//need a function called set to call `renderFrame();`
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_step(JNIEnv *env, jobject thiz) {
    jump(squareVertices);
    renderFrame();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_dinoRun_DinoRunJNI_jump(JNIEnv *env, jobject thiz) {
    startJump = true;

}