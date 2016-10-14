"use strict";

var aj = require("./index");

var create = function create(data) {
    return aj.createBuffer(data);
};

var read = function read(id) {
    return aj.readBuffer(id);
};

var destroy = function destroy(id) {
    return aj.destroyBuffer(id);
};

exports.create = create;
exports.read = read;
exports.destroy = destroy;