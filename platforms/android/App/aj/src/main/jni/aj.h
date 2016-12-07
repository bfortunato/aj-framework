//
// Created by bimbobruno on 1/4/17.
//

#ifndef APP_AJ_H_H
#define APP_AJ_H_H

#include <unistd.h>
extern unsigned int __page_size;

#include <jni.h>
#include <string>


#define NATIVE(returnType, className, methodName) \
extern "C" \
returnType \
        Java_applica_aj_runtime_v8_##className##_##methodName \

#define PARAMS JNIEnv* env, jobject thiz

#endif //APP_AJ_H_H
