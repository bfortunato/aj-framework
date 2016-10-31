# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework

AJ is a simple framework that allows developers to share code in different platforms, such as iOS, Android and Web browsers.

Applications built with AJ are hybrid, written in JS and native code following an unidirectional dataflow architecture, inspired by [Flux](https://facebook.github.io/react/blog/2014/05/06/flux.html) and [Redux](http://redux.js.org). Please take a look to Flux architecture to understand how AJ works.

In simple terms, application state and business logic is managed in Javascript side. Views are in native side, that is the most productive place to write high-quality user interfaces

```
     Shareable Javascript Code         Native Code (Mobile, Web)
│─────────────────────────────────│  │───────────────────────────│
│  ╔═════════╗       ╔════════╗   │  │    ╔═════════════════╗    │
│  ║ Actions ║──────>║ Stores ║──────────>║ View Components ║    │
│  ╚═════════╝       ╚════════╝   │  │    ╚═════════════════╝    │
│       ^                         │  │             │             │
│       └──────────────────────────────────────────┘             │
└─────────────────────────────────┘  └───────────────────────────┘
```

> As you can see, native code is pure user interface, nothing else, super productive and **best quality**. All business logic is completely reusable, also in web applications.


Current version: **0.1**


# Getting Started
AJ is distributed as an npm package, so, is very simple to install.

1: Type this command to install globally:
```
npm install -g aj-framework
```
This command will install aj command line tools.

2: Check if everything was installed correctly typing:
```
aj --version
```

3: Create a new aj project:
```
aj init myproject
```
This command will create a folder named `./myproject` that contains a bootstrapper project

4: Enter on myproject directory and build
```
cd myproject
aj build
```
This command builds images, scripts and assets

5: Watch changes to enable auto-deploy features for development (optional)
```
aj watch
```

6: Open and run Applications

## Project Structure
Here the project structure of AJ bootstrapper

```
+ myproject               (project root)
    + app                 (shared area)
        |- assets
        |- js
        |- resources/images
    + platforms           (native area)
        |- android
        |- ios
        |- web
```

`app/assets`
Contains application assets. `aj build` command will pack assets in native bundles

`app/js`
Contains ES6/ES7 application scripts. `aj build` command will compile scripts for each platform

`app/resources/images`
Contains image resources. Put images in 4x, `aj build` will crates resolution indipendent versions for each platforms

`platforms/*`
Contains native prjects. Developer can open this projects with native IDE, that are XCode for ios, Android Studio for Android, and your favourite html editor for web. I use IntelliJ.


# Usage
AJ is an unidirectional dataflow framework that allows developers to write reusable Javascript code for build applicaation business logic and specific native code to build user interfaces.
In this usage examples, you can take a look on basics in both js and native side.

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


## Handle application state changes
Now is the part of the native side that in AJ application architecture is used to build **only** user interfaces.

### iOS
```swift
AJApp.runtime().subscribe(to: Stores.HOME, owner: self) { [weak self] (state) in
    self?._textView?.text = state.get("message")?.string
}
```

### Android
```java
JApp.runtime().subscribe(Stores.HOME, this, new Store.Subscription() {
   @Override
   public void handle(AJObject state) {
       mTextView.setText(state.get("message").asString());
   }
});
```

### Web
```javascript
aj.subscribe('HOME', state => {
    document.getElementById("textView").value = state.message
})
```
> Pure javascript is used in this example, but AJ is perfect with ReactJS components. The web project bootstrapper has ReactJS included.


## Calling actions
### iOS
```swift
AJApp.runtime().run(action: "GET_MESSAGE")
```

### Android
```java
AJApp.runtime().run("GET_MESSAGE")
```

### Web
```javascript
import { getMessage } from './actions'

getMessage() //or aj.run('GET_MESSAGE')
```


# Documentation 
Available soon


#Tutorials
[TodoList](https://github.com/bfortunato/aj-framework-todolist)


# Colophon
AJ Framework was developed by Bruno Fortunato, CTO at [Applica](http://www.applica.guru) and is completely free and open source.

For support, questions and anything else, please contact me at [bruno.fortunato@applica.guru](mailto:bruno.fortunato@applica.guru)
