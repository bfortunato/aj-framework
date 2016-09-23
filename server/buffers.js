"use strict";

const buffers = require("../app/js/aj/buffers");

module.exports = {
    create: function(data) {
        return buffers.create(data);
    },

    read: function(id) {
        return buffers.read(id);
    },

    destroy: function(id) {
        return buffers.destroy(id);
    }
};