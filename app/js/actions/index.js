import * as types from "./types"
import * as aj from "../aj"

export const getMessage = aj.createAction(types.GET_MESSAGE, data => {
    aj.dispatch({
        type: types.GET_MESSAGE,
        message: "Hello World"
    })
});
