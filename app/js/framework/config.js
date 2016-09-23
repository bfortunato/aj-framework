const config = require("../config");

const _ = require("./underscore");

exports.get = function(key) {
    if (_.has(config, key)) {
        return config[key];
    } else {
        throw "Config not found: " + key;
    }
};