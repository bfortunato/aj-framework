"use strict";

var EventEmitter = EventEmitter || {};
EventEmitter.addListener = function(obj, evt, handler) {
    var listeners = obj.__events_listeners;
    if(!listeners) {
        listeners = {};
        obj.__events_listeners = listeners;
    }

    if(!listeners[evt]) {
        listeners[evt] = [];
    }

    listeners[evt].push(handler);
};

EventEmitter.addListeners = function(obj, listeners) {
    for(var key in listeners) {
        events.addListener(obj, key, listeners[key]);
    }
};

EventEmitter.removeListener = function(obj, evt, listener) {
    if (obj.__events_listeners && obj.__events_listeners[evt]) {
        obj.__events_listeners[evt] = obj.__events_listeners[evt].filter(l => l != listener);
    }
};

EventEmitter.on = function(obj, evt, handler) {
    if(typeof(evt) === "object") {
        events.addListeners(obj, evt);
    } else {
        events.addListener(obj, evt, handler);
    }
};

EventEmitter.live = function(obj, evt) {
    if(!obj.__events_offs) obj.__events_offs = {};
    if(evt) {
        obj.__events_offs[evt] = false;
    } else {
        obj.__events_off = false;
    }
};

EventEmitter.die = function(obj, evt) {
    if(!obj.__events_offs) obj.__events_offs = {};
    if(evt) {
        obj.__events_offs[evt] = true;
    } else {
        obj.__events_off = true;
    }
};

EventEmitter.invoke = function(obj, evt) {
    if(!obj.__events_offs) obj.__events_offs = {};
    if(obj.__events_off) return;
    if(obj.__events_offs[evt]) return;

    var listeners = obj.__events_listeners;
    if(!listeners) {
        listeners = {};
        obj.__events_listeners = listeners;
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

class Observable {
    on(evt, handler) {
        EventEmitter.on(this, evt, handler);
    }

    invoke(evt) {
        EventEmitter.invoke(this, evt, Array.prototype.slice.call(arguments, 1));
    }
}

exports.EventEmitter = EventEmitter;
exports.Observable = Observable;
