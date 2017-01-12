const base64 = require("../assets/js/aj/base64");
const http = require("http");
const https = require("https");
const url = require("url");
const request = require("request");

module.exports = {
    request: function(uri, method, data, headers, accept, contentType, rawResponse, cb) {
        var method = method || "GET";
        var headers = headers || {};
        var rawResponse = rawResponse || false;

        logger.i("Calling", method, uri, "with data", JSON.stringify(data));

        headers = headers || {};
        if (accept) { headers["Accept"] = accept; }
        headers["Content-Type"] = contentType || "application/x-www-form-urlencoded";

        logger.i("headers:", JSON.stringify(headers));

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
            request({
                method: method,
                url: uri,
                headers: headers,
                body: data
            }, function(error, response, body) {
                if (error) {
                    logger.i(response)
                    cb(true);
                } else {
                    logger.i("Response", body);
                    cb(false, body);
                }
            });
        }


    }
};