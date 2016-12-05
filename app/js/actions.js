import * as aj from "./aj"

export const GET_MESSAGE = "GET_MESSAGE"
export const getMessage = aj.createAction(GET_MESSAGE, data => {
    aj.dispatch({
        type: GET_MESSAGE,
        message: "Hello World"
    })
});
