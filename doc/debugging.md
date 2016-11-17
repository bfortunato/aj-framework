# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_http.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
- **Debugging** <-
    
# Debugging
AJ framework application interprets JavaScript files using webkit JavaScriptCore engine. 
JavaScript source files are stored into device assets and AJ Runtime uses this scripts to run application logic.

But, how to debug this code?

AJ framework application has 2 kinds of runtime. **AJJavaScriptCoreRuntime** and **AJWebSocketRuntime**.

## AJWebSocketRuntime
This runtime do not interpret directly JavaScript sources, but uses web sockets to communicate with a debug NodeJS server.
NodeJS server represents another AJ platform: **node** platform.

## Node Platform
The `aj build` command also compile sources and resource for node platform. 
This resources are used by NodeJS debug server to run application logic and with a NodeJS running server is possible to attach a debugger.

> Node platform is intended only for development purposes. Do not use in a production environment.


## Run NodeJS debug server in debug mode
```bash
$ cd platforms/node
$ npm update
# output...
$ node debug server.js
< Debugger listening on port 5858
connecting to 127.0.0.1:5858 ... ok
break in server.js:1
> 1 "use strict";
  2 
  3 const app = require("express")();
```

> Please refer [NodeJS debug documentation](https://nodejs.org/api/debugger.html) for more informations about debugging.

## Run AJ application in debug mode
To run application in debug mode, locate AJ runtime initialization code, set debug server address and set debug to true.
In iOS is located in application delegate, in Android in SlashActivity.

```swift
func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        let app = AJApp()
        app.socketUrl = "http://localhost:3000" // <- your node debug server address
        app.debug = true // <- put this value to true 
        app.initialize()
        
        ...
    }
```

```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AJApp app = new AJApp(getApplicationContext());
                app.setSocketUrl("http://localhost:3000"); // <- your node debug server address
                app.setDebug(false); // <- put this value to true 
                app.init();

                ...
            }
        }).start();
    }
}
```
