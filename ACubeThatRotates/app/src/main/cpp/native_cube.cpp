
#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <android/log.h>
#include <vector>
#include <fstream>
#include <stdio.h>

#include <android/asset_manager.h>

#include "native_cube.h"
#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"

static const char VERTEX_SHADER[] =
        "#version 320 es\n"
        "layout (location = 0) in vec3 aPos;\n"
        "layout (location = 1) in vec2 aTexCoord;\n"
        "out vec2 TexCoord;\n"
        "uniform mat4 model;\n"
        "uniform mat4 view;\n"
        "uniform mat4 projection;\n"
        "\n"
        "void main()\n"
        "{\n"
        //"    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n"
        "gl_Position = projection * view * model * vec4(aPos, 1.0f);\n"
        "TexCoord = vec2(aTexCoord.x, aTexCoord.y);\n"
        "}\n";

static const char FRAGMENT_SHADER[] =
        "#version 320 es\n"
        "precision mediump float;\n"
        "out vec4 FragColor;\n"
        "in vec2 TexCoord;\n"
        "\n"
        "void main()\n"
        "{\n"
        "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n"
        "FragColor = vec4(TexCoord, 1.0f, 1.0f);\n"
        "}\n";

unsigned int SCREEN_WIDTH;
unsigned int SCREEN_HEIGHT;

std::vector<float> verticesObj;
int triCount = 0;

std::vector<float> vertices2 = {
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
};

float vertices[] = {
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,

        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,

        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,

        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f
};


unsigned int createShaderProgram(const char* vertexCode, const char* fragmentCode){
    unsigned int vertex, fragment, shaderProgram;

    // compile vertex shader
    vertex = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertex, 1, &vertexCode, NULL);
    glCompileShader(vertex);
    // TODO check for compile errors
    // fragment shaders
    fragment = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragment, 1, &fragmentCode, NULL);
    glCompileShader(fragment);
    // TODO check for compile errors
    // shader program
    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertex);
    glAttachShader(shaderProgram, fragment);
    glLinkProgram(shaderProgram);
    // TODO check for linking errors...
    glDeleteShader(vertex);
    glDeleteShader(fragment);
    // no need for error checking if there are no errors

    return shaderProgram;
}

extern double now_ms(){
    struct timespec res;
    clock_gettime(CLOCK_MONOTONIC, &res);
    return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;
}


Renderer* createRenderer(){
    Renderer* renderer = new Renderer();
    if(renderer->init()){
        return renderer;
    }else{
        return nullptr;
    }
}

// ----------------------------------------------------------------------------
// Renderer

Renderer::Renderer()
{
    mProgram = 0;
    mVBO = 0;
}

Renderer::~Renderer() {
}

bool Renderer::init() {



    mRotationSpeed = 0.1f;
    mOldTime = now_ms();
    mProgram = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    if(!mProgram)
        return false;

    glGenVertexArrays(1, &mVAO);
    glGenBuffers(1, &mVBO);

    glBindVertexArray(mVAO);

    glBindBuffer(GL_ARRAY_BUFFER, mVBO);
    glBufferData(GL_ARRAY_BUFFER, verticesObj.size() * sizeof(float), &verticesObj[0], GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*)(3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);

    return true;
}

void Renderer::resize(int w, int h) {
    SCREEN_HEIGHT = h;
    SCREEN_WIDTH = w;
}

