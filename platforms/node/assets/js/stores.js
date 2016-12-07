"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.home = exports.HOME = undefined;

var _aj = require("./aj");

var aj = _interopRequireWildcard(_aj);

var _actions = require("./actions");

var actions = _interopRequireWildcard(_actions);

var _underscore = require("./libs/underscore");

var _ = _interopRequireWildcard(_underscore);

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

var HOME = exports.HOME = "HOME";
var home = exports.home = aj.createStore(HOME, function () {
    var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : { message: null };
    var action = arguments[1];


    switch (action.type) {
        case actions.GET_MESSAGE:
            return _.assign(state, { message: action.message });
    }
});