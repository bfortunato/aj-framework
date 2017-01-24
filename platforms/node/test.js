"use strict";

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
    device: "unknown",
    test: true
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


global.async = async;

const aj = require("./assets/js/aj/index.js");
const main = require("./assets/js/main.js").main;

aj.createRuntime()
main()

aj.run("INIT");

setTimeout(function() {
    aj.run("LOGIN", {server: "http://localhost:8080", mail: "mail", password: "password"})
}, 1000);

