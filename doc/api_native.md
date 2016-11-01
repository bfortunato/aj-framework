# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - **Native API** <-
    - [Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
    
# Native API
Native API are a sets of classes and function that allow developers to communicate with javascript shared business logic.
Native API are identical for each platforms. The only difference is in the sintax because depends on the platform language: Java for Android, Swift 3 for iOS, Javascript for web


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

### iOS: get(store type: String) -> AJStore

iOS Example:
```swift
let todosStore = AJ.get(store: "TODOS")
let count = todoStore.state?.get("todos")?.array?.count ?? 0
print("Things to do: \(count)")
```

### Android: getStore(String type)

Android Example:
```java
Store todosStore = AJ.getStore("TODOS");
int count = todoStore.getState().get("todos").asArray().count();
System.out.println(String.format("Things to do: %d", count));
```

### Web: import { TodosStore } from "stores"

web Example:
```javascript
import { TodosStore } from "stores"

let count = TodosStore.state.todos.length
console.log("Things to do: " + count)
```


# <a name="ajobject"></a>AJObject

# <a name="buffer"></a>Buffer

# <a name="plugin"></a>Plugin
