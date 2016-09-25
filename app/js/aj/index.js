/**
 * AJ Framework main module. Contains functions to create hybrid applications using flux framework
 * @module aj
 */

"use strict";

const _ = require("../framework/underscore")
const Observable = require("../framework/events").Observable;
const Promise = require("../framework/promise");

var __runtime = null;
var __stores = {};
var __actions = {};

class AJRuntime {
    constructor() {

    }

    static instance() {
        if (!__runtime) {
            throw "Runtime not initialized"
        }
        return __runtime;
    }

    exec() {
        throw "Not implemented"
    }

    createBuffer(data) {
        throw "Not implemented"
    }

    loadBuffer(id) {
        throw "Not implemented"
    }

    destroyBuffer(id) {
        throw "Not implemented"
    }

    __trigger(store, state) {
        throw "Not implemented";
    }
}

if (platform.engine == "node") {
    (function() {
        var vm = require("vm");
        var fs = require("fs");

        class AJWebSocketServerRuntime extends AJRuntime {
            constructor() {
                super();

                this.semaphores = [];

                logger.i("New websocket server runtime created");
            }

            init(options) {
                this.socket =  options.socket;
                if (!this.socket) {
                    throw "Socket is required";
                }

                this.socket.on("run", (action, json, ack) => {
                    async(() => {
                        try {
                            var data = JSON.parse(json);
                            run(action, data);

                            ack();
                        } catch (e) {
                            if (e && e.stack) { logger.i(e.stack); }
                            logger.e(e);
                        }
                    });
                });

                this.socket.on("freeSemaphore", (id, data) => {
                    try {
                        this.freeSemaphore(id, data);
                    } catch(e) {
                        if (e && e.stack) { logger.i(e.stack); }
                        logger.e(e);
                    }
                });

                this.socket.on("error", (e) => {
                    if (e && e.stack) { logger.i(e.stack); }
                    logger.e(e);
                });
            }

            exec(plugin, fn, data) {
                logger.i("Executing plugin ", plugin + "." + fn);

                return new Promise((resolve, reject) => {
                    try {
                        this.socket.emit("exec", plugin, fn, data, (result) => {
                            resolve(result);
                        });
                    } catch (e) {
                        reject(e);
                    }
                })
            }

            __trigger(store, state) {
                logger.i("Triggering ", store);

                return new Promise((resolve, reject) => {
                    this.socket.emit("trigger", store, state, function() {
                        resolve();
                    });
                });

            }

            freeSemaphore(id, data) {
                var index = -1;
                var found = false;
                for (var i = 0; i < this.semaphores.length; i++) {
                    index++;
                    var semaphore = this.semaphores[i];

                    if (semaphore.id == id) {
                        found = true;
                        semaphore.free(data);
                        break;
                    }
                }

                if (found) {
                    this.semaphores.splice(index, 1);
                    logger.i("Semaphore destroyed:", semaphore.name);
                }
            }

            createBuffer(data) {
                return new Promise((resolve, reject) => {
                    this.socket.emit("createBuffer", data, (error, id) => {
                        if (!error) {
                            resolve(id)
                        } else {
                            reject()
                        }
                    })
                })
            };

            readBuffer(id) {
                return new Promise((resolve, reject) => {
                    this.socket.emit("readBuffer", id, (error, data) => {
                        if (!error) {
                            resolve(data)
                        } else {
                            reject()
                        }
                    })
                })
            };

            destroyBuffer(id) {
                return new Promise((resolve, reject) => {
                    this.socket.emit("readBuffer", id, (error) => {
                        if (!error) {
                            resolve()
                        } else {
                            reject()
                        }
                    })
                })
            };

        }

        AJRuntime.create = function() {
            return new AJWebSocketServerRuntime();
        }
    })();
} else {
    (function() {
        class AJNativeServerRuntime extends AJRuntime {
            constructor() {
                super();

                logger.i("New native server runtime created");
            }

            init(options) {

            }

            run(action, data) {
                run(action, data);
            }

            __trigger(store, state) {
                if (__trigger == undefined) {
                    throw "__trigger function not defined";
                }

                logger.i("Triggering ", store);

                return new Promise((resolve, reject) => {
                    async(() => {
                        try {
                            __trigger(store, state);
                            resolve();
                        } catch (e) {
                            reject(e);
                        }
                    });
                });
            }

            exec(plugin, fn, data) {
                if (__exec == undefined) {
                    throw "__exec function not defined";
                }

                logger.i("Executing plugin", plugin + "." + fn);

                return new Promise((resolve, reject) => {
                    async(() => {
                        try {
                            var result = __exec(plugin, fn, data);
                            logger.i("Plugin called with res:", result);

                            resolve(result);
                        } catch (e) {
                            reject(e);
                        }
                    });
                });
            }

            createBuffer(data) {
                return new Promise((resolve, reject) => {
                    __buffersManager.create(data, function(error, value) {
                        if (error) {
                            reject(value);
                        } else {
                            resolve(value);
                        }
                    });
                })
            };

            readBuffer(id) {
                return new Promise((resolve, reject) => {
                    __buffersManager.read(id, function(error, value) {
                        if (error) {
                            reject(value);
                        } else {
                            resolve(value);
                        }
                    });
                })
            };

            destroyBuffer(id) {
                return new Promise((resolve, reject) => {
                    __buffersManager.destroy(id, function(error, value) {
                        if (error) {
                            reject(value);
                        } else {
                            resolve(value);
                        }
                    });
                })
            };

        }

        AJRuntime.create = function() {
            return new AJNativeServerRuntime();
        }
    })();
}


