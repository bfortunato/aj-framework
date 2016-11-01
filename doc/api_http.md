# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - **HTTP** <-
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
    
# HTTP

HTTP client API are contained in `aj/http` js module.
In order to use HTTP client in your code, use `import` keyword

```javascript
import * as http from "./aj/http" 
```

### http.get(url, [data = {}], [headers: {}])
Makes a GET request to specified url. Returns a promise of the result.
Parameter `data`, can be a string or object. If is an object will be converted in a form encoded string

Example:
```javascript
http.get("http://service.com/contents", {filter: "aj"}}
    .then(contents => {
        logger.i(contents)
    })
    .catch(e => logger.e(e))
```


### http.post(url, [data = {}], [headers: {}])
Makes a POST request to specified url. Returns a promise of the result.
Parameter `data`, can be a string or object. If is an object will be converted in a form encoded string

Example:
```javascript
http.post("http://service.com/contents", {filter: "aj"}}
    .then(contents => {
        logger.i(contents)
    })
    .catch(e => logger.e(e))
```

### http.put(url, [data = {}], [headers: {}])
Makes a PUT request to specified url. Returns a promise of the result.
Parameter `data`, can be a string or object. If is an object will be converted in a form encoded string

Example:
```javascript
http.put("http://service.com/contents", {filter: "aj"}}
    .then(contents => {
        logger.i(contents)
    })
    .catch(e => logger.e(e))
```

### http.delete(url, [data = {}], [headers: {}])
Makes a DELETE request to specified url. Returns a promise of the result.
Parameter `data`, can be a string or object. If is an object will be converted in a form encoded string

Example:
```javascript
http.delete("http://service.com/contents", {filter: "aj"}}
    .then(contents => {
        logger.i(contents)
    })
    .catch(e => logger.e(e))
```

### http.download(url, [data = {}], [headers: {}])
Download a binary file from specified url. Returns a promise of the result.
Parameter `data`, can be a string or object. If is an object will be converted in a form encoded string.
Result contains buffer id of downloaded data.

Example:
```javascript
http.download("http://service.com/file.zip"}
    .then(bufferId => buffers.get(bufferId))
    .then(data => /* do something with data */)
    .catch(e => logger.e(e))
```