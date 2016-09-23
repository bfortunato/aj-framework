"use strict";

class ServiceLocator {
    static instance() {
        if (!ServiceLocator.__instance) {
            ServiceLocator.__instance = new ServiceLocator("applica");
        }

        return ServiceLocator.__instance;
    }

    constructor(pwd) {
        if (pwd != "applica") {
            throw new Error("ServiceLocator is a singleton. Please use ServiceLocator.instance()");
        }

        this.services = {};
    }

    register(type, builder) {
        this.services[type] = builder;
    }

    getService(type) {
        if (this.services[type] && typeof(this.services[type] == "function")) {
            return this.services[type]();
        }

        throw new Error("Service not registered: " + type);
    }
}

exports.ServiceLocator = ServiceLocator;

exports.get = function(type) {
    return ServiceLocator.instance().getService(type);
};