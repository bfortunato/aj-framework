"use strict";

const aj = require("../aj");
const http = require("../aj/http");

const services = require("./services");
const preferences = require("./preferences");
const config = require("./config");

let _loggedUser;
let _sessionToken;

export const TYPE_MAIL = "MAIL";
export const TYPE_FACEBOOK = "FACEBOOK";

let STOP_OBJ = {};

function stop() {
    return STOP_OBJ;
}

function wrap(r, fn) {
    if (r == STOP_OBJ) {
        return STOP_OBJ;
    } else {
        return fn(r);
    }
}

export function start(mail, password) {
    return new Promise((resolve, reject) => {
        _loggedUser = null;
        _sessionToken = null;

        let data = {};

        var login = services.get("login");
        if (!login) {
            reject("Login service not defined");
            return;
        }

        preferences.load()
            .then(() => { return login(mail, password) })
            .then(token => {
                preferences.set("session.type", TYPE_MAIL);
                preferences.set("session.mail", mail);
                preferences.set("session.password", password);

                _sessionToken = token;
                _loggedUser = {
                    type: TYPE_MAIL,
                    mail: mail,
                    data: data,
                };

                return preferences.save();
            })
            .then((r) => {
                resolve(_loggedUser);
            })
            .catch((e) => {
                _loggedUser = null;
                _sessionToken = null;

                logger.e(e);

                preferences.load()
                    .then(() => {
                        preferences.set("session.type", false);
                        preferences.set("session.mail", false);
                        preferences.set("session.password", false);
                        return preferences.save();
                    })
                    .then(() => {
                        reject("Bad username or password");
                    });

            })
    })
}

export function resume() {
    return new Promise((resolve, reject) => {
        _loggedUser = null;
        _sessionToken = null;

        preferences.load()
            .then(() => {
                var type = preferences.get("session.type");
                var mail = preferences.get("session.mail");
                var password = preferences.get("session.password");

                if ((type == TYPE_MAIL && mail && password)) {
                    return start(mail, password);
                } else {
                    reject("Cannot resume session");
                    return stop();
                }
            })
            .then((r) => {
                return wrap(r, () => {
                    resolve(r);
                });
            })
            .catch(e => {reject(e)});
    });
}

export function getLoggedUser() {
    return _loggedUser;
}

export function isLoggedIn() {
    return _loggedUser != null;
}

export function getSessionToken() {
    logger.i("Current session token: ", _sessionToken);
    logger.i("Current user: ", _loggedUser);

    return _sessionToken;
}

