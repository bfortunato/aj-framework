/**
 * AJ Framework assets manager
 * @module aj/assets
 */

"use strict";

import * as assert from "../framework/assert"
import * as path from "../framework/path"

class AssetsManager {
    constructor() {
        if (__assetsManager == undefined) {
            throw "__assetsManager undefined";
        }
    }

    load(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                logger.i("Loading asset", path);

                __assetsManager.load(path, (error, value) => {
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

    exists(path) {
        return new Promise((resolve, reject) => {
            try {
                assert.assertNotEmpty(path, "path is not defined");

                __assetsManager.load(path, (error, value) => {
                    if (error) {
                        logger.e(value);
                        reject(value);
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

    loadImage(imagePath) {
        var scale = device.getScale();

        var name = path.removeExt(imagePath);
        var ext = path.ext(imagePath);

        var scaledName = `${name}@${scale}x${ext}`;

        return this.load(scaledName);
    }
}

var instance = new AssetsManager();

/**
 * @function load
 * @description Load asset from device (async)
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
export const load = function(path) {
    return instance.load(path);
};

/**
 * @function exists
 * @description Load asset from device (async)
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
export const exists = function(path) {
    return instance.exists(path);
};

/**
 * @function save
 * @description Save asset to device (async)
 * @param {string} path - The path of asset to save
 * @param {buffer} data - Buffer id that contains data
 * @returns {Promise} - A promise of the result
 */
export const save = function(path, data) {
    return instance.save(path, data);
};

/**
 * @function remove
 * @description Remove asset from device (async)
 * @param path - The path of asset to remove
 * @returns {Promise} - A promise of the result
 */
export const remove = function(path) {
    return instance.remove(path);
};

/**
 * @function image
 * @description Load image asset from device, with density support
 * @param path - The path of image to load
 * @returns {Promise} - A promise of the result
 */
export const image = function(path) {
    return instance.loadImage(path);
};
