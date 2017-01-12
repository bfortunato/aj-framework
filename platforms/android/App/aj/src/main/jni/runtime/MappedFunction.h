//
// Created by bruno fortunato on 08/01/2017.
//

#ifndef APP_MAPPEDFUNCTION_H
#define APP_MAPPEDFUNCTION_H

#include <string>
#include <functional>

class MappedFunction {
public:
    MappedFunction(const std::string &name, const std::function<void()> &fn);
    ~MappedFunction();

    void Call();

private:
    std::string m_name;
    std::function<void()> m_fn;
};


#endif //APP_MAPPEDFUNCTION_H
