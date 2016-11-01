# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- [aj: command line interface](https://github.com/bfortunato/aj-framework/blob/master/doc/cli.md)
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
- **Plugins** <-
    
# Plugins
AJ framework provides a plugins system that allows developer to extend javascript functionalities, binding native methods to js code.

use `AJ.register` method to make a plugin available to JS.
use `aj.exec` method from JS to call native plugins methods.

> In web platform, just create a global object with Plugin name to make it available