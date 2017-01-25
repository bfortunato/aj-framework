/**
 * AJ Framework main module. Contains functions to create hybrid applications using flux framework
 * @module aj
 */

"use strict";

const _ = require("../libs/underscore")
const Observable = require("./events").Observable;

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

if (platform.test) {
    (function() {
        var vm = require("vm");
        var fs = require("fs");
        var buffers = {}
        var bufferId = 0

        class AJTestRuntime extends AJRuntime {
            constructor() {
                super();

                this.semaphores = [];

                logger.i("New test runtime created");
            }

            init(options) {

            }

            exec(plugin, fn, data) {
                logger.i("Executing plugin ", plugin + "." + fn);

                return new Promise((resolve, reject) => {
                    resolve({})
                })
            }

            __trigger(store, state) {
                logger.i("Triggering", store, "with state", JSON.stringify(state));
            }

            createBuffer(data) {
                return new Promise((resolve, reject) => {
                    let id = ++bufferId
                    buffers[id] = data
                    resolve(id)
                })
            };

            readBuffer(id) {
                return new Promise((resolve, reject) => {
                    resolve(buffers[id])
                })
            };

            destroyBuffer(id) {
                return new Promise((resolve, reject) => {
                    delete buffers[id]
                    resolve()
                })
            };

        }

        AJRuntime.create = function() {
            return new AJTestRuntime();
        }
    })();
}
else if (platform.engine == "node") {
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
                    this.socket.emit("exec", plugin, fn, data, (error, result) => {
                        if (!error) {
                            resolve(result);
                        } else {
                            reject(result);
                        }
                    });
                })
            }

            __trigger(store, state) {
                logger.i("Triggering", store, "with state", JSON.stringify(state));

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

                logger.i("Triggering", store, "with state", JSON.stringify(state));

                return new Promise((resolve, reject) => {
                    try {
                        __trigger(store, state);
                        resolve();
                    } catch (e) {
                        reject(e);
                    }
                });
            }

            exec(plugin, fn, data) {
                if (__exec == undefined) {
                    throw "__exec function not defined"
                }

                logger.i("Executing plugin", plugin + "." + fn)

                return new Promise((resolve, reject) => {
                    __exec(plugin, fn, data, function(error, value) {
                        if (error) {
                            reject(value)
                        } else {
                            resolve(value)
                        }
                    })
                })
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
        this.subscriptions = [];
    }

    init(options) {}

    subscribe(owner, subscription) {
        this.subscriptions.push({owner, subscription});
    }

    unsubscribe(owner) {
        this.subscriptions = _.filter(this.subscriptions, s => s.owner != owner);
    }

    trigger(state) {
        let newState = state || this.state;

        _.each(this.subscriptions, s => {
            s.subscription(newState);
        });

        return __runtime.__trigger(this.type, newState);
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

    return store;
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
    logger.i("Dispatching action", JSON.stringify(action));

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
    logger.i("Running action", action, "with data", JSON.stringify(data));

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
 * @function createRuntime
 * @description Creates a new instance of runtime. Usually used internally by devices runtimes
 * @returns singleton instance of runtime
 */
export var createRuntime = createRuntime;

/**
 * @function createStore
 * @description Creates a new singleton instance of store
 * @param {string} type - Name of store to create
 * @param {function} reducer - Store reducer
 * @returns {store} - The newly created store
 */
export var createStore = createStore;

/**
 * @function createAction
 * @Description Creates a new action for the application
 * @param {string} type - Type of action to create
 * @param {function} action - Action to execute
 * @returns {function} The newly created action
 */
export var createAction = createAction;

/**
 * @function dispatch
 * @description Dispatch action to stores, usually called by actions
 * @param {object} data - Data to pass to stores
 */
export var dispatch = dispatch;

/**
 * @function exec
 * @description Exec a plugin method
 * @param {string} plugin - The plugin
 * @param {method} method - The plugin method to call
 * @param {data} data - Data to pass to plugin
 * @returns {Promise} - A promise of plugin call result
 */
export var exec = exec;

/**
 * @function run
 * @description Run specified action. This is not the common method to call actions, but it's necessary for managing actions from
 * devices. On JS side, call actions directly
 * @param {type} type - Type of action to call
 * @param {data} type - Data to pass to action
 */
export var run = run;


export function createBuffer(data) {
    return __runtime.createBuffer(data);
}

export function readBuffer(id) {
    return __runtime.readBuffer(id);
}

export function destroyBuffer(id) {
    return __runtime.destroyBuffer(id);
}