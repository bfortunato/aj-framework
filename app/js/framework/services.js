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

    register(type, fn) {
        this.services[type] = fn;
    }

    getService(type) {
        if (this.services[type]) {
            return this.services[type];
        }

        throw new Error("Service not registered: " + type);
    }
}

export function get(type) {
    return ServiceLocator.instance().getService(type);
}

export function register(type, fn) {
    ServiceLocator.instance().register(type, fn);
}