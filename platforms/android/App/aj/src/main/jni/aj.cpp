//
// Created by bimbobruno on 1/4/17.
//

#include "aj.h"
#include "runtime/AJV8Runtime.h"

unsigned int __page_size = getpagesize();

AJV8Runtime *runtime = nullptr;

NATIVE(void, AJV8Runtime, init) (PARAMS) {
    runtime = new AJV8Runtime();
    runtime->Init();
}

NATIVE(void, AJV8Runtime, executeScript) (PARAMS, jstring source) {
    const char* c_source = env->GetStringUTFChars(source, 0);
    std::string s_source (c_source);
    runtime->executeScript(s_source);
}

NATIVE(void, AJV8Runtime, destroy) (PARAMS) {
    runtime->Destroy();
}