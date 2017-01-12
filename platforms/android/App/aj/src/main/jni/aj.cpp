//
// Created by bimbobruno on 1/4/17.
//

#include "aj.h"
#include "runtime/AJV8Runtime.h"
#include "runtime/JavaFunction.h"

unsigned int __page_size = getpagesize();

AJV8Runtime *runtime = nullptr;
std::vector<JavaFunction *> *javaFunctions;
JavaVM *jvm;

NATIVE(void, AJV8Runtime, init) (PARAMS) {
    env->GetJavaVM(&jvm);

    javaFunctions = new std::vector<JavaFunction *>();

    runtime = new AJV8Runtime();
    runtime->Init();
}

NATIVE(void, AJV8Runtime, mapFunction) (PARAMS, jstring name, jobject fn) {
    std::string c_name(env->GetStringUTFChars(name, 0));

    auto jf = new JavaFunction(jvm, c_name, env->NewGlobalRef(fn));
    javaFunctions->push_back(jf);
    auto action = std::function<void()>([jf]() {
        jf->Call();
    });
    runtime->RegisterFunction(c_name, action);
}

NATIVE(void, AJV8Runtime, executeScript) (PARAMS, jstring source) {
    const char* c_source = env->GetStringUTFChars(source, 0);
    std::string s_source (c_source);
    runtime->executeScript(s_source);
}

NATIVE(void, AJV8Runtime, destroy) (PARAMS) {
    runtime->Destroy();

    delete javaFunctions;
    delete runtime;
}