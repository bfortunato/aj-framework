/**
 * AJ Framework main module. Contains functions to create hybrid applications using flux framework
 * @module aj
 */

"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

exports.createBuffer = createBuffer;
exports.readBuffer = readBuffer;
exports.destroyBuffer = destroyBuffer;

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var _ = require("../libs/underscore");
var Observable = require("./events").Observable;

var __runtime = null;
var __stores = {};
var __actions = {};

var AJRuntime = function () {
    function AJRuntime() {
        _classCallCheck(this, AJRuntime);
    }

    _createClass(AJRuntime, [{
        key: "exec",
        value: function exec() {
            throw "Not implemented";
        }
    }, {
        key: "createBuffer",
        value: function createBuffer(data) {
            throw "Not implemented";
        }
    }, {
        key: "loadBuffer",
        value: function loadBuffer(id) {
            throw "Not implemented";
        }
    }, {
        key: "destroyBuffer",
        value: function destroyBuffer(id) {
            throw "Not implemented";
        }
    }, {
        key: "__trigger",
        value: function __trigger(store, state) {
            throw "Not implemented";
        }
    }], [{
        key: "instance",
        value: function instance() {
            if (!__runtime) {
                throw "Runtime not initialized";
            }
            return __runtime;
        }
    }]);

    return AJRuntime;
}();

