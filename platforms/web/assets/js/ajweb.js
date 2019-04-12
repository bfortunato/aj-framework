"use strict";

(function(global) {
    /**
     *
     *  Base64 encode / decode
     *  http://www.webtoolkit.info/
     *
     **/
    var Base64 = {
        // private property
        _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

        // public method for encoding
        encode : function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;

            input = Base64._utf8_encode(input);

            while (i < input.length) {

                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                    this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

            }

            return output;
        },

        // public method for decoding
        decode : function (input) {
            var output = "";
            var chr1, chr2, chr3;
            var enc1, enc2, enc3, enc4;
            var i = 0;

            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            while (i < input.length) {

                enc1 = this._keyStr.indexOf(input.charAt(i++));
                enc2 = this._keyStr.indexOf(input.charAt(i++));
                enc3 = this._keyStr.indexOf(input.charAt(i++));
                enc4 = this._keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

            }

            output = Base64._utf8_decode(output);

            return output;

        },

        // private method for UTF-8 encoding
        _utf8_encode : function (string) {
            string = string.replace(/\r\n/g,"\n");
            var utftext = "";

            for (var n = 0; n < string.length; n++) {

                var c = string.charCodeAt(n);

                if (c < 128) {
                    utftext += String.fromCharCode(c);
                }
                else if((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
                else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }

            }

            return utftext;
        },

        // private method for UTF-8 decoding
        _utf8_decode : function (utftext) {
            var string = "";
            var i = 0;
            var c = 0, c1 = 0, c2 = 0, c3 = 0;

            while ( i < utftext.length ) {

                c = utftext.charCodeAt(i);

                if (c < 128) {
                    string += String.fromCharCode(c);
                    i++;
                }
                else if((c > 191) && (c < 224)) {
                    c2 = utftext.charCodeAt(i+1);
                    string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                    i += 2;
                }
                else {
                    c2 = utftext.charCodeAt(i+1);
                    c3 = utftext.charCodeAt(i+2);
                    string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                    i += 3;
                }

            }

            return string;
        }

    };

    /**
     *
     * async
     */

    var async = global.async = function(action) {
        setTimeout(action, 0);
    };

    /**
     * path utils
     */

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
                    return path;
                }
            }

            return "";
        }
    };
    /**
     * Buffers
     */

    var Buffer = global.Buffer = {
        buffers: [],
        lastBufferId: 0,

        create: function(bytes) {
            var buffer = {
                id: ++this.lastBufferId,
                data: bytes
            };
            this.buffers.push(buffer);
            return buffer.id;
        },

        get: function(id) {
            var bytes = _.find(this.buffers, function(b) {return b.id == id});
            if (bytes != null) {
                this.destroy(buffer.id);
            }

            return buffer.data;
        },

        destroy: function(id) {
            this.buffers = _.find(this.buffers, function(b) {return b.id != id});
        }


    };

    /**
     * Logger
     */

    global.logger = {
        i: function(msg) {
            if (LOG_LEVEL >= LOG_LEVEL_INFO) {
                if (arguments.length == 1) {
                    console.log(msg);
                } else {
                    console.log(Array.prototype.join.call(arguments, " "));
                }
            }
        },

        e: function(msg) {
            if (LOG_LEVEL >= LOG_LEVEL_ERROR) {
                if (arguments.length == 1) {
                    console.error(msg);
                } else {
                    console.error(Array.prototype.join.call(arguments, " "));
                }
            }
        },

        w: function(msg) {
            if (LOG_LEVEL >= LOG_LEVEL_WARNING) {
                if (arguments.length == 1) {
                    console.warn(msg);
                } else {
                    console.warn(Array.prototype.join.call(arguments, " "));
                }
            }
        }
    };

    /**
     * Http
     */

    global.__httpClient = {
        request: function(url, method, data, headers, accept, contentType, rawResponse, cb) {
            $.ajax({
                url: url,
                method: method,
                beforeSend: request => {
                    if (_.isObject(headers)) {
                        _.keys(headers).forEach(k => request.setRequestHeader(k, headers[k]))
                    }
                },
                data: data,
                dataType: "text",
                accept: accept == null ? undefined : accept,
                contentType: contentType == null ? undefined : contentType,
                success: function(response) {
                    cb(false, response);
                },
                error: function(xhr, err) {
                    cb(true, err);
                }
            })
        }
    };

    /**
     * Assets
     */

    global.__assetsManager = {
        load: function(path, cb) {
            $.ajax({
                url: url,
                method: "GET",
                success: function(response) {
                    cb(false, response);
                },
                error: function(xhr, err) {
                    cb(true, err);
                }
            })
        },

        exists: function(path, cb) {
            cb(false, true);
        }
    };


    /**
     * Storage
     */

    (function() {

        function checkSupport() {
            if (!Storage) {
                throw new Error("No support for storage manager");
            }
        }

        global.__storageManager = {
            readText: function(path, cb) {
                async(function() {
                    try {
                        checkSupport();

                        var item = localStorage.getItem(path);
                        cb(false, item);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            },

            read: function(path, cb) {
                async(function() {
                    try {
                        checkSupport();

                        var item = localStorage.getItem(path);
                        cb(false, item);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            },

            writeText: function(path, content, cb) {
                async(function() {
                    try {
                        checkSupport();

                        localStorage.setItem(path, content);
                        cb(false);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            },


            write: function(path, buffer, cb) {
                async(function() {
                    try {
                        checkSupport();

                        localStorage.setItem(path, Buffer.get(buffer));
                        cb(false);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            },

            delete: function(path, cb) {
                async(function() {
                    try {
                        checkSupport();

                        localStorage.setItem(path, null);
                        cb(false);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            },

            exists: function(path, cb) {
                async(function() {
                    try {
                        checkSupport();

                        var exists = localStorage.getItem(path) != null;
                        cb(false, exists);
                    } catch (e) {
                        cb(true, e);
                    }
                });
            }
        };


    })();


    /***
     * Buffers manager
     */

    global.__buffersManager = {
        create: function(base64, cb) {
            async(function() {
                var bytes = Base64.encode(base64);
                cb(false, Buffer.create(bytes));
            });

        },

        get: function(id, cb) {
            async(function() {
                var base64 = Base64.decode(Buffer.get(id));
                cb(false, base64);
            });
        },

        destroy: function(id, cb) {
            async(function() {
                Buffer.destroy(id);
                cb(false, "");
            });
        }
    };

    /**
     * Device
     */

    global.device = {
        getName: function() {
            return "web"
        },

        getHeight: function() {
            return window.innerHeight;
        },

        getWidth: function() {
            return window.innerWidth
        },

        getScale: function() {
            return 1;
        }
    };

    /**
     * Platform
     */

    global.platform = {
        engine: "native",
        device: "browser"
    };

    /**
     * AJ Web Runtime
     */

    global.__trigger = function(store, state) {
        //nothing, already done in js
    };

    global.__exec = function(plugin, method, data, callback) {
        //executes a class method with data, simply
        var Plugin = global[plugin];
        if (!_.isObject(Plugin)) {
            throw new Error("Plugin " + plugin + " not registered");
        }

        var fn = Plugin[method];

        if (!_.isFunction(fn)) {
            throw new Error("Plugin method " + plugin + "." + method +  " not found");
        }

        fn(data, callback);
    };

})(window);



