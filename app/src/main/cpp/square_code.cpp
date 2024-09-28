
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

//
// Created by Tristan on 2024-09-25.
//
// 1) CREATE A SINGLE RED TRIANGLE                       (DONE)
// 2) CREATE TO TRIANGLES AND HAVE THEM FORM A SQUARE    (DONE)




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
        "void main() {\n"
        "  gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);\n"
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
        // Triangle one (top-left half of the square)
        -0.5f, 0.25f,   // Top-left corner
        -0.5f, -0.25f,  // Bottom-left corner
        0.5f, -0.25f,   // Bottom-right corner

        // Triangle two (bottom-right half of the square)
        0.5f, 0.25f,    // Top-right corner
        -0.5f, 0.25f,   // Top-left corner
        0.5f, -0.25f    // Bottom-right corner
};

void renderFrame() {

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);



    glUseProgram(gProgram);//We select which program we want to use

    // Get the location of the color uniform
   // GLuint colorUniform = glGetUniformLocation(gProgram, "vColor");


    // We then need to link the attribute we mentioned in the shader to the actual triangle data defined above
    //so we need to link the gvPositionHandle and the gTriangleVertices data
    glVertexAttribPointer(
            gvPositionHandle,
            2, //each vertex is going to have 2 elements to. these are the X,Y positions
            GL_FLOAT, //Specifies the data type of each component in the array
            GL_FALSE,
            0, //no stride between our verticies
            gSquareVertices //pointer to the actual triangle vertices.
    );

    glEnableVertexAttribArray(gvPositionHandle);

    glDrawArrays(GL_TRIANGLES, 0, 3);  // First triangle (vertices 0, 1, 2)
    glDrawArrays(GL_TRIANGLES, 3, 3);  // Second triangle (vertices 3, 4, 5)


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