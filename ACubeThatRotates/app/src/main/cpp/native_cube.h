#ifndef GLES3JNI_H
#define GLES3JNI_H 1

#include <android/log.h>
#include <math.h>

#if DYNAMIC_ES3
#include "gl3stub.h"
#else
// Include the latest possible header file( GL version header )
#if __ANDROID_API__ >= 24
#include <GLES3/gl32.h>
#elif __ANDROID_API__ >= 21
#include <GLES3/gl31.h>
#else
#include <GLES3/gl3.h>
#endif

#endif

#include "glm/glm.hpp"

extern unsigned int createShaderProgram(const char* vertexCode, const char* fragmentCode);
extern double now_ms();

// ----------------------------------------------------------------------------

class Renderer {
public:
    virtual ~Renderer();

protected:

public:
    Renderer();
    void render();
    void resize(int w, int h);

    bool init();

    float mZoomFactor = -3.0f;
    float mAngle = 0;

private:

    double mCurrentTime;
    double mOldTime;
    double mRotationSpeed;
    unsigned int mVBO;
    unsigned int mVAO;
    unsigned mProgram;
};

extern Renderer* createRenderer();

#endif // GLES3JNI_H
