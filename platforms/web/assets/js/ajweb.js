"use strict";

(function(exports) {

    var path = {
        separator: "/",

        normalize: function(path, endingSeparator) {
            if (!_.isString(path)) {
                throw new Error("path is not a string");
            }

            var tree = path.split("/");
            var normalizedTree = [];
            _.each(tree, function(node) {
                if (node == "..") {
                    normalizedTree = _.initial(normalizedTree);
                } else if (node != ".") {
                    normalizedTree.push(node);
                }
            });

            var normalizedPath = normalizedTree.join(this.separator);
            if (endingSeparator) {
                if (!normalizedPath.endsWith(this.separator)) {
                    normalizedPath = normalizedPath + this.separator;
                }
            } else {
                if (normalizedPath.endsWith(this.separator)) {
                    if (path.size > 2) {
                        normalizedPath = normalizedPath.substring(0, normalizedPath.length - 2);
                    }
                }
            }

            return normalizedPath;
        },

        name: function(path, includeExtension) {
            if (!_.isString(path)) {
                throw new Error("path is not a string");
            }

            var name = null;
            var index = path.lastIndexOf(this.separator);

            if (index == -1) {
                name = path;
            } else {
                name = path.substring(index + 1);
            }

            if (!includeExtension) {
                index = name.lastIndexOf(".");
                if (index != -1) {
                    if (name.length > 2) {
                        name = name.substring(0, index);
                    }
                }
            }

            return name;
        },

        removeExtension: function(path) {
            if (!_.isString(path)) {
                throw new Error("path is not a string");
            }

            var index = path.lastIndexOf(".");
            if (index != -1) {
                if (path.length > 2) {
                    path = path.substring(0, index);
                }
            }

            return path;
        },

        join: function(p1, p2) {
            if (!_.isString(p1)) {
                throw new Error("p1 is not a string");
            }

            if (!_.isString(p2)) {
                throw new Error("p2 is not a string");
            }

            if (_.isEmpty(p1)) {
                return p2;
            }

            if (_.isEmpty(p2)) {
                return p1;
            }

            if (p1.endsWith(this.separator)) {
                if (p1.length > 2) {
                    p1 = p1.substr(0, p1.length - 2);
                }
            }

            if (p2.startsWith(this.separator)) {
                if (p2.length > 2) {
                    p2 = p2.substr(1);
                }
            }

            return p1 + this.separator + p2;
        },

        base: function(path) {
            if (!_.isString(path)) {
                throw new Error("path is not a string");
            }

            var index = path.lastIndexOf(this.separator);
            if (index != -1) {
                if (path.length > 2) {
                    path = path.substring(0, index);
                }
            }

            return path;
        }
    };

    /**
     * Require
     */
    (function() {
        var builders = {};
        var cache = {};
        var currentRequireQueue = [];

        function define(module, builder) {
            builders[module] = builder;
        }

        function require(_path) {
            var currentRelativePath = _.last(currentRequireQueue) || "";
            var moduleExt = "js";
            var moduleBase = path.removeExtension(path.normalize(path.join(currentRelativePath, _path)));
            var moduleName = path.name(moduleBase);
            var possibilities = [
                moduleBase + "." + moduleExt,
                path.join(moduleBase, "index.js"),
                path.join(moduleBase, moduleName) + "." + moduleExt
            ];

            var module = null;

            for (var i = 0; i < possibilities.length; i++) {
                var possibility = possibilities[i];
                if (_.has(cache, possibility)) {
                    console.log("Loading cached module " + possibility);

                    module = cache[possibility];
                    break;
                } else {
                    if (_.has(builders, possibility)) {
                        var builder = builders[possibility];
                        if (!_.isFunction(builder)) {
                            throw new Error("Builder for module " + possibility + " is not a function");
                        }

                        module = {};
                        module.exports = {};

                        console.log("Loading module " + possibility);

                        currentRequireQueue.push(path.base(possibility));
                        builder(module, module.exports);
                        currentRequireQueue = _.initial(currentRequireQueue);

                        cache[possibility] = module;
                    }
                }
            }

            if (module == null) {
                throw new Error("Module not found: " + _path);
            }

            return module.exports;
        }

        exports.define = define;
        exports.require = require;
    })();

    /**
     * Logger
     */

    exports.logger = {
        i: function(msg) {
            if (_.isArray(msg)) {
                console.log("AJ: " + msg.join(" "));
            } else {
                console.log("AJ: " + msg);
            }
        },

        e: function(msg) {
            if (_.isArray(msg)) {
                console.error("AJ: " + msg.join(" "));
            } else {
                console.error("AJ: " + msg);
            }
        },

        w: function(msg) {
            if (_.isArray(msg)) {
                console.warn("AJ: " + msg.join(" "));
            } else {
                console.warn("AJ: " + msg);
            }
        },
    }


    /**
     * AJ Web Runtime
     */

    var AJWebRuntime = function() {};
    AJWebRuntime.prototype = {

    };

})(window);



