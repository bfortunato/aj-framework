//
// Created by bruno fortunato on 08/01/2017.
//

#include "JavaFunction.h"

JNIEnv *GetEnv(JavaVM *jvm) {
    JNIEnv *env;
    int stat = jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    //if (stat == JNI_EDETACHED) {
        //int res = jvm->AttachCurrentThread(&env, nullptr);
    //}

    return env;
}

void CloseEnv(JavaVM *jvm) {
    jvm->DetachCurrentThread();
}

JavaFunction::JavaFunction(JavaVM *jvm, const std::string &name, jobject instance) : m_jvm(jvm),
                                                                                     m_name(name),
                                                                                     m_instance(instance) {

    m_methodId = nullptr;
}

JavaFunction::~JavaFunction() {
    JNIEnv *env = GetEnv(m_jvm);

    env->DeleteGlobalRef(m_instance);

    CloseEnv(m_jvm);
}

void JavaFunction::Call() {
    JNIEnv *env = GetEnv(m_jvm);

    if (m_methodId == nullptr) {
        jclass type = env->GetObjectClass(m_instance);
        m_methodId = env->GetMethodID(type, "run", "()V");
    }

    env->CallVoidMethod(m_instance, m_methodId);

    CloseEnv(m_jvm);
}

