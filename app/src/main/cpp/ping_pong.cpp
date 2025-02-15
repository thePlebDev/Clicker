#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>


// 1) CREATE A SINGLE RED TRIANGLE                       (DONE)
// 2) CREATE TO TRIANGLES AND HAVE THEM FORM A SQUARE    (DONE)

#define LOG_TAG "pingPong"
#define LOGI(TAG, ...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//source code for the vertex shade
// we define, attribute vec4 vPosition; as the input for the shader
// we set gl_Position = vPosition meaning we hook up the input to the output
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
bool setupGraphics(int w, int h){
    simpleTriangleProgram = createProgram(glVertexShader, glFragmentShader);
    if (!simpleTriangleProgram)
    {
        LOGE ("Could not create program");
        return false;
    }
    //retrieve a reference to the position input in the vertex shader
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    glViewport(0, 0, w, h);
    return true;
}
// the X coordinates are 0,3,6,9,12,15
// the Y coordinates are 1,4,7,10,13,16
 GLfloat triangleVertices[] = {
        // First triangle
        0.08f,  0.04f, 0.0f,  // Top right
        0.08f, -0.04f, 0.0f,  // Bottom right
        -0.08f,  0.04f, 0.0f,  // Top left

        // Second triangle
        0.08f, -0.04f, 0.0f,  // Bottom right
        -0.08f, -0.04f, 0.0f,  // Bottom left
        -0.08f,  0.04f, 0.0f   // Top left

};
void renderFrame(){

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear (GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    glUseProgram(simpleTriangleProgram);
    //specify how it should interpret the vertex data
    glVertexAttribPointer(vPosition, 3, GL_FLOAT, GL_FALSE, 0 ,triangleVertices);
    //giving the vertex attribute location as its argument;
    glEnableVertexAttribArray(vPosition);
    glDrawArrays(GL_TRIANGLES, 0, 6);
}
void updateTriangle(
        float xValue,
        float yValue
        ){

    float newX = triangleVertices[6] + xValue/20;
    float newXRight = triangleVertices[0] + xValue/20;
    LOGI("RENDERFRAMECHECK", "triangleVertices[6]newX ==> %f", newX);
    if(newX>-1 && newXRight<1){
        for (int i = 0; i < 18; i += 3) {
            // LOGI("RENDERFRAMECHECK", "currentXValue  ==> %f", triangleVertices[i]);

            // Calculate new position
            float newX = triangleVertices[i] + xValue/20;

            // Clamp between -1.0 and 1.0
            triangleVertices[i] = fmaxf(-1.0f, fminf(1.0f, newX));
        }
    }


    // Update the X coordinates


//    //this only updates the Y COORDINATES
//    for (int i = 1; i < 18; i += 3) {
//        LOGI("RENDERFRAMECHECK",  "i ==> %d",i);
//        triangleVertices[i] += 0.01f;
//    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_init(JNIEnv *env, jobject thiz,jint width, jint height) {
    setupGraphics(width, height);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_move(JNIEnv *env, jobject thiz,
                                                                          jfloat x_value,
                                                                          jfloat y_value) {
    updateTriangle(x_value,y_value);
    renderFrame();
}