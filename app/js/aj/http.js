"use strict";

var assert = require("../framework/assert");
var Promise = require("../framework/promise");

class HttpClient {
    constructor(url, method, data) {
        this.url = url;
        this.method = method || "GET";
        this.headers = {};
        this.data = data || {};
        this.rawResponse = false;
    }

    request() {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(this.url, "url is not defined");
                assert.assertNotEmpty(this.method, "method is not defined");

                let data = this.data || {};
                let headers = this.headers || {};

                __httpClient.request(this.url, this.method, data, headers, this.rawResponse, (error, value) => {
                    if (error) {
                        logger.e("error");
                        reject("error.connection");
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        })
    }
}


let request = (url, method, data, headers, rawResponse) => {
    var method = method || "GET";
    var data = data || {};
    var headers = headers || {};
    var rawResponse = rawResponse || false;

    let client = new HttpClient(url);
    client.method = method;
    client.data = data;
    client.headers = headers;
    client.rawResponse = rawResponse;

    return client.request();
};


exports.HttpClient = HttpClient;
exports.request = request;

exports.get = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};

    return request(url, "GET", data, headers);
};

exports.post = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "POST", data, headers);
};

exports.put = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "PUT", data, headers);
};

exports.delete = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "DELETE", data, headers);
};

exports.download = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "GET", data, headers, true);
};