//
// Created by bimbobruno on 1/4/17.
//

#ifndef APP_AJV8RUNTIME_H
#define APP_AJV8RUNTIME_H

#include <string>
#include <vector>
#include <v8.h>

#include "MappedFunction.h"

using namespace v8;

class AJV8Runtime {
public:
    AJV8Runtime();
    ~AJV8Runtime();

    void Init();
    void Destroy();

    void executeScript(std::string &source);
    void RegisterFunction(std::string &name, std::function<void()> &fn);

private:
    Isolate *m_isolate;
    Persistent<Context> m_context;

    std::vector<MappedFunction *> *m_functions;

};


#endif //APP_AJV8RUNTIME_H
