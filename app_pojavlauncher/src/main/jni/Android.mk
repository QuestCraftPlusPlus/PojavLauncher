LOCAL_PATH := $(call my-dir)
HERE_PATH := $(LOCAL_PATH)

# include $(HERE_PATH)/crash_dump/libbase/Android.mk
# include $(HERE_PATH)/crash_dump/libbacktrace/Android.mk
# include $(HERE_PATH)/crash_dump/debuggerd/Android.mk

LOCAL_PATH := $(HERE_PATH)

include $(CLEAR_VARS)
LOCAL_MODULE := openxr_loader
LOCAL_SRC_FILES := libopenxr_loader.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := occore
LOCAL_SRC_FILES := ./OpenOVR/libOCCore.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := openxr_loader
LOCAL_MODULE := drvopenxr
LOCAL_SRC_FILES := ./OpenOVR/libDrvOpenXR.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ocovr
LOCAL_STATIC_LIBRARIES := drvopenxr occore drvopenxr
LOCAL_SRC_FILES := ./OpenOVR/OCOVR.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog -landroid -lGLESv3 -lvulkan -lEGL
LOCAL_CFLAGS := -DXR_USE_PLATFORM_ANDROID -DXR_USE_GRAPHICS_API_OPENGL_ES
LOCAL_SHARED_LIBRARIES := openxr_loader
LOCAL_WHOLE_STATIC_LIBRARIES := ocovr
LOCAL_MODULE := openvr_api
        LOCAL_SRC_FILES := \
                    vloader.cpp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := xhook
        LOCAL_SRC_FILES  := xhook/xhook.c \
                    xhook/xh_core.c \
                    xhook/xh_elf.c \
                    xhook/xh_jni.c \
                    xhook/xh_log.c \
                    xhook/xh_util.c \
                    xhook/xh_version.c
        LOCAL_C_INCLUDES := $(LOCAL_PATH)/xhook
LOCAL_CFLAGS     := -Wall -Wextra -Werror -fvisibility=hidden
LOCAL_CONLYFLAGS := -std=c11
LOCAL_LDLIBS     := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
# Link GLESv2 for test
LOCAL_LDLIBS := -ldl -llog -landroid
# -lGLESv2
LOCAL_MODULE := pojavexec
# LOCAL_CFLAGS += -DDEBUG
# -DGLES_TEST
LOCAL_SRC_FILES := \
    egl_bridge.c \
    input_bridge_v3.c \
    jre_launcher.c \
    utils.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := istdio
LOCAL_SHARED_LIBRARIES := xhook
LOCAL_SRC_FILES := \
    stdio_is.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/xhook
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pojavexec_awt
LOCAL_SRC_FILES := \
    awt_bridge.c
include $(BUILD_SHARED_LIBRARY)

# Helper to get current thread
# include $(CLEAR_VARS)
# LOCAL_MODULE := thread64helper
# LOCAL_SRC_FILES := thread_helper.cpp
# include $(BUILD_SHARED_LIBRARY)

# fake lib for linker
include $(CLEAR_VARS)
LOCAL_MODULE := awt_headless
include $(BUILD_SHARED_LIBRARY)

# libawt_xawt without X11, used to get Caciocavallo working
LOCAL_PATH := $(HERE_PATH)/awt_xawt
include $(CLEAR_VARS)
LOCAL_MODULE := awt_xawt
# LOCAL_CFLAGS += -DHEADLESS
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
LOCAL_SHARED_LIBRARIES := awt_headless
LOCAL_SRC_FILES := xawt_fake.c
include $(BUILD_SHARED_LIBRARY)

# delete fake libs after linked
$(info $(shell (rm $(HERE_PATH)/../jniLibs/*/libawt_headless.so)))

