//
// Created by Tristan on 2024-09-30.
//
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>


// 1) CREATE A SINGLE RED TRIANGLE                       (DONE)
// 2) CREATE TO TRIANGLES AND HAVE THEM FORM A SQUARE    (DONE)
#define LOG_TAG "libgl2jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

float matrixDegreesToRadians(float degrees) {
    return degrees * (M_PI / 180.0f);
}

/**
 * matrixTranslate initializes a matrix. The matrix will look like this:
 *
 * [ 1, 0, 0, 0 ]
 * [ 0, 1, 0, 0 ]
 * [ 0, 0, 1, 0 ]
 * [ 0, 0, 0, 1 ]
 *
 * @param matrix pointer(holds the memory address) to a sequence of float values in memory
 *
 * */
void matrixIdentityFunction(float* matrix) {
    if (matrix == NULL) {
        return;  // Avoid null pointer dereference
    }
    matrix[0] = 1.0f;  matrix[1] = 0.0f;  matrix[2] = 0.0f;  matrix[3] = 0.0f;
    matrix[4] = 0.0f;  matrix[5] = 1.0f;  matrix[6] = 0.0f;  matrix[7] = 0.0f;
    matrix[8] = 0.0f;  matrix[9] = 0.0f;  matrix[10] = 1.0f; matrix[11] = 0.0f;
    matrix[12] = 0.0f; matrix[13] = 0.0f; matrix[14] = 0.0f; matrix[15] = 1.0f;
}

/**
 * matrixMultiply
 *
 * [ 0,  1,   2,  3 ]       [ 1, 0, 0, 0 ]          [ 1, 0, 0, 0 ]
 * [ 4,  5,   6,  7 ]       [ 0, 1, 0, 0 ]          [ 0, 1, 0, 0 ]
 * [ 8,  9,  10, 11 ]       [ 0, 0, 1, 0 ]          [ 0, 0, 1, 0 ]
 * [ 12, 13, 14, 15 ]       [ 0, 0, 0, 1 ]          [ 0, 0, 0, 1 ]
 *
 *
 * @param matrix pointer(holds the memory address) to a sequence of float values in memory
 *
 * */
// [0] = 0*0 + 4*1 + 8*2 + 12*3
// [1] = 1*0 + 5*1 + 9*2 + 13*3
// [2] = 2*0 + 6*1 +10*2 + 14*3

 // [5] = 1*4 + 5*5 + 9*6 + 13*7
