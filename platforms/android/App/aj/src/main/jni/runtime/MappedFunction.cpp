//
// Created by bruno fortunato on 08/01/2017.
//

#include "MappedFunction.h"


MappedFunction::MappedFunction(const std::string &name, const std::function<void()> &fn)
        : m_name(name), m_fn(fn) {

}

MappedFunction::~MappedFunction() {

}

void MappedFunction::Call() {
    m_fn();
}

