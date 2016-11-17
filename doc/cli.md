# <img src="https://raw.githubusercontent.com/bfortunato/aj-framework/master/doc/images/aj.png" height="100" align="middle" /> Framework documentation

- [Installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md)
- **aj: command line interface** <-
- [API](https://github.com/bfortunato/aj-framework/blob/master/doc/api.md)
    - [Native API](https://github.com/bfortunato/aj-framework/blob/master/doc/api_native.md)
    - [Implementing Flux](https://github.com/bfortunato/aj-framework/blob/master/doc/api_flux.md)
    - [Assets](https://github.com/bfortunato/aj-framework/blob/master/doc/api_assets.md)
    - [Storage](https://github.com/bfortunato/aj-framework/blob/master/doc/api_storage.md)
    - [HTTP](https://github.com/bfortunato/aj-framework/blob/master/doc/api_http.md)
- [Plugins](https://github.com/bfortunato/aj-framework/blob/master/doc/plugins.md)
- [Debugging](https://github.com/bfortunato/aj-framework/blob/master/doc/debugging.md)
    
# aj: command line interface

`aj` is a very simple command line interface that allow developers to create and build aj framework projects.
Take a look on [installation](https://github.com/bfortunato/aj-framework/blob/master/doc/installation.md) document to make `aj ready to use

### Initialize a project
Initialize a new aj project in destination directory with `aj init` command
```bash
$ aj init <destination>
# output...
```

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


### Build project
Compile all scripts, images and app icon for each platforms with `aj build` command
```bash
$ aj build
# output...
```

If you would specify platforms to build, use `--platforms` or `-p` option. If you want to specify more than one platform, use a comma `,`.
Available platforms are
- ios
- android
- web

Example: build project only for android and ios
```bash
$ aj build --platforms ios,android 
# output...
```

If you would specify resource types to build, use `--types` or `-t` option. If you want to specify more than one types, use a comma `,`.
Available types are
- scripts
- images
- app_icon

Example: build only app icon and scripts
```bash
$ aj build --platforms scripts,app_icon
# output...
```

### Watch for changes
To compile scripts every times that changes use `aj watch` command
```bash
$ aj watch
# Looking for changes...
```