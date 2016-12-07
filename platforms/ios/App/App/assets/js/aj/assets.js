/**
 * AJ Framework assets manager
 * @module aj/assets
 */

"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.image = exports.remove = exports.save = exports.exists = exports.load = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _assert = require("./assert");

var assert = _interopRequireWildcard(_assert);

var _path = require("./path");

var path = _interopRequireWildcard(_path);

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var AssetsManager = function () {
    function AssetsManager() {
        _classCallCheck(this, AssetsManager);

        if (__assetsManager == undefined) {
            throw "__assetsManager undefined";
        }
    }

    _createClass(AssetsManager, [{
        key: "load",
        value: function load(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    logger.i("Loading asset", path);

                    __assetsManager.load(path, function (error, value) {
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
    }, {
        key: "exists",
        value: function exists(path) {
            return new Promise(function (resolve, reject) {
                try {
                    assert.assertNotEmpty(path, "path is not defined");

                    __assetsManager.load(path, function (error, value) {
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
    }, {
        key: "loadImage",
        value: function loadImage(imagePath) {
            var scale = device.getScale();

            var name = path.removeExt(imagePath);
            var ext = path.ext(imagePath);

            var scaledName = name + "@" + scale + "x" + ext;

            return this.load(scaledName);
        }
    }]);

    return AssetsManager;
}();

var instance = new AssetsManager();

/**
 * @function load
 * @description Load asset from device (async)
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
var load = exports.load = function load(path) {
    return instance.load(path);
};

/**
 * @function exists
 * @description Load asset from device (async)
 * @param {string} path - The path of asset to load
 * @returns {Promise} - A promise of the result
 */
var exists = exports.exists = function exists(path) {
    return instance.exists(path);
};

/**
 * @function save
 * @description Save asset to device (async)
 * @param {string} path - The path of asset to save
 * @param {buffer} data - Buffer id that contains data
 * @returns {Promise} - A promise of the result
 */
var save = exports.save = function save(path, data) {
    return instance.save(path, data);
};

/**
 * @function remove
 * @description Remove asset from device (async)
 * @param path - The path of asset to remove
 * @returns {Promise} - A promise of the result
 */
var remove = exports.remove = function remove(path) {
    return instance.remove(path);
};

/**
 * @function image
 * @description Load image asset from device, with density support
 * @param path - The path of image to load
 * @returns {Promise} - A promise of the result
 */
var image = exports.image = function image(path) {
    return instance.loadImage(path);
};