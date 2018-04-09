"use strict";

var DEBUG = true;

var LOG_LEVEL_INFO = 3;
var LOG_LEVEL_WARNING = 2;
var LOG_LEVEL_ERROR = 1;
var LOG_LEVEL_DISABLED = 0;

var LOG_LEVEL = LOG_LEVEL_INFO;

const app = require("express")();
const http = require("http").Server(app);
const io = require("socket.io")(http);
const ip = require("ip");
const process = require("process");

var logger = {
    i: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.log(message);
    },

    w: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.warn(message);
    },

    e: function(message) {
        if (arguments.length > 1) { message = Array.prototype.join.call(arguments, " ") }
        console.error(message);
    }
};

var platform = {
    engine: "node",
    device: "unknown"
};

var async = function(action) {
    //nodeasync.parallel([action], function() { console.log("async complete"); })
    setTimeout(action, 0);
};

global.logger = logger;
global.platform = platform;
global.__httpClient = require("./server/http");
global.__assetsManager = require("./server/assets");
global.__storageManager = require("./server/storage");
global.__buffersManager = require("./server/buffers");

global.__notify = function(notification, componentId, data) {
    io.emit("notify", notification, componentId, data);
};

global.async = async;

const main = require("./assets/js/main.js");
const aj = require("./assets/js/aj/index.js");

http.listen(3000, function() {
    console.log("AJ debug server listening on " + ip.address() + ":3000");

    var appPath = process.argv[2];

    io.on("connection", function(socket) {
        socket.emit("device", function(json) {
            var result = JSON.parse(json);

            global.device = {
                getScale: function() {
                    return result.scale;
                },

                getHeight: function() {
                    return result.height;
                },

                getWidth: function() {
                    return result.width;
                }
            };

            aj.createRuntime({appPath: appPath, socket: socket});

            main.main();
        });

        socket.on("disconnect", function() {
            process.exit(0);
        })

    });

});