class Store extends Observable {
    constructor(type, reducer) {
        super();

        this.type = type;
        this.reducer = reducer;
    }

    init(options) {}

    trigger(state) {
        if (state == undefined) {
            return __runtime.__trigger(this.type, this.state);
        } else {
            return __runtime.__trigger(this.type, state);
        }

    }

    dispatch(action) {
        if (_.isFunction(this.reducer)) {
            var newState = this.reducer(this.state, action);
            if (newState) {
                this.state = newState;

                this.trigger()
            }
        } else {
            logger.w("Cannot dispatch action:", this.type + "." + action);
        }
    }
}



class Semaphore {
    constructor(action) {
        this.complete = false;
        this.listeners = [];
        this.id = Semaphore.counter++;

        if (action) {
            this.runAction(action);
        }

    }

    runAction(action) {
        async(() => {
            action();
            this.free()
        })
    }

    then(action) {
        this.listeners.push(action);

        if (this.complete) {
            action();
        }

        return this;
    }

    free(data) {
        this.listeners.forEach((l) => { l(data); });
        this.complete = true;

        return this;
    }

}

Semaphore.counter = 1;

function createRuntime(options) {
    __runtime = AJRuntime.create();
    __runtime.init(options);

    return __runtime;
};

function createStore(type, reducer) {
    if (_.has(__stores, type)) {
        throw "Cannot create store " + type + ". Only one instance of store is allowed";
    }

    var store = new Store(type, reducer);
    __stores[type] = store;

    logger.i("Store created:", type);
}

function createAction(type, fn) {
    if (_.has(__actions, type)) {
        throw "Cannot create action " + type + ". Already created";
    }

    var act = __actions[type] = (data) => {
        fn(data);
    };

    logger.i("Action created:", type);

    return act;
}

function dispatch(action) {
    logger.i("Dispatching action", action);

    _.each(__stores, (store) => {
        try {
            store.dispatch(action);
        } catch (e) {
            if (e && e.stack) { logger.i(e.stack); }
            logger.e(e);
        }
    });
}

function run(action, data) {
    logger.i("Running action", action);

    if (_.has(__actions, action)) {
        __actions[action](data);
    } else {
        logger.w("Cannot find action: " + action);
    }
}

function exec(plugin, fn, data) {
    return __runtime.exec(plugin, fn, data);
}

/**
 * Creates a new instance of runtime. Usually used internally by devices runtimes
 * @returns singleton instance of runtime
 */
export var createRuntime = createRuntime;

/**
 * Creates a new singleton instance of store
 * @param {string} type - Name of store to create
 * @param {function} reducer - Store reducer
 */
export var createStore = createStore;

/**
 * Creates a new action for the application
 * @param {string} type - Type of action to create
 * @param {function} action - Action to execute
 * @returns {function} the newly created action
 */
export var createAction = createAction;

/**
 * Dispatch action to stores, usually called by actions
 * @param {object} data - Data to pass to stores
 */
export var dispatch = dispatch;

/**
 * Exec a plugin action
 * @param {string} plugin - The plugin
 * @param {action} action - The plugin action to call
 * @param {data} data - Data to pass to plugin
 */
export var exec = exec;

/**
 * Run specified action. This is not the common method to call actions, but it's necessary for managing actions from
 * devices. On JS side, call actions directly
 * @param {type} type - Type of action to call
 * @param {data} type - Data to pass to action
 */
export var run = run;
