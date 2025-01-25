#include <jni.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#include <EGL/egl.h>
#include <android/native_window_jni.h>

#define LOG_TAG "libNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


static const char glVertexShader[] =
        "attribute vec4 vPosition;\n"
        "attribute vec2 aTexCoord;\n"
        "varying vec2 v_TexCoord;\n"
        "void main()\n"
        "{\n"
        "  gl_Position = vPosition;\n"
        "  v_TexCoord = aTexCoord;\n"
        "}\n";

static const char glFragmentShader[] =
        "precision mediump float;\n"
        "varying vec2 v_TexCoord;\n"
        "uniform sampler2D u_Texture;\n"
        "void main()\n"
        "{\n"
        "  vec4 color = texture2D(u_Texture, v_TexCoord);\n"
        "  float gray = dot(color.rgb, vec3(0.3, 0.59, 0.11));\n"
        "  vec3 sepia = vec3(gray) * vec3(1.2, 1.0, 0.8);\n"
        "  gl_FragColor = vec4(sepia, color.a);\n"
        "}\n";

GLuint loadShader(GLenum shaderType, const char* shaderSource) {
    GLuint shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &shaderSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char*) malloc(infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("Could not compile shader %d:\n%s\n", shaderType, buf);
                    free(buf);
                }
            }
            glDeleteShader(shader);
            shader = 0;
        }
    }
    return shader;
}

GLuint createProgram(const char* vertexSource, const char* fragmentSource) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    if (!vertexShader) {
        return 0;
    }
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    if (!fragmentShader) {
        return 0;
    }
    GLuint program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char *buf = (char*) malloc(bufLength);
                if (buf) {
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
GLuint vPosition, aTexCoord, u_Texture;
GLuint textureId;

const GLfloat triangleVertices[] = {
        // Positions    // Texture coordinates
        0.0f,  1.0f,    0.5f, 0.0f,
        -1.0f, -1.0f,    0.0f, 1.0f,
        1.0f, -1.0f,    1.0f, 1.0f
};

bool setupGraphics(int w, int h) {
    simpleTriangleProgram = createProgram(glVertexShader, glFragmentShader);
    if (!simpleTriangleProgram) {
        LOGE("Could not create program");
        return false;
    }
    vPosition = glGetAttribLocation(simpleTriangleProgram, "vPosition");
    aTexCoord = glGetAttribLocation(simpleTriangleProgram, "aTexCoord");
    u_Texture = glGetUniformLocation(simpleTriangleProgram, "u_Texture");

    glGenTextures(1, &textureId);
    glBindTexture(GL_TEXTURE_2D, textureId);

    // Set texture parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    // Load a blank texture for now (example size 256x256)
    unsigned char textureData[256 * 256 * 4] = {255}; // White texture
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 256, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData);

    glViewport(0, 0, w, h);
    return true;
}

void renderFrame() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    glUseProgram(simpleTriangleProgram);

    // Set position attribute
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), triangleVertices);
    glEnableVertexAttribArray(vPosition);

    // Set texture coordinate attribute
    glVertexAttribPointer(aTexCoord, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), &triangleVertices[2]);
    glEnableVertexAttribArray(aTexCoord);

    // Bind texture
    glBindTexture(GL_TEXTURE_2D, textureId);
    glUniform1i(u_Texture, 0); // Texture unit 0

    glDrawArrays(GL_TRIANGLES, 0, 3);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_VideoEncoder_init(JNIEnv *env, jobject thiz,
                                                                jint width, jint height) {
    setupGraphics(width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_VideoEncoder_step(JNIEnv *env, jobject thiz) {
    renderFrame();
}



