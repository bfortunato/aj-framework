# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - **Storage** <-
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
    
# Storage
Storage management API are contained in `aj/storage` js module and allows to use device file system.
In order to use storage api in your code, use `import` keyword

```javascript
import * as storage from "./aj/storage" 
```

### storage.readText(path)
Reads text of file in specified path.

Example:
```javascript
storage.readText("documentation.txt")
    .then(text => console.log(text))
    .catch(e => logger.e(e))
```

### storage.read(path)
Reads binary file from specified path.
Result contains buffer id of downloaded data.

Example:
```javascript
storage.read("documentation.zip")
    .then(bufferId => buffers.get(bufferId))
    .then(data => /* do something with data */)
    .catch(e => logger.e(e))
```

### storage.writeText(path, contents)
Writes a text content in specified file

Example:
```javascript
storage.writeText("documentation.txt", "my documentation string")
    .then(() => console.log("Success"))
    .catch(e => logger.e(e))
```

### storage.write(path, bytes)
Writes a binary content in specified file

Example:
```javascript
let bytes = [] //byte array
storage.write("documentation.zip", bytes)
    .then(() => console.log("Success"))
    .catch(e => logger.e(e))
```

### storage.delete(path)
Deletes specified file from device storage

Example:
```javascript
storage.delete("documentation.zip")
    .then(() => console.log("Success"))
    .catch(e => logger.e(e))
```

### storage.exists(path)
Check if specified file exists in device storage

Example:
```javascript
storage.exists("documentation.zip")
    .then(result => {
            if (result) {
                console.log("documentation.zip exists")
            } else {
                console.log("documentation.zip not exists")
            }
        })
    .catch(e => logger.e(e))
```