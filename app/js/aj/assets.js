/**
 * AJ Framework assets manager
 * @module aj/assets
 */

"use strict";

import * as assert from "../framework/assert"
import * as path from "../framework/path"

import Promise from "../framework/promise"

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
 * Load asset from device
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
export const load = function(path) {
    return instance.load(path);
};

/**
 * Load asset from device
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
export const exists = function(path) {
    return instance.exists(path);
};

export const save = function(path, data) {
    return instance.save(path, data);
};

export const remove = function(path) {
    return instance.remove(path);
};

export const image = function(path) {
    return instance.loadImage(path);
};