void matrixMultiply(float* destination, float* operand1, float* operand2)
{
    float theResult[16]; // Temporary matrix to store the result

    // Perform matrix multiplication for each element in the 4x4 result matrix
    for(int i = 0; i < 4; i++) // For each row in the result matrix
    {
        for(int j = 0; j < 4; j++) // For each column in the result matrix
        {
            // Calculate the dot product for the (i, j) element in the result matrix
            theResult[4 * i + j] = operand1[j] * operand2[4 * i]      // First element of the row * first element of the column
                                   + operand1[4 + j] * operand2[4 * i + 1]  // Second element of the row * second element of the column
                                   + operand1[8 + j] * operand2[4 * i + 2]  // Third element of the row * third element of the column
                                   + operand1[12 + j] * operand2[4 * i + 3]; // Fourth element of the row * fourth element of the column
        }
    }

    // Copy the result into the destination matrix
    for(int i = 0; i < 16; i++)
    {
        destination[i] = theResult[i];
    }
}
GLuint loadShader(GLenum shaderType, const char* pSource) {


    GLuint shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);

        //on a successful shader, the below conditional does not get ran
        if (!compiled) {
            GLint infoLen = 0;
            //I WANT TO LOG THIS
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {

                char* buf = (char*)malloc(infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}
GLuint createProgram(const char* pVertexSource, const char* pFragmentSource) {
    //load up each shader and validate each of them
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    if (!vertexShader) {
        return 0;
    }
    GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
    if (!pixelShader) {
        return 0;
    }

    GLuint program = glCreateProgram();
    if (program) {
        //attach the shaders to the program object. Which is how we specify what objects are to be linked together
        //ie. attaching these shaders to the program object, tells openGL that these shaders are to be linked together
        // when linking operations occur on the program object
        glAttachShader(program, vertexShader);
        glAttachShader(program, pixelShader);

        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);

        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char* buf = (char*)malloc(bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }

    return program;
}
/***
 *
 *
 * [ 1  0  0  0 ]
 * [ 0  1  0  0 ]
 * [ 0  0  1  0 ]
 * [ x  y  z  1 ]   <-- x, y, z values represent the new position along X, Y, Z axes
 *
 *
 *
 * */
void matrixTranslate(float* matrix, float x, float y, float z)
{
    float temporaryMatrix[16];                // Create a temporary 4x4 matrix
    matrixIdentityFunction(temporaryMatrix);  // Initialize it to the identity matrix(basic not movement matrix)
    temporaryMatrix[12] = x;                  // Set translation on the X axis
    temporaryMatrix[13] = y;                  // Set translation on the Y axis
    temporaryMatrix[14] = z;                  // Set translation on the Z axis
    matrixMultiply(matrix, temporaryMatrix, matrix);  // Apply translation to the input matrix
}



void matrixScale(float* matrix, float x, float y, float z){
    float tempMatrix[16];
    matrixIdentityFunction(tempMatrix);
    tempMatrix[0] = x;
    tempMatrix[5] = y;
    tempMatrix[10] = z;
    matrixMultiply(matrix, tempMatrix, matrix);
}

void matrixRotateX(float* matrix, float angle){
    float tempMatrix[16];
    matrixIdentityFunction(tempMatrix);
    tempMatrix[5] = cos(matrixDegreesToRadians(angle));
    tempMatrix[9] = -sin(matrixDegreesToRadians(angle));
    tempMatrix[6] = sin(matrixDegreesToRadians(angle));
    tempMatrix[10] = cos(matrixDegreesToRadians(angle));
    matrixMultiply(matrix, tempMatrix, matrix);
}
void matrixRotateY(float *matrix, float angle){
    float tempMatrix[16];
    matrixIdentityFunction(tempMatrix);
    tempMatrix[0] = cos(matrixDegreesToRadians(angle));
    tempMatrix[8] = sin(matrixDegreesToRadians(angle));
    tempMatrix[2] = -sin(matrixDegreesToRadians(angle));
    tempMatrix[10] = cos(matrixDegreesToRadians(angle));
    matrixMultiply(matrix, tempMatrix, matrix);
}
void matrixRotateZ(float *matrix, float angle){
    float tempMatrix[16];
    matrixIdentityFunction(tempMatrix);
    tempMatrix[0] = cos(matrixDegreesToRadians(angle));
    tempMatrix[4] = -sin(matrixDegreesToRadians(angle));
    tempMatrix[1] = sin(matrixDegreesToRadians(angle));
    tempMatrix[5] = cos(matrixDegreesToRadians(angle));
    matrixMultiply(matrix, tempMatrix, matrix);
}
void matrixFrustum(float* matrix, float left, float right, float bottom, float top, float zNear, float zFar){
    float temp, xDistance, yDistance, zDistance;
    temp = 2.0 *zNear;
    xDistance = right - left;
    yDistance = top - bottom;
    zDistance = zFar - zNear;
    matrixIdentityFunction(matrix);
    matrix[0] = temp / xDistance;
    matrix[5] = temp / yDistance;
    matrix[8] = (right + left) / xDistance;
    matrix[9] = (top + bottom) / yDistance;
    matrix[10] = (-zFar - zNear) / zDistance;
    matrix[11] = -1.0f;
    matrix[14] = (-temp * zFar) / zDistance;
    matrix[15] = 0.0f;
}

void matrixPerspective(float* matrix, float fieldOfView, float aspectRatio, float zNear, float zFar){
    float ymax, xmax;
    ymax = zNear * tanf(fieldOfView * M_PI / 360.0);
    xmax = ymax * aspectRatio;
    matrixFrustum(matrix, -xmax, xmax, -ymax, ymax, zNear, zFar);
}


static const char  glVertexShader[] =
        "attribute vec4 vertexPosition;\n"
        "attribute vec3 vertexColour;\n"
        "varying vec3 fragColour;\n"
        "uniform mat4 projection;\n"
        "uniform mat4 modelView;\n"
        "void main()\n"
        "{\n"
        "    gl_Position = projection * modelView * vertexPosition;\n"
        "    fragColour = vertexColour;\n"
        "}\n";

static const char  glFragmentShader[] =
        "precision mediump float;\n"
        "varying vec3 fragColour;\n"
        "void main()\n"
        "{\n"
        "    gl_FragColor = vec4(fragColour, 1.0);\n"
        "}\n";


GLuint simpleCubeProgram; // the shader program
GLuint vertexLocation;
GLuint vertexColourLocation;
GLint projectionLocation;
GLint modelViewLocation;

float projectionMatrix[16];
float modelViewMatrix[16];
float angle = 0.0f;

bool setupGraphics(int width, int height){
    simpleCubeProgram = createProgram(glVertexShader, glFragmentShader);
    if (simpleCubeProgram == 0)
    {
        LOGE ("Could not create program");
        return false;
    }
    vertexLocation = glGetAttribLocation(simpleCubeProgram, "vertexPosition");
    vertexColourLocation = glGetAttribLocation(simpleCubeProgram, "vertexColour");
    projectionLocation = glGetUniformLocation(simpleCubeProgram, "projection");
    modelViewLocation = glGetUniformLocation(simpleCubeProgram, "modelView");
    /* Setup the perspective */
    matrixPerspective(projectionMatrix, 45, (float)width / (float)height, 0.1f, 100);
    glEnable(GL_DEPTH_TEST);
    glViewport(0, 0, width, height);
    return true;
}

GLfloat cubeVertices[] = {-1.0f,  1.0f, -1.0f, /* Back. */
                          1.0f,  1.0f, -1.0f,
                          -1.0f, -1.0f, -1.0f,
                          1.0f, -1.0f, -1.0f,
                          -1.0f,  1.0f,  1.0f, /* Front. */
                          1.0f,  1.0f,  1.0f,
                          -1.0f, -1.0f,  1.0f,
                          1.0f, -1.0f,  1.0f,
                          -1.0f,  1.0f, -1.0f, /* Left. */
                          -1.0f, -1.0f, -1.0f,
                          -1.0f, -1.0f,  1.0f,
                          -1.0f,  1.0f,  1.0f,
                          1.0f,  1.0f, -1.0f, /* Right. */
                          1.0f, -1.0f, -1.0f,
                          1.0f, -1.0f,  1.0f,
                          1.0f,  1.0f,  1.0f,
                          -1.0f, -1.0f, -1.0f, /* Top. */
                          -1.0f, -1.0f,  1.0f,
                          1.0f, -1.0f,  1.0f,
                          1.0f, -1.0f, -1.0f,
                          -1.0f,  1.0f, -1.0f, /* Bottom. */
                          -1.0f,  1.0f,  1.0f,
                          1.0f,  1.0f,  1.0f,
                          1.0f,  1.0f, -1.0f
};
GLfloat colour[] = {1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f
};
GLushort indices[] = {0, 2, 3, 0, 1, 3, 4, 6, 7, 4, 5, 7, 8, 9, 10, 11, 8, 10, 12, 13, 14, 15, 12, 14, 16, 17, 18, 16, 19, 18, 20, 21, 22, 20, 23, 22};

void renderFrame(){
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    matrixIdentityFunction(modelViewMatrix);
    // Apply scaling to make the cube larger or smaller
   // matrixScale(modelViewMatrix, 1.0f, 0.1f, 1.0f); // Flattens the cube along the y-axis
    matrixRotateX(modelViewMatrix, angle);
//   matrixRotateY(modelViewMatrix, angle);
//    matrixRotateZ(modelViewMatrix, angle);
    matrixTranslate(modelViewMatrix, 0.0f, 0.0f, -9.0f);
    glUseProgram(simpleCubeProgram);
    glVertexAttribPointer(vertexLocation, 3, GL_FLOAT, GL_FALSE, 0, cubeVertices);
    glEnableVertexAttribArray(vertexLocation);
    glVertexAttribPointer(vertexColourLocation, 3, GL_FLOAT, GL_FALSE, 0, colour);
    glEnableVertexAttribArray(vertexColourLocation);
    glUniformMatrix4fv(projectionLocation, 1, GL_FALSE, projectionMatrix);
    glUniformMatrix4fv(modelViewLocation, 1, GL_FALSE, modelViewMatrix);
    glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, indices);
    angle += 1;
    if (angle > 360)
    {
        angle -= 360;
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeCube_init(JNIEnv *env, jobject thiz,
        jint width, jint height) {
setupGraphics(width,height);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeCube_step(JNIEnv *env, jobject thiz) {
  renderFrame();

}