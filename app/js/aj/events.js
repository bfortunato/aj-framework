"use strict";

export const EventEmitter = {}

EventEmitter.addListener = function(obj, evt, handler) {
    var listeners = obj.$$events_listeners;
    if(!listeners) {
        listeners = {};
        obj.$$events_listeners = listeners;
    }

    if(!listeners[evt]) {
        listeners[evt] = [];
    }

    listeners[evt].push(handler);
};

EventEmitter.addListeners = function(obj, listeners) {
    for(var key in listeners) {
        EventEmitter.addListener(obj, key, listeners[key]);
    }
};

EventEmitter.removeListener = function(obj, evt, listener) {
    if (obj.$$events_listeners && obj.$$events_listeners[evt]) {
        obj.$$events_listeners[evt] = obj.$$events_listeners[evt].filter(l => l != listener);
    }
};

EventEmitter.on = function(obj, evt, handler) {
    if(typeof(evt) === "object") {
        EventEmitter.addListeners(obj, evt);
    } else {
        EventEmitter.addListener(obj, evt, handler);
    }
};

EventEmitter.off = function(obj, evt, handler) {
    EventEmitter.removeListener(obj, evt, handler);
};

EventEmitter.live = function(obj, evt) {
    if(!obj.$$events_offs) obj.$$events_offs = {};
    if(evt) {
        obj.$$events_offs[evt] = false;
    } else {
        obj.$$events_off = false;
    }
};

EventEmitter.die = function(obj, evt) {
    if(!obj.$$events_offs) obj.$$events_offs = {};
    if(evt) {
        obj.$$events_offs[evt] = true;
    } else {
        obj.$$events_off = true;
    }
};

EventEmitter.invoke = function(obj, evt) {
    if(!obj.$$events_offs) obj.$$events_offs = {};
    if(obj.$$events_off) return;
    if(obj.$$events_offs[evt]) return;

    var listeners = obj.$$events_listeners;
    if(!listeners) {
        listeners = {};
        obj.$$events_listeners = listeners;
    }

    var handlers = listeners[evt];
    if(handlers) {
        var size = handlers.length;
        for (var i = 0; i < size; i++) {
            var h = handlers[i];
            h.apply(obj, Array.prototype.slice.call(arguments, 2));
        }
    }
};

export class Observable {
    on(evt, handler) {
        EventEmitter.on(this, evt, handler);
    }

    off(evt, handler) {
        EventEmitter.off(this, evt, handler);
    }

    invoke(evt) {
        EventEmitter.invoke.apply(null, [this, evt].concat(Array.prototype.slice.call(arguments, 1)));
    }
}

