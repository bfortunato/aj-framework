"use strict";

const utils = require("./utils");

exports.ext = function(path) {
    if (utils.isEmpty(path)) {
        return "";
    }

    var index = path.lastIndexOf(".");
    if (index == -1) {
        return "";
    }

    return path.substring(index);
};

exports.removeExt = function(path) {
    if (utils.isEmpty(path)) {
        return path;
    }

    var index = path.lastIndexOf(".");
    if (index == -1) {
        return path;
    }

    return path.substring(0, index);
};