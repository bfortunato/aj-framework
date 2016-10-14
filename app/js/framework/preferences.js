"use strict";

const storage = require("../aj/storage");

class Preferences {
    static instance() {
        if (!Preferences._instance) {
            Preferences._instance = new Preferences();
        }

        return Preferences._instance;
    }

    constructor() {
        this.path = "preferences.json";
        this.data = {};
    }

    load() {
        logger.i("Loading preferences...");

        this.data = {};

        return new Promise((resolve, reject) => {
            storage.exists(this.path)
                .then(exists => {
                    if (exists) {
                        return storage.readText(this.path).then(content => {
                            logger.i("Preferences:", content);
                            try {
                                this.data = JSON.parse(content);
                            } catch (e) {}
                            resolve(this);
                        })
                    } else {
                        resolve(this);
                    }
                })
                .catch(e => reject(e))
        });
    }

    get(key) {
        return this.data[key];
    }

    set(key, value) {
        this.data[key] = value;
    }

    save() {
        logger.i("Saving preferences", JSON.stringify(this.data));
        return new Promise((resolve, reject) => {
            storage.writeText(this.path, JSON.stringify(this.data))
                .then(() => {
                    resolve();
                })
                .catch(e => reject(e))
        });
    }

    clear() {
        this.data = {};
    }
}

exports.Preferences = Preferences;

exports.load = function() {
    return Preferences.instance().load();
};

exports.get = function(key) {
    return Preferences.instance().get(key);
};

exports.set = function(key, value) {
    return Preferences.instance().set(key, value);
};

exports.save = function() {
    return Preferences.instance().save();
};

exports.clear = function() {
    return Preferences.instance().clear();
};