//
// Created by bruno fortunato on 08/01/2017.
//

#ifndef APP_JAVAFUNCTION_H
#define APP_JAVAFUNCTION_H

#include <jni.h>
#include <string>

class JavaFunction {
public:
    JavaFunction(JavaVM *jvm, const std::string &name, jobject instance);

    ~JavaFunction();

    void Call();

private:
    JavaVM *m_jvm;
    std::string m_name;
    jobject m_instance;
    jmethodID m_methodId;
};


#endif //APP_JAVAFUNCTION_H
