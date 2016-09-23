const requestify = require("requestify");
const base64 = require("./../app/js/framework/base64");
const http = require("http");
const https = require("https");
const url = require("url");
const request = require("request");

module.exports = {
    request: function(uri, method, data, headers, rawResponse, cb) {
        var method = method || "GET";
        var data = data || {};
        var headers = headers || {};
        var rawResponse = rawResponse || false;

        logger.i("Calling", uri);

        if (rawResponse) {
            request({uri: uri, encoding: null}, function(error, response, body) {
                if (error) {
                    cb(true, null);
                } else {
                    __buffersManager.create(body.toString("base64")).then((id) => {
                        cb(false, id)
                    }).catch((e) => {
                        cb(true, e);
                    });
                }
            });
        } else {
            requestify.request(uri, {
                    method: method,
                    body: data,
                    headers: headers,
                    cookies: {}
                })
                .then(function(response) {
                    logger.i("Response", response.getCode());

                    var body = response.body;

                    cb(false, body);
                })
                .catch((e) => {
                    logger.e(e);
                    cb(true);
                });

        }


    }
};