"use strict";

const aj = require("./index");

var create = function(data) {
    return aj.createBuffer(data);
};

var read = function(id) {
    return aj.readBuffer(id);
};

var destroy = function(id) {
    return aj.destroyBuffer(id);
};

exports.create = create;
exports.read = read;
exports.destroy = destroy;