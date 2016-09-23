var fs = require("fs");

module.exports = {
    readText: function(path, cb) {
        path = ".storage/" + path;
        fs.readFile(path, "utf8", (err, data) => {
            if (err) { cb(true, err); }
            else { cb(false, data); }
        });
    },

    read: function(path, cb) {
        path = ".storage/" + path;
        fs.readFile(path, (err, data) => {
            if (err) { cb(true, err); }
            else {
                __buffersManager.create(data.toString("base64")).then((id) => {
                    cb(false, id)
                }).catch((e) => {
                    cb(true, e);
                });
            }
        });
    },

    writeText: function(path, contents, cb) {
        path = ".storage/" + path;
        fs.stat(".storage", (err, stat) => {
            if (err || !stat.isDirectory()) {
                fs.mkdirSync(".storage");
            }

            fs.writeFile(path, contents, "utf8", (err, data) => {
                if (err) { cb(true, err); }
                else { cb(false, "OK"); }
            });
        });
    },

    write: function(path, bytes, cb) {
        path = ".storage/" + path;
        fs.stat(".storage", (stat, err) => {
            if (!stat.isDirectory()) {
                fs.mkdirSync(".storage");
            }

            fs.writeFile(path, bytes, (err, data) => {
                if (err) { cb(true, err); }
                else { cb(false, data); }
            });
        });
    },

    delete: function(path, cb) {
        path = ".storage/" + path;
        fs.unlink(".storage", (err, stat) => {
            if (err) { cb(true, err); }
            else { cb(false, "OK"); }
        });
    },

    exists: function(path, cb) {
        path = ".storage/" + path;
        fs.stat(".storage", (err, stat) => {
            if (err) { cb(false, false); }
            else { cb(false, stat.isFile() || stat.isDirectory()); }
        });
    }

};