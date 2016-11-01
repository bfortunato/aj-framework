# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - **Implementing Flux** <-
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_http.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
    
# Implementing flux architecture
As already discussed before, aj framework business logic is implemented with Flux architecture.
All business logic code is shared for each platform.
Platforms can communicate with business logic using Flux actions, actions change application state and trigger it to subscribed platforms.

All methods to manage Flux architecture, are located in `aj` module

```javascript
import * as aj from "./aj"
```

## Creating actions
Create an action calling `aj.createAction` with an identifier and the effective action function

```javascript
export function getMessage = aj.createAction('GET_MESSAGE' (data) => {
    //dispatch actions to stores
    aj.dispatch({
        type: 'GET_MESSAGE',
        message: "Hello from AJ"
    }
} 
```
Actions are simple js functions

## Creating stores
Stores maintains application state. Create a store using `aj.crateStore` with an identifier and reducer function.
```javascript
//hello store initial state
var initialState = {
    message: ""
}

export let hello = aj.createStore('HELLO' (state = initialState, action) => {
    switch (action.type) {
    case: 'GET_MESSAGE':
        return Object.assign({}, state, {message: action.message})
    }
} 
```
If returned state is changed, this will causes an invocation of application state change event.

> Reducers are the only things that can change store states in AJ applications. Please keep state immutable using `Object.assign` or underscore `_.assign` to have more readable code.

## Dispatching actions to stores
Usually called from actions, `aj.dispatch` send action data to the stores that changes application state through reducers.

```javascript
export function getMessage = aj.createAction('GET_MESSAGE' (data) => {
    //dispatch actions to stores
    aj.dispatch({
        type: 'GET_MESSAGE',
        message: "Hello from AJ"
    }
} 
```
