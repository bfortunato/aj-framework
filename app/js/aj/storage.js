"use strict";

const assert = require("./assert");
const path = require("./path");
const base64 = require("./base64");

class StorageManager {
    constructor() {
        if (__storageManager == undefined) {
            throw "__storageManager undefined";
        }
    }

    /*
    Read text files and return a promise with the result as string
     */
    readText(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Reading text file", path);

                __storageManager.readText(path, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

    /*
     Read binary files and return a promise with the result as byte array (transfer with native using base64)
     */
    read(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Reading binary file", path);

                __storageManager.read(path, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        var bytes = base64.decode(value);
                        resolve(bytes);
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

    /*
     Write text files and return a promise with the result of operation
     */
    writeText(path, content) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Writing text file", path);

                __storageManager.writeText(path, content, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

    /*
     Write binary files and return a promise with the result of operation
     */
    write(path, bytes) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Writing binary file", path);

                var content = base64.encode(bytes);
                __storageManager.write(path, content, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

    /*
     Delete a file and return a promise with the result of operation
     */
    delete(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Deleting file", path);

                __storageManager.delete(path, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

    /*
     Check file existence and return a promise with the result of operation
     */
    exists(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Checking file existence", path);

                __storageManager.exists(path, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value)
                    } else {
                        resolve(value)
                    }
                })
            } catch (e) {
                logger.e(e);
                reject(e);
            }
        });
    }

}

var instance = new StorageManager();

/**
 * Reads text of file in specified path
 * @param path
 * @returns A promise with text result
 */
exports.readText = function(path) {
    return instance.readText(path);
};

/**
 * Reads binary file from specified path.
 * @param path
 * @returns A promise of result
 */
exports.read = function(path) {
    return instance.read(path);
};

/**
 * Writes text contents in specified file
 * @param path
 * @param content
 * @returns A promise of result
 */
exports.writeText = function(path, content) {
    return instance.writeText(path, content);
};

/**
 * Writes bytes contents in speified file
 * @param path
 * @param bytes
 * @returns A promise of result
 */
exports.write = function(path, bytes) {
    return instance.write(path, bytes);
};

/**
 * Deletes specified file from device storage
 * @param path
 * @returns A promise of result
 */
exports.delete = function(path) {
    return instance.delete(path);
};

/**
 * Check if specified file exists in device storage
 * @param path
 * @returns A promise of result
 */
exports.exists = function(path) {
    return instance.exists(path);
};
