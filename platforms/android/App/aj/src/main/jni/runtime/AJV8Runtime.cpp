//
// Created by bimbobruno on 1/4/17.
//

#include "AJV8Runtime.h"

#include <iostream>
#include <android/log.h>
#include <libplatform/libplatform.h>

void log(std::string message) {
    __android_log_print(ANDROID_LOG_INFO, "AJCPP", "%s", message.c_str());
}


using namespace v8;

class ArrayBufferAllocator : public v8::ArrayBuffer::Allocator {
public:
    virtual void* Allocate(size_t length) {
        void* data = AllocateUninitialized(length);
        return data == NULL ? data : memset(data, 0, length);
    }
    virtual void* AllocateUninitialized(size_t length) { return malloc(length); }
    virtual void Free(void* data, size_t) { free(data); }
};

ArrayBufferAllocator array_buffer_allocator;

AJV8Runtime::AJV8Runtime() {

}

void AJV8Runtime::Init() {
    log("Initializing AJV8Runtime platform...");

    Platform* platform = platform::CreateDefaultPlatform();
    V8::InitializePlatform(platform);
    V8::Initialize();

    log("Initializing AJV8Runtime isolate...");
    Isolate::CreateParams create_params;
    create_params.array_buffer_allocator = &array_buffer_allocator;
    m_isolate = Isolate::New(create_params);

    log("Creating scope for context");

    Isolate::Scope isolate_scope(m_isolate);
    HandleScope handle_scope(m_isolate);

    log("Initializing AJV8Runtime context...");

    auto context = Context::New(m_isolate);
    m_context.Reset(m_isolate, context);

    log("Done");
}

void AJV8Runtime::Destroy() {
    m_context.Reset();
    //m_isolate->Dispose();

    V8::Dispose();
    V8::ShutdownPlatform();

    log("AJV8Runtime disposed");

}

void AJV8Runtime::executeScript(std::string source) {
    log("Executing script: " + source);

    m_isolate->Enter();

    HandleScope handle_scope(m_isolate);
    Local<Context> context = m_context.Get(m_isolate);
    Context::Scope context_scope(context);

    TryCatch try_catch(m_isolate);
    Local<String> script = String::NewFromUtf8(m_isolate, source.c_str(), NewStringType::kNormal).ToLocalChecked();

    // Compile the script and check for errors.
    Local<Script> compiled_script;
    if (!Script::Compile(context, script).ToLocal(&compiled_script)) {
        String::Utf8Value error(try_catch.Exception());
        log(*error);
        return;
    }

    // Run the script!
    Local<Value> result;
    if (!compiled_script->Run(context).ToLocal(&result)) {
        // The TryCatch above is still in effect and will have caught the error.
        String::Utf8Value error(try_catch.Exception());
        log(*error);
        // Running the script failed; bail out.
        return;
    }

    String::Utf8Value stringResult(result->ToString());
    log(*stringResult);

    m_isolate->Exit();

}