if (platform.engine == "node") {
    (function () {
        var vm = require("vm");
        var fs = require("fs");

        var AJWebSocketServerRuntime = function (_AJRuntime) {
            _inherits(AJWebSocketServerRuntime, _AJRuntime);

            function AJWebSocketServerRuntime() {
                _classCallCheck(this, AJWebSocketServerRuntime);

                var _this = _possibleConstructorReturn(this, (AJWebSocketServerRuntime.__proto__ || Object.getPrototypeOf(AJWebSocketServerRuntime)).call(this));

                _this.semaphores = [];

                logger.i("New websocket server runtime created");
                return _this;
            }

            _createClass(AJWebSocketServerRuntime, [{
                key: "init",
                value: function init(options) {
                    var _this2 = this;

                    this.socket = options.socket;
                    if (!this.socket) {
                        throw "Socket is required";
                    }

                    this.socket.on("run", function (action, json, ack) {
                        async(function () {
                            try {
                                var data = JSON.parse(json);
                                _run(action, data);

                                ack();
                            } catch (e) {
                                if (e && e.stack) {
                                    logger.i(e.stack);
                                }
                                logger.e(e);
                            }
                        });
                    });

                    this.socket.on("freeSemaphore", function (id, data) {
                        try {
                            _this2.freeSemaphore(id, data);
                        } catch (e) {
                            if (e && e.stack) {
                                logger.i(e.stack);
                            }
                            logger.e(e);
                        }
                    });

                    this.socket.on("error", function (e) {
                        if (e && e.stack) {
                            logger.i(e.stack);
                        }
                        logger.e(e);
                    });
                }
            }, {
                key: "exec",
                value: function exec(plugin, fn, data) {
                    var _this3 = this;

                    logger.i("Executing plugin ", plugin + "." + fn);

                    return new Promise(function (resolve, reject) {
                        try {
                            _this3.socket.emit("exec", plugin, fn, data, function (result) {
                                resolve(result);
                            });
                        } catch (e) {
                            reject(e);
                        }
                    });
                }
            }, {
                key: "__trigger",
                value: function __trigger(store, state) {
                    var _this4 = this;

                    logger.i("Triggering ", store);

                    return new Promise(function (resolve, reject) {
                        _this4.socket.emit("trigger", store, state, function () {
                            resolve();
                        });
                    });
                }
            }, {
                key: "freeSemaphore",
                value: function freeSemaphore(id, data) {
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
            }, {
                key: "createBuffer",
                value: function createBuffer(data) {
                    var _this5 = this;

                    return new Promise(function (resolve, reject) {
                        _this5.socket.emit("createBuffer", data, function (error, id) {
                            if (!error) {
                                resolve(id);
                            } else {
                                reject();
                            }
                        });
                    });
                }
            }, {
                key: "readBuffer",
                value: function readBuffer(id) {
                    var _this6 = this;

                    return new Promise(function (resolve, reject) {
                        _this6.socket.emit("readBuffer", id, function (error, data) {
                            if (!error) {
                                resolve(data);
                            } else {
                                reject();
                            }
                        });
                    });
                }
            }, {
                key: "destroyBuffer",
                value: function destroyBuffer(id) {
                    var _this7 = this;

                    return new Promise(function (resolve, reject) {
                        _this7.socket.emit("readBuffer", id, function (error) {
                            if (!error) {
                                resolve();
                            } else {
                                reject();
                            }
                        });
                    });
                }
            }]);

            return AJWebSocketServerRuntime;
        }(AJRuntime);

        AJRuntime.create = function () {
            return new AJWebSocketServerRuntime();
        };
    })();
} else {
    (function () {
        var AJNativeServerRuntime = function (_AJRuntime2) {
            _inherits(AJNativeServerRuntime, _AJRuntime2);

            function AJNativeServerRuntime() {
                _classCallCheck(this, AJNativeServerRuntime);

                var _this8 = _possibleConstructorReturn(this, (AJNativeServerRuntime.__proto__ || Object.getPrototypeOf(AJNativeServerRuntime)).call(this));

                logger.i("New native server runtime created");
                return _this8;
            }

            _createClass(AJNativeServerRuntime, [{
                key: "init",
                value: function init(options) {}
            }, {
                key: "run",
                value: function run(action, data) {
                    _run(action, data);
                }
            }, {
                key: "__trigger",
                value: function (_trigger) {
                    function __trigger(_x, _x2) {
                        return _trigger.apply(this, arguments);
                    }

                    __trigger.toString = function () {
                        return _trigger.toString();
                    };

                    return __trigger;
                }(function (store, state) {
                    if (__trigger == undefined) {
                        throw "__trigger function not defined";
                    }

                    logger.i("Triggering ", store);

                    return new Promise(function (resolve, reject) {
                        async(function () {
                            try {
                                __trigger(store, state);
                                resolve();
                            } catch (e) {
                                reject(e);
                            }
                        });
                    });
                })
            }, {
                key: "exec",
                value: function exec(plugin, fn, data) {
                    if (__exec == undefined) {
                        throw "__exec function not defined";
                    }

                    logger.i("Executing plugin", plugin + "." + fn);

                    return new Promise(function (resolve, reject) {
                        async(function () {
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
            }, {
                key: "createBuffer",
                value: function createBuffer(data) {
                    return new Promise(function (resolve, reject) {
                        __buffersManager.create(data, function (error, value) {
                            if (error) {
                                reject(value);
                            } else {
                                resolve(value);
                            }
                        });
                    });
                }
            }, {
                key: "readBuffer",
                value: function readBuffer(id) {
                    return new Promise(function (resolve, reject) {
                        __buffersManager.read(id, function (error, value) {
                            if (error) {
                                reject(value);
                            } else {
                                resolve(value);
                            }
                        });
                    });
                }
            }, {
                key: "destroyBuffer",
                value: function destroyBuffer(id) {
                    return new Promise(function (resolve, reject) {
                        __buffersManager.destroy(id, function (error, value) {
                            if (error) {
                                reject(value);
                            } else {
                                resolve(value);
                            }
                        });
                    });
                }
            }]);

            return AJNativeServerRuntime;
        }(AJRuntime);

        AJRuntime.create = function () {
            return new AJNativeServerRuntime();
        };
    })();
}

var Store = function (_Observable) {
    _inherits(Store, _Observable);

    function Store(type, reducer) {
        _classCallCheck(this, Store);

        var _this9 = _possibleConstructorReturn(this, (Store.__proto__ || Object.getPrototypeOf(Store)).call(this));

        _this9.type = type;
        _this9.reducer = reducer;
        _this9.subscriptions = [];
        return _this9;
    }

    _createClass(Store, [{
        key: "init",
        value: function init(options) {}
    }, {
        key: "subscribe",
        value: function subscribe(owner, subscription) {
            this.subscriptions.push({ owner: owner, subscription: subscription });
        }
    }, {
        key: "unsubscribe",
        value: function unsubscribe(owner) {
            this.subscriptions = _.filter(this.subscriptions, function (s) {
                return s.owner != owner;
            });
        }
    }, {
        key: "trigger",
        value: function trigger(state) {
            var newState = state || this.state;

            _.each(this.subscriptions, function (s) {
                s.subscription(newState);
            });

            return __runtime.__trigger(this.type, newState);
        }
    }, {
        key: "dispatch",
        value: function dispatch(action) {
            if (_.isFunction(this.reducer)) {
                var newState = this.reducer(this.state, action);
                if (newState) {
                    this.state = newState;

                    this.trigger();
                }
            } else {
                logger.w("Cannot dispatch action:", this.type + "." + action);
            }
        }
    }]);

    return Store;
}(Observable);

var Semaphore = function () {
    function Semaphore(action) {
        _classCallCheck(this, Semaphore);

        this.complete = false;
        this.listeners = [];
        this.id = Semaphore.counter++;

        if (action) {
            this.runAction(action);
        }
    }

    _createClass(Semaphore, [{
        key: "runAction",
        value: function runAction(action) {
            var _this10 = this;

            async(function () {
                action();
                _this10.free();
            });
        }
    }, {
        key: "then",
        value: function then(action) {
            this.listeners.push(action);

            if (this.complete) {
                action();
            }

            return this;
        }
    }, {
        key: "free",
        value: function free(data) {
            this.listeners.forEach(function (l) {
                l(data);
            });
            this.complete = true;

            return this;
        }
    }]);

    return Semaphore;
}();

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

    var act = __actions[type] = function (data) {
        fn(data);
    };

    logger.i("Action created:", type);

    return act;
}

function dispatch(action) {
    logger.i("Dispatching action", action);

    _.each(__stores, function (store) {
        try {
            store.dispatch(action);
        } catch (e) {
            if (e && e.stack) {
                logger.i(e.stack);
            }
            logger.e(e);
        }
    });
}

function _run(action, data) {
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
 * @function createRuntime
 * @description Creates a new instance of runtime. Usually used internally by devices runtimes
 * @returns singleton instance of runtime
 */
var createRuntime = exports.createRuntime = createRuntime;

/**
 * @function createStore
 * @description Creates a new singleton instance of store
 * @param {string} type - Name of store to create
 * @param {function} reducer - Store reducer
 * @returns {store} - The newly created store
 */
var createStore = exports.createStore = createStore;

/**
 * @function createAction
 * @Description Creates a new action for the application
 * @param {string} type - Type of action to create
 * @param {function} action - Action to execute
 * @returns {function} The newly created action
 */
var createAction = exports.createAction = createAction;

/**
 * @function dispatch
 * @description Dispatch action to stores, usually called by actions
 * @param {object} data - Data to pass to stores
 */
var dispatch = exports.dispatch = dispatch;

/**
 * @function exec
 * @description Exec a plugin method
 * @param {string} plugin - The plugin
 * @param {method} method - The plugin method to call
 * @param {data} data - Data to pass to plugin
 * @returns {Promise} - A promise of plugin call result
 */
var exec = exports.exec = exec;

/**
 * @function run
 * @description Run specified action. This is not the common method to call actions, but it's necessary for managing actions from
 * devices. On JS side, call actions directly
 * @param {type} type - Type of action to call
 * @param {data} type - Data to pass to action
 */
var _run = _run;

exports.run = _run;
function createBuffer(data) {
    return __runtime.createBuffer(data);
}

function readBuffer(id) {
    return __runtime.readBuffer(id);
}

function destroyBuffer(id) {
    return __runtime.destroyBuffer(id);
}