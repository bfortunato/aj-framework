import * as aj from "../aj"
import * as types from "./types"
import * as actions from "../actions/types"

import * as _ from "../libs/underscore"

const initialState = {
    message: null
};

export const home = aj.createStore(types.HOME, (state = initialState, action) => {

    switch (action.type) {
        case actions.GET_MESSAGE:
            return _.assign(state, { message: action.message });
    }

});