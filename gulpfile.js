const gulp = require("gulp");
const babel = require("gulp-babel");
const easyimage = require("easyimage");
const through2 = require("through2");
const path = require("path");
const pn = require("pn/fs");
const fs = require("fs");
const svg2png = require("svg2png");

var android = {
    mapAssetPath: function(path) {
        return "app/android/AJ/app/src/main/assets/" + path
    },
    mapImagePath: function(dir, name, extension, ratio) {
        var quality = "";
        switch (ratio) {
            case 1:
                quality = "-mdpi";
                break;
            case 1.5:
                quality = "-hdpi";
                break;
            case 2:
                quality = "-xhdpi";
                break;
            case 3:
                quality = "-xxhdpi";
                break;
            case 4:
                quality = "-xxxhdpi";
                break;

        }

        return "app/android/AJ/app/src/main/res/drawable" + quality + "/" + name + extension;
    },
    ratios: [0.75, 1, 1.5, 2, 3, 4]
};

var ios = {
    mapAssetPath: function(path) {
        return "app/ios/App/assets/" + path
    },
    mapImagePath: function(dir, name, extension, ratio) {
        return ("app/ios/App/App/Assets.xcassets/" + name  + ".imageset/" + name + "@" + parseInt(ratio) + "x" + extension).replace("@1x", "");
    },
    afterImage: function(dir, name, extension) {
        var contents = {
            "images": [],
            "info": {
                "version": 1,
                "author": "applica"
            }
        };

        ios.ratios.forEach(r => {
            contents.images.push({
                "idiom": "universal",
                "filename": (name + "@" + parseInt(r) + "x" + extension).replace("@1x", ""),
                "scale": parseInt(r) + "x"
            })
        });

        fs.writeFile("app/ios/App/App/Assets.xcassets/" + name  + ".imageset/Contents.json", JSON.stringify(contents), function(err) {
            if(err) {
                return console.log(err);
            }

            console.log("Written Contents.json for " + name);
        });
    },
    ratios: [1, 2, 3]
};

var node = {
    mapAssetPath: function(path) {
        return ".work/" + path;
    },
    mapImagePath: function() {
        return null;
    },
    ratios: [0.75, 1, 1.5, 2, 3, 4]
};

function imagesSvg(platforms) {
    var images = [];
    gulp.src(["app/resources/images/**/*.svg"])
        .pipe(through2.obj(function(chunk, enc, cb) {
            console.log("Working on " + chunk.path);
            if (chunk.path.indexOf("@") == -1) { //already converted
                easyimage.info(chunk.path).then(function (info) {
                    var ext = path.extname(chunk.path).toLowerCase();
                    var dir = path.dirname(chunk.path.replace(__dirname + "/app/assets/images/", "")) + "/";
                    var name = path.basename(chunk.path, ext);
                    var ratios = android.ratios;
                    for (var i = 0; i < ratios.length; i++) {
                        (function (m) {
                            var factor = m;
                            pn.readFile(chunk.path)
                                .then(buffer => svg2png(buffer, {width: parseInt(info.width * factor), height: parseInt(info.height * factor)}))
                                .then(buffer => {
                                    platforms.forEach(platform => {
                                        if (platform.ratios.indexOf(m) != -1) {
                                            var dest = platform.mapImagePath(dir, name, ".png", m);
                                            if (!fs.existsSync(path.dirname(dest))) {
                                                fs.mkdirSync(path.dirname(dest));
                                            }
                                            fs.writeFile(dest, buffer);

                                            console.log(dest + " " + JSON.stringify({
                                                width: info.width * factor,
                                                height: info.height * factor
                                            }));
                                        }

                                        if (platform.afterImage) {
                                            platform.afterImage(dir, name, ".png");
                                        }
                                    });
                                })
                                .catch(e => { console.log(e.message); console.error(e.stack) });
                        })(ratios[i]);
                    }

                });
            }
            cb()
        }))
}

function executeSerial(actions) {
    var count = actions.length
    var index = 0;

    function next() {
        console.log("next: " + index);
        if (index < count) {
            actions[index++](next);
        }
    }

    next();
}

function imagesRaster(platforms) {
    var images = [];
    gulp.src(["app/resources/images/**/*.png", "app/resources/images/**/*.jpg"])
        .pipe(through2.obj(function(chunk, enc, cb) {
            if (chunk.path.indexOf("@") == -1) { //already converted
                easyimage.info(chunk.path).then(function (info) {
                    var ext = path.extname(chunk.path).toLowerCase();
                    var dir = path.dirname(chunk.path.replace(__dirname + "/app/assets/images/", "")) + "/";
                    console.log("Working on " + chunk.path);
                    var name = path.basename(chunk.path, ext);
                    var ratios = android.ratios;
                    platforms.forEach(platform => {
                        for (var i = 0; i < ratios.length; i++) {

                            (function (m) {
                                var factor = m / 4;
                                var dest = platform.mapImagePath(dir, name, ".png", m);
                                if (!fs.existsSync(path.dirname(dest))) {
                                    fs.mkdirSync(path.dirname(dest));
                                }


                                if (platform.ratios.indexOf(m) != -1) {

                                    console.log("Resizing " + name + " with factor " + factor);
                                    easyimage.resize({
                                        src: chunk.path,
                                        dst: dest,
                                        width: info.width * factor,
                                        height: info.height * factor
                                    })
                                        .then(() => console.log("Resized " + dest + " " + JSON.stringify({
                                                width: info.width * factor,
                                                height: info.height * factor
                                            })))
                                        .catch(err => {
                                            console.log("Error resizing image " + chunk.path + " with factor " + factor
                                                + "(w=" + (info.width * factor) + ", h=" + (info.height * factor) + ")" + err)
                                        })

                                }
                            })(ratios[i]);

                            if (platform.afterImage) {
                                platform.afterImage(dir, name, ".png");
                            }
                        }
                    });
                });
            }
            cb()
        }))
}

gulp.task("images-svg-ios", function () {
    imagesSvg([ios]);
});

gulp.task("images-svg-android", function () {
    imagesSvg([android]);
});

gulp.task("images-svg", function() {
    imagesSvg([ios, android]);
});

gulp.task("images-raster-ios", function () {
   imagesRaster([ios]);
});

gulp.task("images-raster-android", function () {
    imagesRaster([android]);
});

gulp.task("images-raster", function () {
    imagesRaster([ios, android]);
});

gulp.task("images", ["images-svg", "images-raster"]);

gulp.task("assets", function() {
    assets(ios);
    assets(android);
    assets(node);
});

function assets(platform) {
    gulp.src("app/assets/**/*.*").pipe(gulp.dest(platform.mapAssetPath("js")));
}


function scripts(platform) {
    try {
        gulp.src("app/js/**/*.js")
            .pipe(babel({presets: "es2015"}).on("error", function(e) { console.log(e.stack); }))
            .pipe(gulp.dest(platform.mapAssetPath("js")));

    } catch (e) {
        console.error(e);
    }
}

gulp.task("scripts-ios", function() {
    scripts(ios);
});

gulp.task("scripts-android", function() {
    scripts(android);
});

gulp.task("scripts", ["scripts-ios", "scripts-android"]);




gulp.task("build", ["scripts", "images", "assets"]);






gulp.task("watch-scripts-ios", function() {
    gulp.watch("app/js/**/*.js", ["scripts-ios"]);
});

gulp.task("watch-scripts-android", function() {
    gulp.watch("app/js/**/*.js", ["scripts-android"]);
});

gulp.task("watch-scripts", ["watch-scripts-ios", "watch-scripts-android"]);