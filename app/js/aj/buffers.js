"use strict";

const aj = require("./index");

var create = function(data) {
    return aj.AJRuntime.instance().createBuffer(data);
};

var read = function(id) {
    return aj.AJRuntime.instance().readBuffer(id);
};

var destroy = function(id) {
    return aj.AJRuntime.instance().destroyBuffer(id);
};

exports.create = create;
exports.read = read;
exports.destroy = destroy;