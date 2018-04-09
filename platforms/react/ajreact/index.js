"use strict";

global.DEBUG = true;

global.LOG_LEVEL_INFO = 3;
global.LOG_LEVEL_WARNING = 2;
global.LOG_LEVEL_ERROR = 1;
global.LOG_LEVEL_DISABLED = 0;

global.LOG_LEVEL = LOG_LEVEL_INFO;

var logger = {
    i: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.log(message);
    },

    w: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.log("W:" + message);
    },

    e: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.log("E:" + message);
    }
};

var platform = {
    engine: "react",
    device: "unknown"
};

var async = function(action) {
    //nodeasync.parallel([action], function() { console.log("async complete"); })
    setTimeout(action, 0);
};

global.logger = logger;
global.platform = platform;
global.__httpClient = require("./http");
global.__assetsManager = require("./assets");
global.__storageManager = require("./storage");
global.__buffersManager = require("./buffers");

global.__notify = function(notification, componentId, data) {
    io.emit("notify", notification, componentId, data);
};

global.async = async;
