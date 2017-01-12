LOCAL_PATH := $(call my-dir)
LOCAL_MULTILIB := "both"

V8_ROOT = /v8/v8
V8_INCLUDE = $(V8_ROOT)/include
V8_ARCH = arm
V8_OUT_DIR = $(V8_ROOT)/out/android_arm.release/obj.target/src
TARGET = release
V8_LIBRARY = static

ifeq ($(TARGET_ARCH_ABI),armeabi)
    V8_ARCH = arm
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    V8_ARCH = arm
endif

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
    V8_ARCH = arm64
endif

ifeq ($(TARGET_ARCH_ABI),x86)
	V8_ARCH = ia32
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
	V8_ARCH = x64
endif

ifeq ($(TARGET_ARCH_ABI),mips)
	V8_ARCH = mipsel
endif

ifeq ($(TARGET_ARCH_ABI),mips64)
	V8_ARCH = mipsel
endif

V8_OUT_DIR = $(V8_ROOT)/out/android_$(V8_ARCH).$(TARGET)/obj.target/src

ifeq ($(V8_LIBRARY),static)
    include $(CLEAR_VARS)
    LOCAL_MODULE := libv8_base
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_base.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := libv8_libbase
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_libbase.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := libv8_nosnapshot
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_nosnapshot.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := libv8_libplatform
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_libplatform.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
        LOCAL_MODULE := libv8_libsampler
        LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
        LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_libsampler.a
        include $(PREBUILT_STATIC_LIBRARY)
endif

ifeq ($(V8_LIBRARY),shared)
    include $(CLEAR_VARS)
    LOCAL_MODULE := libv8_libplatform
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8_libplatform.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := v8
    LOCAL_EXPORT_C_INCLUDES := $(V8_ROOT) $(V8_INCLUDE)
    LOCAL_SRC_FILES := $(V8_OUT_DIR)/libv8.so
    include $(PREBUILT_SHARED_LIBRARY)
endif


include $(CLEAR_VARS)
LOCAL_MODULE    := aj
LOCAL_SRC_FILES :=      runtime/MappedFunction.cpp \
                        runtime/JavaFunction.cpp \
                        runtime/AJV8Runtime.cpp \
                        aj.cpp
LOCAL_CFLAGS += -std=c++11 -Wall -Wno-unused-function -Wno-unused-variable -funroll-loops -ftree-vectorize -ffast-math -fpermissive -fexceptions -g

ifeq ($(V8_LIBRARY),shared)
    LOCAL_SHARED_LIBRARIES := v8
    LOCAL_STATIC_LIBRARIES := libv8_libplatform
endif

ifeq ($(V8_LIBRARY),static)
    LOCAL_SHARED_LIBRARIES :=
    LOCAL_STATIC_LIBRARIES := libv8_libplatform libv8_base libv8_libbase libv8_nosnapshot libv8_libsampler
endif

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -latomic
include $(BUILD_SHARED_LIBRARY)