void Renderer::render(){

    mCurrentTime = now_ms();
    double delta = mCurrentTime - mOldTime;

    glClearColor(0.2f, 0.2f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


    //mAngle += delta * mRotationSpeed;

    glm::mat4 model = glm::mat4(1.0f);
    glm::mat4 view = glm::mat4(1.0f);
    glm::mat4 projection = glm::mat4(1.0f);
    view = glm::translate(view, glm::vec3(0.0f, 0.0f, -10.0f));
    model = glm::translate(model, glm::vec3(0.0f, 0.0f, mZoomFactor));
    projection = glm::perspective(glm::radians(45.0f), (float)SCREEN_WIDTH / (float)SCREEN_HEIGHT, 0.1f, 100.0f);
    model = glm::rotate(model, glm::radians(mAngle), glm::vec3(0.0f, 1.0f, 0.0f));


    glUseProgram(mProgram);

    unsigned int modelLoc = glGetUniformLocation(mProgram, "model");
    unsigned int viewLoc = glGetUniformLocation(mProgram, "view");
    unsigned int projectionLoc = glGetUniformLocation(mProgram, "projection");

    glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
    glUniformMatrix4fv(projectionLoc, 1, GL_FALSE, glm::value_ptr(projection));

    glBindVertexArray(mVAO);
    glDrawArrays(GL_TRIANGLES, 0, triCount);

    mOldTime = mCurrentTime;
}

// ----------------------------------------------------------------------------
// Kinda init stuff

static Renderer* renderer = NULL;

extern "C" {
    JNIEXPORT void JNICALL Java_com_example_acubethatrotates_nativeCubeLib_init(JNIEnv* env, jobject obj, jint width, jint height, jfloatArray vertexArray);
    JNIEXPORT void JNICALL Java_com_example_acubethatrotates_nativeCubeLib_resize(JNIEnv* env, jobject obj, jint width, jint height);
    JNIEXPORT void JNICALL Java_com_example_acubethatrotates_nativeCubeLib_update(JNIEnv* env, jobject obj);
    JNIEXPORT void JNICALL Java_com_example_acubethatrotates_nativeCubeLib_zoom(JNIEnv* env, jobject obj, jfloat zoomfactor);
    JNIEXPORT void JNICALL Java_com_example_acubethatrotates_nativeCubeLib_rotate(JNIEnv* env, jobject obj, jfloat angle);
};

#if !defined(DYNAMIC_ES3)
static GLboolean gl3stubInit() {
    return GL_TRUE;
}
#endif

JNIEXPORT void JNICALL
Java_com_example_acubethatrotates_nativeCubeLib_init(JNIEnv* env, jobject obj, jint width, jint height, jfloatArray vertexArray) {



    if (renderer) {
        delete renderer;
        renderer = NULL;
    }

    jsize len = (*env).GetArrayLength(vertexArray);
    triCount = len / 5;

    jfloat *body = (*env).GetFloatArrayElements(vertexArray, 0);

    for(int i = 0; i < len; ++i){
        verticesObj.push_back(body[i]);
        //__android_log_print(ANDROID_LOG_ERROR, "Vertex Jarray", "%f", body[i]);

    }

    SCREEN_HEIGHT = height;
    SCREEN_WIDTH = width;

    glEnable(GL_DEPTH_TEST);
    //glDisable(GL_CULL_FACE);

    const char* versionStr = (const char*)glGetString(GL_VERSION);
    if (strstr(versionStr, "OpenGL ES 3.") && gl3stubInit()) {
        renderer = createRenderer();
    }
    else {
        __android_log_print(ANDROID_LOG_ERROR, "TRACKERS", "%s", "You dont have OpenGL ES3 and that is a problem");
    }
}

JNIEXPORT void JNICALL
Java_com_example_acubethatrotates_nativeCubeLib_resize(JNIEnv* env, jobject obj, jint width, jint height) {
    if (renderer) {
        __android_log_print(ANDROID_LOG_ERROR, "TRACKERS", "%s", "Ich bin in resize");
        renderer->resize(width, height);
    }
}

JNIEXPORT void JNICALL
Java_com_example_acubethatrotates_nativeCubeLib_update(JNIEnv* env, jobject obj) {
    if (renderer) {
        renderer->render();
    }
}

JNIEXPORT void JNICALL
Java_com_example_acubethatrotates_nativeCubeLib_zoom(JNIEnv* env, jobject obj, jfloat zoomfactor){
    if(renderer)
        renderer->mZoomFactor = zoomfactor;
}

JNIEXPORT void JNICALL
Java_com_example_acubethatrotates_nativeCubeLib_rotate(JNIEnv* env, jobject obj, jfloat angle){
    if(renderer)
        renderer->mAngle += angle;
}


