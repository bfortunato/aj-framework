"use strict"

const _ = require("underscore")

exports.assertTrue = function(test, msg){
    if (!test) {
        throw "Assertion failure: " + msg || "";
    }
};

exports.assertEquals = function(first, second, msg){
    if (first != second) {
        throw "Assertion failure: " + msg || "";
    }
};

exports.assertNotNull = function(obj, msg){
    if (obj == undefined || obj == null) {
        throw "Assertion failure: " + msg || "";
    }
};

exports.assertNotEmpty = function(obj, msg){
    if (_.isEmpty(obj)) {
        throw "Assertion failure: " + msg || "";
    }
};
