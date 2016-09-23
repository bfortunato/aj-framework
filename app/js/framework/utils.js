"use strict";

exports.isEmpty = function(obj) {
    if (obj == undefined) { return true; }
    if (obj == null) { return true; }

    if (obj instanceof Array) {
        return obj.length == 0;
    }

    if (obj == "") {
        return true;
    }

    return false;
};

exports.isNotEmpty = function(obj) {
    return !exports.isEmpty(obj);
};