
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




/**
 * loadShader() is a function meant to create and compile a shader.
 *
 * @param shaderType Specifies the type of shader to be created.
 * Can be GL_VERTEX_SHADER or GL_FRAGMENT_SHADER.
 *
 * @param pSource A C-string containing the source code of the shader.
 * Can be gVertexShader or gFragmentShader
 *
 * @return The shader object handle (GLuint) if the shader is successfully compiled,
 *         or 0 if compilation fails.
 */
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




auto gVertexShader =
        "attribute vec4 vPosition;\n"
        "void main() {\n"
        "  gl_Position = vPosition;\n"
        "}\n";

//This is the source code for your fragment shader.
auto gFragmentShader =
        "precision mediump float;\n"
        "uniform vec4 u_Color;\n"  // Uniform variable for color
        "void main() {\n"
        "  gl_FragColor = u_Color;\n"  // Set the color dynamically
        "}\n";


GLuint gProgram; // the shader program
GLuint gvPositionHandle;// hold the location of where the GPU will be expecting the vertex data that is required for our shader
//that we have loaded with the vPosition

bool setupGraphics(int w, int h) {

    gProgram = createProgram(gVertexShader, gFragmentShader);


    if (!gProgram) {
        return false;
    }
    gvPositionHandle = glGetAttribLocation(gProgram, "vPosition");

    glViewport(0, 0, w, h);
    return true;
}

const GLfloat gSquareVertices[] = {
        // Square 1: Top-left corner
        // Triangle one (top-left half)
        -0.75f, 0.375f,   // Top-left
        -0.75f, 0.125f,   // Bottom-left
        -0.25f, 0.125f,   // Bottom-right
        // Triangle two (bottom-right half)
        -0.25f, 0.375f,   // Top-right
        -0.75f, 0.375f,   // Top-left
        -0.25f, 0.125f,   // Bottom-right

        // Square 2: Top-right corner
        // Triangle one (top-left half)
        0.25f, 0.375f,    // Top-left
        0.25f, 0.125f,    // Bottom-left
        0.75f, 0.125f,    // Bottom-right
        // Triangle two (bottom-right half)
        0.75f, 0.375f,    // Top-right
        0.25f, 0.375f,    // Top-left
        0.75f, 0.125f,    // Bottom-right

        // Square 3: Bottom-left corner
        // Triangle one (top-left half)
        -0.75f, -0.125f,  // Top-left
        -0.75f, -0.375f,  // Bottom-left
        -0.25f, -0.375f,  // Bottom-right
        // Triangle two (bottom-right half)
        -0.25f, -0.125f,  // Top-right
        -0.75f, -0.125f,  // Top-left
        -0.25f, -0.375f,  // Bottom-right

        // Square 4: Bottom-right corner
        // Triangle one (top-left half)
        0.25f, -0.125f,   // Top-left
        0.25f, -0.375f,   // Bottom-left
        0.75f, -0.375f,   // Bottom-right
        // Triangle two (bottom-right half)
        0.75f, -0.125f,   // Top-right
        0.25f, -0.125f,   // Top-left
        0.75f, -0.375f,   // Bottom-right

        // Square 5: Middle
        // Triangle one (top-left half)
        -0.25f, 0.125f,   // Top-left
        -0.25f, -0.125f,  // Bottom-left
        0.25f, -0.125f,   // Bottom-right
        // Triangle two (bottom-right half)
        0.25f, 0.125f,    // Top-right
        -0.25f, 0.125f,   // Top-left
        0.25f, -0.125f    // Bottom-right
};
GLfloat currentColor[4] = {1.0f, 0.0f, 0.0f, 1.0f};
void renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    GLint colorLocation = glGetUniformLocation(gProgram, "u_Color");

    glUseProgram(gProgram);  // Use the shader program

    // Set the color based on the global variable
    glUniform4f(colorLocation, currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

    // Bind the vertex attribute array and draw
    glVertexAttribPointer(
            gvPositionHandle,
            2,  // 2 components per vertex (X, Y)
            GL_FLOAT,
            GL_FALSE,
            0,
            gSquareVertices
    );

    glEnableVertexAttribArray(gvPositionHandle);
    glDrawArrays(GL_TRIANGLES, 0, 30);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeSquareLoading_init(JNIEnv *env, jobject thiz,
                                                                       jint width, jint height) {
    setupGraphics(width, height);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeSquareLoading_step(JNIEnv *env, jobject thiz) {
    renderFrame();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_NativeSquareLoading_click(JNIEnv *env, jobject thiz) {
    if (currentColor[0] == 1.0f) {
        currentColor[0] = 0.0f;  // Red -> Green
        currentColor[1] = 1.0f;
        currentColor[2] = 0.0f;
    } else {
        currentColor[0] = 1.0f;  // Green -> Red
        currentColor[1] = 0.0f;
        currentColor[2] = 0.0f;
    }
}