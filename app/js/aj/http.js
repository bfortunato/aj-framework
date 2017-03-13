"use strict";

const assert = require("./assert");
const _ = require("../libs/underscore");

function buildQueryString(obj) {
    var q = "";
    var first = true;
    for (var k in obj) {
        var sep = first ? "" : "&";
        q += sep + k + "=" + encodeURIComponent(obj[k]);
        first = false;
    }

    return q;
}

class HttpClient {
    constructor(url, method, data) {
        this.url = url;
        this.method = method || "GET";
        this.headers = {};
        this.data = data || {};
        this.accept = null;
        this.contentType = null;
        this.rawResponse = false;
    }

    request() {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(this.url, "url is not defined");
                assert.assertNotEmpty(this.method, "method is not defined");

                let data = _.isObject(this.data) ? buildQueryString(this.data) : this.data;
                let headers = this.headers || {};

                logger.i(this.method.toUpperCase() + " " + this.url)
                if (data) {
                    logger.i(data)
                }

                __httpClient.request(this.url, this.method, data, headers, this.accept, this.contentType, this.rawResponse, (error, value) => {
                    if (error) {
                        logger.e("error");
                        reject(error);
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


let request = (url, method, data, headers, accept, contentType, rawResponse) => {
    var method = method || "GET";
    var data = data || {};
    var headers = headers || {};
    var rawResponse = rawResponse || false;

    let client = new HttpClient(url);
    client.method = method;
    client.data = data;
    client.headers = headers;
    client.rawResponse = rawResponse;
    client.accept = accept;
    client.contentType = contentType;

    return client.request();
};


exports.HttpClient = HttpClient;
exports.request = request;

/**
 * Makes a GET request to specified url
 * @param url
 * @param data, can be a string or object. If is an object will be converted in a form encoded string
 * @param headers
 * @returns A promise of result
 */
exports.get = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};

    return request(url, "GET", data, headers, null, null, false);
};

/**
 * Makes a POST request to specified url
 * @param url
 * @param data, can be a string or object. If is an object will be converted in a form encoded string
 * @param headers
 * @returns A promise of result
 */
exports.post = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "POST", data, headers, null, null, false);
};

/**
 * Makes a PUT request to specified url
 * @param url
 * @param data, can be a string or object. If is an object will be converted in a form encoded string
 * @param headers
 * @returns A promise of result
 */
exports.put = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "PUT", data, headers, null, null, false);
};

/**
 * Makes a DELETE request to specified url
 * @param url
 * @param data, can be a string or object. If is an object will be converted in a form encoded string
 * @param headers
 * @returns A promise of result
 */
exports.delete = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "DELETE", data, headers, null, null, false);
};

/**
 * Downloads a file from specified url
 * @param url
 * @param data, can be a string or object. If is an object will be converted in a form encoded string
 * @param headers
 * @returns A promise of result
 */
exports.download = (url, data, headers) => {
    var data = data || {};
    var headers = headers || {};
    return request(url, "GET", data, headers, null, null, true);
};