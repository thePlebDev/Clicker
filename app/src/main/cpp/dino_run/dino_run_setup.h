//
// Created by Tristan on 2025-03-17.
//

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <string>
#include <stdlib.h>
#include <vector>

#ifndef CLICKER_DINO_RUN_SETUP_H
#define CLICKER_DINO_RUN_SETUP_H

#endif //CLICKER_DINO_RUN_SETUP_H



/**
 * basic TransformShader
 */
class TransformShader {


public:
    TransformShader();
    ~TransformShader();
    std::string  m_glVertexShader;
    std::string m_glFragmentShader;
    std::vector<GLfloat> m_squareVertices = {
            -0.800f, -0.0375f, -0.650f, -0.0375f, -0.650f,  0.0375f,
            -0.800f, -0.0375f, -0.650f,  0.0375f, -0.800f,  0.0375f,
            0.85f, -0.0375f, 1.0f, -0.0375f, 1.0f,  0.0375f,
            0.85f, -0.0375f, 1.0f,  0.0375f, 0.85f,  0.0375f
    };
    std::vector<GLfloat>& getSquareVertices() {
        return m_squareVertices;  // Returns a reference
    }


};
