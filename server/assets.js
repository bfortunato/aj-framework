const fs = require("fs");

module.exports = {
    load: function(path, cb) {
        path = ".work/" + path;
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

    exists: function(path, cb) {
        cb(false, true);
    }
};