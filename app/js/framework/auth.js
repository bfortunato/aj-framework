"use strict";

const aj = require("../aj");
const http = require("../aj/http");

const services = require("./services");
const preferences = require("./preferences");
const config = require("./config");

const Promise = require("./promise");

class RestSessionService {
    constructor(runtime) {
        this.runtime = runtime;
    }

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


class Session {
    start(mail, password) {
        return new Promise((resolve, reject) => {
            var sessionService = services.get("SessionService");
            sessionService.login(mail, password)
                .then(response => {
                    preferences.load().then(() => {
                        preferences.set("session.type", Session.TYPE_MAIL);
                        preferences.set("session.mail", mail);
                        preferences.set("session.password", password);
                        preferences.save();
                    });
                })
                .catch(() => {
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

    resume() {
        return new Promise((resolve, reject) => {
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
}

Session.TYPE_MAIL = "mail";
Session.TYPE_FACEBOOK = "facebook";


class FacebookLogin {
    login(permissions) {

    }

    setToken(data) {

    }
}

exports.RestSessionService = RestSessionService;
exports.Session = Session;