# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - **Native API** <-
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_http.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
- [Debugging](https://github.com/bfortunato/aj-framework/blob/master/doc/debugging.md)
    
# Native API
Native API are a sets of classes and function that allow developers to communicate with javascript shared business logic.
Native API are identical for each platforms. The only difference is in the sintax because depends on the platform's language: Java for Android, Swift 3 for iOS, Javascript for web


# Classes and modules

- [AJ](#aj)
- [AJObject](#ajobject)
- [Store](#store)
- [Buffer](#buffer)
- [Plugin](#plugin)

# <a name="aj"></a>AJ
AJ class it's the main class to allow a device to communicate with JS side flux based business logic.

## Getting a Store
use `AJ.getStore` method to get store singleton instance of specified `type`

#### iOS: get(store type: String) -> AJStore

iOS Example:
```swift
let todosStore = AJ.get(store: "TODOS")
let count = todoStore.state?.get("todos")?.array?.count ?? 0
print("Things to do: \(count)")
```

#### Android: getStore(String type)

Android Example:
```java
Store todosStore = AJ.getStore("TODOS");
int count = todoStore.getState().get("todos").asArray().count();
System.out.println(String.format("Things to do: %d", count));
```

#### Web: import { TodosStore } from "stores"

Web Example:
```javascript
import { TodosStore } from "stores"

let count = TodosStore.state.todos.length
console.log("Things to do: " + count)
```


## Subscribe to Store
use `AJ.subscribe` method to subscribe to a store and keep track of application state changes

#### iOS: AJ.subscribe(to store: String, owner: AnyObject, subscription: @escaping Subscription) {

iOS Example:
```swift
//inside UIViewController or UIView init method, for example

AJ.subscribe(to: "TODOS", owner: self) { [weak self] (state) in
    self?.todos = state.get("todos")?.array
    self?.tableView.reloadData()
}
```

> Take care of `weak self` in subscription closure. Using weak is very important to avoid memory leaks

#### Android: AJ.subscribe(String store, Object owner, Store.Subscription subscription)

Android Example:
```java
//inside an activity onCreate method, for example

AJ.subscribe("TODOS", this, new Store.Subscription() {
    @OverrideO
    public void handle(AJObject state) {
        mTodos = state.get("todos").asArray();
        mAdapter.notifyDataSetChanged();
    }
});

```

#### Web: store.subscribe(owner, subscription)

Web Example:
```javascript
//inside a react componentDidMount method, for example

import { TodosStore } from "stores"

TodoStore.subscribe(this, (state) => {
    this.setState(state)
})
```


## Unsubscribe to Store
use `AJ.unsubscribe` method to stop application state change tracking

#### iOS: AJ.unsubscribe(from store: String, owner: AnyObject) {

iOS Example:
```swift
//inside UIViewController or UIView deinit method, for example

AJ.unsubscribe(from: "TODOS", owner: self)
```

#### Android: AJ.unsubscribe(String store, Object owner)

Android Example:
```java
//inside an activity onDestroy method, for example

AJ.unsubscribe("TODOS", this);

```

#### Web: store.unsubscribe(owner)

Web Example:
```javascript
//inside a react componentWillUnmount method, for example

import { TodosStore } from "stores"

TodoStore.unsubscribe(this)
```


## Running actions
use `AJ.run` method to run actions. run method is async for iOS and Android

#### iOS: AJ.run(action: String, data: AJObject = AJObject.empty())

iOS Example:
```swift
AJ.run(action: "TODO_CREATE", data: AJObject.create().set("text", "Guitar Lessons"))
```

#### Android: AJ.run(String action, AJObject data)

Android Example:
```java
AJ.run("TODO_CREATE", AJObject.create().set("text", "Guitar Lessons"));

```

#### Web: direct function call

Web Example:
```javascript
import { createTodo } from "actions"

createTodo({text: "Guitar Lessons"})
```


# <a name="ajobject"></a>AJObject
AJObject is a native representation of JS objects and provides a fluent creation API and types conversions.
AJObject is the main exchange object to pass data to the actions and to receive state from stores.
Arrays are also supported with `AJArray` class
Here some examples of usages.

#### Android
```java
//creates a new object
AJObject user = AJObject.create()
                    .set("username", "bruno")
                    .set("password", "******")
                    .set("age", 30)
                    .set("address", AJObject.create()
                        .set("city", "Matera")
                        .set("address", "Talking Street")
                    );



//read objects
String username = user.get("username").asString();
int age = user.get("age").asInt();
String city = user.get("address").asObject().get("city").asString();

//working with arrays
AJArray teams = new AJArray();
teams.append(AJObject.create().set("name", "programers").set("members", 13));
teams.append(AJObject.create().set("name", "testers").set("members", 5));
    
int allMembers = 0;
for (int i = 0; i < teams.count(); i++) {
    allMembers += teams.objectAt(i).get("members").asInt();
}
```

#### iOS
```swift
//creates a new object
let user = AJObject.create()
                    .set("username", "bruno")
                    .set("password", "******")
                    .set("age", 30)
                    .set("address", AJObject.create()
                        .set("city", "Matera")
                        .set("address", "Talking Street")
                    )



//read objects
let username = user.get("username")?.string
let age = user.get("age")?.int
let city = user.get("address")?.object?.get("city")?.string

//working with arrays
let teams = AJArray()
teams.append(AJObject.create().set("name", "programers").set("members", 13))
teams.append(AJObject.create().set("name", "testers").set("members", 5))
    
let allMembers = 0
for i in 0..<allMembers.count
    allMembers += teams.objectAt(i)?.get("members")?.int
}
```

> of course web platform uses Javascript, so, an object representation of js object is not needed

List of AJObject conversion methods:
- string
- int
- long
- boolean
- float
- double
- array
- object
- bitmap (image in iOS)
- color ({r: 0-255, g: 0-255, b: 0-255, a: 0-255})
- buffer


# <a name="buffer"></a>Buffer
Buffer is a very useful class in AJ framework. It is an abstract reference manager to byte buffers.
To improve performance and reduce memory consumption, aj framework uses the Buffer class to swap bytes from the native side to the js side (and vice versa).
The actual bytes reside only in the native side, it is exchanged only a reference.

For example, `http.download` method do not returns directly a byte array, but only a buffer reference to it.

Example: download an image and display to an image view:

```javascript
//creates an action that load a pdf document from web
const loadPdf = aj.createAction("LOAD_PDF", (data) => {
    http.download("http://url.of/image.jpg")
        .then(bufferId => { 
            //dispatch action to stores
            aj.dispatch({
                type: "LOAD_PDF",
                pdf: bufferId 
            })
        })
})
```

```swift
//iOS app track changes of states and view pdf

AJ.subscribe(to: "DOCUMENTS", owner: self) { [weak self] (state) in
    let data: NSData = state.get("pdf")?.buffer) //or Buffer.get(state.get("pdf")?.int)
    pdfView.load(data: data)
}

```

> NB: To avoid memory leaks, a buffer is destroyed immediately after obtaining it

# <a name="plugin"></a>Plugin
Plugin is the base class to build plugins.
For more information visit [plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md) section in documentation