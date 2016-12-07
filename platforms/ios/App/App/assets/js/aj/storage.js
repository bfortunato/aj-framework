"use strict";

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var assert = require("./assert");
var path = require("./path");
var base64 = require("./base64");

var StorageManager = function () {
    function StorageManager() {
        _classCallCheck(this, StorageManager);

        if (__storageManager == undefined) {
            throw "__storageManager undefined";
        }
    }

    /*
    Read text files and return a promise with the result as string
     */


    _createClass(StorageManager, [{
        key: "readText",
        value: function readText(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Reading text file", path);

                    __storageManager.readText(path, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
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

        /*
         Read binary files and return a promise with the result as byte array (transfer with native using base64)
         */

    }, {
        key: "read",
        value: function read(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Reading binary file", path);

                    __storageManager.read(path, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
                        } else {
                            var bytes = base64.decode(value);
                            resolve(bytes);
                        }
                    });
                } catch (e) {
                    logger.e(e);
                    reject(e);
                }
            });
        }

        /*
         Write text files and return a promise with the result of operation
         */

    }, {
        key: "writeText",
        value: function writeText(path, content) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Writing text file", path);

                    __storageManager.writeText(path, content, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
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

        /*
         Write binary files and return a promise with the result of operation
         */

    }, {
        key: "write",
        value: function write(path, bytes) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Writing binary file", path);

                    var content = base64.encode(bytes);
                    __storageManager.write(path, content, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
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

        /*
         Delete a file and return a promise with the result of operation
         */

    }, {
        key: "delete",
        value: function _delete(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Deleting file", path);

                    __storageManager.delete(path, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
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

        /*
         Check file existence and return a promise with the result of operation
         */

    }, {
        key: "exists",
        value: function exists(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Checking file existence", path);

                    __storageManager.exists(path, function (error, value) {
                        if (error) {
                            logger.e(value);
                            reject(value);
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

    return StorageManager;
}();

var instance = new StorageManager();

/**
 * Reads text of file in specified path
 * @param path
 * @returns A promise with text result
 */
exports.readText = function (path) {
    return instance.readText(path);
};

/**
 * Reads binary file from specified path.
 * @param path
 * @returns A promise of result
 */
exports.read = function (path) {
    return instance.read(path);
};

/**
 * Writes text contents in specified file
 * @param path
 * @param content
 * @returns A promise of result
 */
exports.writeText = function (path, content) {
    return instance.writeText(path, content);
};

/**
 * Writes bytes contents in speified file
 * @param path
 * @param bytes
 * @returns A promise of result
 */
exports.write = function (path, bytes) {
    return instance.write(path, bytes);
};

/**
 * Deletes specified file from device storage
 * @param path
 * @returns A promise of result
 */
exports.delete = function (path) {
    return instance.delete(path);
};

/**
 * Check if specified file exists in device storage
 * @param path
 * @returns A promise of result
 */
exports.exists = function (path) {
    return instance.exists(path);
};