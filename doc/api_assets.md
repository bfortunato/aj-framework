# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - **Assets** <-
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_http.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
    
# Assets

Assets management API are contained in `aj/assets` js module.
In order to use assets api in your code, use `import` keyword

```javascript
import * as assets from "./aj/assets" 
```

### assets.load(path)
Load an asset from device assets. Returns a promise of result
Result contains buffer id of downloaded data.

Example:
```javascript
assets.load("values.json")
    .then(bufferId => buffers.get(bufferId))
    .then(data => console.log(data))
    .catch(e => logger.e(e))
```

### assets.exists(path)
Check assets existence. Returns a promise of result

Example:
```javascript
assets.exists("values.json")
    .then(result => {
        if (result) {
            console.log("values.json exists")
        } else {
            console.log("values.json not exists")
        }
    })
    .catch(e => logger.e(e))
```