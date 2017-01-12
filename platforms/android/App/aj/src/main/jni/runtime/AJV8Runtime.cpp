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

void Print(const v8::FunctionCallbackInfo<v8::Value>& args);
void CallFunction(const v8::FunctionCallbackInfo<v8::Value>& args);

AJV8Runtime::AJV8Runtime() {
    m_functions = new std::vector<MappedFunction *>();
}

void AJV8Runtime::Init() {
    log("Initializing AJV8Runtime platform...");

    Platform* platform = platform::CreateDefaultPlatform();
    V8::InitializePlatform(platform);
    V8::Initialize();

    log("Initializing AJV8Runtime isolate...");
    Isolate::CreateParams create_params;
    create_params.array_buffer_allocator = ArrayBuffer::Allocator::NewDefaultAllocator();
    m_isolate = Isolate::New(create_params);

    log("Creating scope for context");

    Isolate::Scope isolate_scope(m_isolate);
    HandleScope handle_scope(m_isolate);

    log("Initializing AJV8Runtime context...");

    Local<ObjectTemplate> global = ObjectTemplate::New(m_isolate);
    global->Set(
            String::NewFromUtf8(m_isolate, "print", String::kNormalString),
            FunctionTemplate::New(m_isolate, Print)
    );

    auto context = Context::New(m_isolate, nullptr, global);
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

void AJV8Runtime::executeScript(std::string &source) {
    log("Executing script: " + source);

    m_isolate->Enter();

    Locker locker(m_isolate);
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

AJV8Runtime::~AJV8Runtime() {

}

void AJV8Runtime::RegisterFunction(std::string &name, std::function<void()> &fn) {
    log("Registering function: " + name);

    m_isolate->Enter();

    Locker locker(m_isolate);
    HandleScope handle_scope(m_isolate);
    Local<Context> context = m_context.Get(m_isolate);
    Context::Scope context_scope(context);

    auto mappedFunction = new MappedFunction(name, fn);
    m_functions->push_back(mappedFunction);

    Local<FunctionTemplate> fnTemplate = FunctionTemplate::New(m_isolate, CallFunction, External::New(m_isolate, mappedFunction));

    Local<Object> global = context->Global();
    global->Set(
            String::NewFromUtf8(m_isolate, name.c_str(), String::kNormalString),
            fnTemplate->GetFunction()
    );

    log("Registered function: " + name);
}

void CallFunction(const v8::FunctionCallbackInfo<v8::Value>& args) {
    MappedFunction *mappedFunction = (MappedFunction *) args.Data().As<External>()->Value();
    mappedFunction->Call();
}


void Print(const v8::FunctionCallbackInfo<v8::Value>& args) {
    bool first = true;
    for (int i = 0; i < args.Length(); i++) {
        v8::HandleScope handle_scope(args.GetIsolate());
        if (first) {
            first = false;
        } else {
            log(" ");
        }
        v8::String::Utf8Value str(args[i]);
        auto cstr = std::string(*str);
        log(cstr);
    }
    printf("\n");
    fflush(stdout);
}