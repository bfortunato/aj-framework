"use strict";

const aj = require("../aj");
const http = require("../aj/http");

const services = require("./services");
const preferences = require("./preferences");
const config = require("./config");

const Promise = require("./promise");

var loggedUser = null;

export const TYPE_MAIL = "MAIL";
export const TYPE_FACEBOOK = "FACEBOOK";

class RestSessionService {
    login(mail, password) {
        return new Promise((resolve, reject) => {
            http.post(config.get("login.url"), {mail, password})
                .then(response => {
                    if (response.error) {
                        throw new Error(response.message);
                    }

                    resolve(response.value);
                })
                .catch(e => {
                    reject(e);
                })
        });
    }
}

export function start(mail, password) {
    return new Promise((resolve, reject) => {
        loggedUser = null;

        var sessionService = services.get("SessionService");
        sessionService.login(mail, password)
            .then(response => {
                preferences.load().then(() => {
                    preferences.set("session.type", TYPE_MAIL);
                    preferences.set("session.mail", mail);
                    preferences.set("session.password", password);
                    preferences.save();
                });

                loggedUser = {
                    type: TYPE_MAIL,
                    mail: mail,
                    data: response,
                }
            })
            .catch(() => {
                loggedUser = null;

                preferences.load().then(() => {
                    preferences.set("session.type", false);
                    preferences.set("session.mail", false);
                    preferences.set("session.password", false);
                    preferences.save();
                });
                reject("Cannot login");
            })
    })
}

export function resume() {
    return new Promise((resolve, reject) => {
        loggedUser = null;

        preferences.load().then((preferences) => {
            var type = preferences.get("session.type");
            var mail = preferences.get("session.mail");
            var password = preferences.get("session.password");

            if (type == Session.TYPE_MAIL && mail && password) {
                return this.start(mail, password);
            } else {
                reject("Cannot resume session");
            }
        });
    });
}

export function getLoggedUser() {
    return loggedUser;
}

export function isLoggedIn() {
    return loggedUser != null;
}

export { RestSessionService };
