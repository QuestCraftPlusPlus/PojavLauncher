//
// Created by Judge on 12/23/2021.
//
#include <thread>
#include <string>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <OpenOVR/openxr_platform.h>
#include <jni.h>
#include "log.h"

static JavaVM* jvm;
XrInstanceCreateInfoAndroidKHR* OpenComposite_Android_Create_Info;
XrGraphicsBindingOpenGLESAndroidKHR* OpenComposite_Android_GLES_Binding_Info;

std::string (*OpenComposite_Android_Load_Input_File)(const char *path);

static std::string load_file(const char *path);

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (jvm == nullptr) {
        jvm = vm;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_launch(JNIEnv *env, jclass clazz, jobject main) {
main = (*env).NewGlobalRef(main);
jclass clazz1 = (*env).GetObjectClass(main);
jmethodID id = (*env).GetMethodID(clazz1, "runCraft", "()V");
std::thread thread([=]() {
    JNIEnv* threadEnv;
    jvm->AttachCurrentThread(&threadEnv, nullptr);
    threadEnv->CallVoidMethod(main, id);
});
thread.detach();
}

extern "C"
JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_setAndroidInitInfo(JNIEnv *env, jclass clazz, jobject ctx) {
OpenComposite_Android_Load_Input_File = load_file;

env->GetJavaVM(&jvm);
ctx = env->NewGlobalRef(ctx);
OpenComposite_Android_Create_Info = new XrInstanceCreateInfoAndroidKHR{
        XR_TYPE_INSTANCE_CREATE_INFO_ANDROID_KHR,
        nullptr,
        jvm,
        ctx
};

PFN_xrInitializeLoaderKHR initializeLoader = nullptr;
XrResult res;

res = xrGetInstanceProcAddr(XR_NULL_HANDLE, "xrInitializeLoaderKHR",
                            (PFN_xrVoidFunction *) (&initializeLoader));

if(!XR_SUCCEEDED(res)) {
printf("Error!");
}

XrLoaderInitInfoAndroidKHR loaderInitInfoAndroidKhr = {
        XR_TYPE_LOADER_INIT_INFO_ANDROID_KHR,
        nullptr,
        jvm,
        ctx
};

initializeLoader((const XrLoaderInitInfoBaseHeaderKHR *) &loaderInitInfoAndroidKhr);
}

extern "C"
JNIEXPORT void JNICALL
Java_org_vivecraft_provider_VLoader_setEGLGlobal() {
    static const EGLint attribs[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            // Minecraft required on initial 24
            EGL_DEPTH_SIZE, 24,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
            EGL_NONE
    };

    EGLDisplay dsp = eglGetCurrentDisplay();
    EGLConfig cfg;
    int num_configs;
    if (!eglChooseConfig(dsp, attribs, &cfg, 1, &num_configs)) {
        printf("EGLBridge: Error couldn't get an EGL visual config: %s\n", eglGetError());
    }
    OpenComposite_Android_GLES_Binding_Info = new XrGraphicsBindingOpenGLESAndroidKHR{
            XR_TYPE_GRAPHICS_BINDING_OPENGL_ES_ANDROID_KHR,
            nullptr,
            eglGetCurrentDisplay(),
            cfg,
            eglGetCurrentContext()
    };
}

static std::string load_file(const char *path) {
    // Just read the file from the filesystem, we changed the working directory earlier so
    // Vivecraft can extract it's manifest files.

    printf("Path: %s", path);
    int fd = open(path, O_RDONLY);
    if (!fd) {
        LOGE("Failed to load manifest file %s: %d %s", path, errno, strerror(errno));
    }

    int length = lseek(fd, 0, SEEK_END);
    lseek(fd, 0, SEEK_SET);

    std::string data;
    data.resize(length);
    if (!read(fd, (void *) data.data(), data.size())) {
        LOGE("Failed to load manifest file %s failed to read: %d %s", path, errno, strerror(errno));
    }

    if (close(fd)) {
        LOGE("Failed to load manifest file %s failed to close: %d %s", path, errno,
             strerror(errno));
    }

    return std::move(data);
}
