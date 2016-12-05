import * as aj from "./aj"
import * as actions from "./actions"
import * as _ from "./libs/underscore"

export const HOME = "HOME"
export const home = aj.createStore(HOME, (state = {message: null}, action) => {

    switch (action.type) {
        case actions.GET_MESSAGE:
            return _.assign(state, { message: action.message })
    }

});