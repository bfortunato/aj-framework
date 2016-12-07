"use strict";

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var assert = require("./assert");
var _ = require("../libs/underscore");

function buildQueryString(obj) {
    var q = "";
    var first = true;
    for (var k in obj) {
        var sep = first ? "" : "&";
        q += sep + k + "=" + obj[k];
        first = false;
    }

    return q;
}

var HttpClient = function () {
    function HttpClient(url, method, data) {
        _classCallCheck(this, HttpClient);

        this.url = url;
        this.method = method || "GET";
        this.headers = {};
        this.data = data || {};
        this.accept = null;
        this.contentType = null;
        this.rawResponse = false;
    }

    _createClass(HttpClient, [{
        key: "request",
        value: function request() {
            var _this = this;

            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(_this.url, "url is not defined");
                    assert.assertNotEmpty(_this.method, "method is not defined");

                    var data = _.isObject(_this.data) ? buildQueryString(_this.data) : _this.data;
                    var headers = _this.headers || {};

                    __httpClient.request(_this.url, _this.method, data, headers, _this.accept, _this.contentType, _this.rawResponse, function (error, value) {
                        if (error) {
                            logger.e("error");
                            reject(error);
                        } else {
                            resolve(value);
                        }
                    });
                } catch (e) {
                    logger.e(e);
                    reject(e);
                }
            });
        }
    }]);

    return HttpClient;
}();

var request = function request(url, method, data, headers, accept, contentType, rawResponse) {
    var method = method || "GET";
    var data = data || {};
    var headers = headers || {};
    var rawResponse = rawResponse || false;

    var client = new HttpClient(url);
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
exports.get = function (url, data, headers) {
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
exports.post = function (url, data, headers) {
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
exports.put = function (url, data, headers) {
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
exports.delete = function (url, data, headers) {
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
exports.download = function (url, data, headers) {
    var data = data || {};
    var headers = headers || {};
    return request(url, "GET", data, headers, null, null, true);
};