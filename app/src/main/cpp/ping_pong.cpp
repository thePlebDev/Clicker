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

enum Movement {INIT, MOVE, STOP};

Movement bottomPaddleMovementState = INIT;
float initialClick =0.0f;

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

//This is the paddles
GLfloat triangleVertices[] = {

        // First TOP paddle half
        -0.5f,  0.95f, 0.0f,  // Top right
        -0.5f, 1.0f, 0.0f,  // Bottom right
        -1.0f,  1.0f, 0.0f,  // Top left

        // Second TOP paddle half
        -0.5f,  0.95f, 0.0f,  // Top right
        -1.00f, 0.95f, 0.0f,  // Bottom right
        -1.00f,  1.0f, 0.0f,  // Top left





        // First BALL HALF (THE BALL)
        0.08f,  0.04f, 0.0f,  // Top right
        0.08f, -0.04f, 0.0f,  // Bottom right
        -0.08f,  0.04f, 0.0f,  // Top left

        // Second BALL HALF  (THE BALL)
        0.08f, -0.04f, 0.0f,  // Bottom right
        -0.08f, -0.04f, 0.0f,  // Bottom left
        -0.08f,  0.04f, 0.0f,  // Top left



        // FIRST BOTTOM paddle half
        0.5f,  -0.95f, 0.0f,  // Top right
        0.5f, -1.0f, 0.0f,  // Bottom right
        1.0f,  -1.0f, 0.0f,  // Top left

        // SECOND BOTTOM paddle half
        0.5f,  -0.95f, 0.0f,  // Top right
        1.00f, -0.95f, 0.0f,  // Bottom right
        1.00f,  -1.0f, 0.0f,  // Top left
};
void renderFrame(){

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear (GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    glUseProgram(simpleTriangleProgram);
    //specify how it should interpret the vertex data
    glVertexAttribPointer(vPosition, 3, GL_FLOAT, GL_FALSE, 0 ,triangleVertices);
    //giving the vertex attribute location as its argument;
    glEnableVertexAttribArray(vPosition);
    glDrawArrays(GL_TRIANGLES, 0, 18);
   // glDrawArrays(GL_TRIANGLES, 0, 6);
}



/**
 * moveBottomPaddleXAxis is moving the paddle acrosse the screen
 * */
void moveBottomPaddleXAxis(GLfloat *vertices, GLfloat dx) {
    // Indices of y-coordinates for bottom paddle
    int indices[] = {36, 39, 42, 45, 48, 51};
    float newX = dx/40;
    LOGI("NEWXCHECK",  "newX ==> %f",newX);

    for (int i = 0; i < 6; i++) {
        vertices[indices[i]] += newX;  // Apply translation
    }
}
bool topHit = false;
bool gameOver = false;

void moveBall(GLfloat *vertices, GLfloat dy){
    if(!gameOver){


        float newY = (dy/80)*-1;
        if(!topHit){
            //this is when the ball is moving upwards
            if(vertices[19] <1){
                for (int i = 19; i < 36; i+=3){
                    vertices[i] += newY;
                }
            }else{
                topHit = true;
            }
        }else{
            //this is when the ball is moving downwards
            if(vertices[19] > -0.87){ //as long as it has not hit the bottom keep moving
                //todo: make all below work
                // I should just have it move down
                //if it its any of its bottom vertices hit the bottom vertices of the paddle bound up
                //other wise stop
                for (int i = 19; i < 36; i+=3){
                    vertices[i] += (newY*-1);
                }
            }else{ //THIS MEANS THE Y-AXIS IS ASSUMED TO BE CROSSED AND THE PADDLE CAN BE HIT NOW

                if(vertices[27]>=vertices[36] && vertices[27]<= vertices[42]){
                    LOGI("bottomhittesting",  "HIT");
                }else{
                    gameOver = true;
                }

                topHit = false;
            }

        }
    }

}




extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_init(JNIEnv *env, jobject thiz,jint width, jint height) {
    setupGraphics(width, height);
}


/**
 * TODO: THIS SHOULD NOT BE CALLED CONSTANTLY LIKE IT IS. WILL CHANGE LATER
 * */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_move(JNIEnv *env, jobject thiz,jfloat x_value,jfloat y_value) {
    switch (bottomPaddleMovementState) {
        case INIT:{
            LOGI("bottomPaddleMovementState",  "INIT");
            break;
        }
        case MOVE:{
            LOGI("bottomPaddleMovementState",  "MOVE");
           moveBottomPaddleXAxis(triangleVertices,x_value);

            break;
        }
        case STOP:{
            LOGI("bottomPaddleMovementState",  "STOP");
            break;
        }

    }
    moveBall(triangleVertices,y_value);

    glFlush();  // Force OpenGL to process commands immediately
    renderFrame();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_bottomPaddleClicked(
        JNIEnv *env, jobject thiz, jboolean clicked) {
    if(clicked){
        bottomPaddleMovementState = MOVE;

    }else{
        bottomPaddleMovementState = STOP;

    }


}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_presentation_minigames_views_PingPongSystem_checkIfPaddleClicked(
        JNIEnv *env, jobject thiz, jfloat x_value, jfloat y_value) {

    //if anywhere is clicked on the paddle
    if (triangleVertices[36] && x_value <= triangleVertices[51]  && y_value >= triangleVertices[52] && y_value <= triangleVertices[37]) {
        initialClick = x_value;
        bottomPaddleMovementState = MOVE;
    } else {

    }


}