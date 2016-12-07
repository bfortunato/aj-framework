//
// Created by bimbobruno on 1/4/17.
//

#ifndef APP_AJV8RUNTIME_H
#define APP_AJV8RUNTIME_H

#include <string>
#include <v8.h>

using namespace v8;

class AJV8Runtime {
public:
    AJV8Runtime();

    void Init();
    void Destroy();

    void executeScript(std::string source);

private:
    Isolate *m_isolate;
    Persistent<Context> m_context;

};


#endif //APP_AJV8RUNTIME_H
