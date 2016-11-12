package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class Require {

    private final Context context;
    private JSContext jsContext;
    private List<String> cache = new ArrayList<>();
    private Stack<String> currentRequireQueue = new Stack<>();

    public Require(Context context, JSContext jsContext) {
        this.context = context;
        this.jsContext = jsContext;

        StringBuilder source = new StringBuilder()
                .append("var __exports = __exports || {};\n")
                .append("function require(path) {\n")
                .append("   var module = __requireInternal(path);\n")
                .append("   return __exports[module].exports;\n")
                .append("}\n");

        jsContext.evaluateScript(source.toString());

    }

    public JSValue __requireInternal(String path) {
        String relativePath = currentRequireQueue.size() > 0 ? currentRequireQueue.peek() : "";
        String moduleBase = FilenameUtils.normalize(FilenameUtils.concat(relativePath, FilenameUtils.removeExtension(path)));
        String moduleName = FilenameUtils.getName(moduleBase);
        String module = path;
        InputStream inputStream = null;
        List<String> possibilities = Arrays.asList(
                moduleBase.concat(".js"),
                moduleBase.concat("/index.js"),
                moduleBase.concat("/").concat(moduleName).concat(".js")
        );

        for (String possibility : possibilities) {
            if (cache.contains(possibility)) {
                Log.i("AJ", String.format("Loading cached module %s", possibility));

                return new JSValue(jsContext, possibility);
            }

            String appDir = "js/";
            String finalPath = FilenameUtils.normalize(FilenameUtils.concat(appDir, possibility));
            try {
                inputStream = context.getAssets().open(finalPath);
            } catch (IOException e) {}

            if (inputStream != null) {
                module = possibility;
                break;
            }
        }

        if (inputStream == null) {
            throw new RuntimeException("Cannot load module: " + path);
        }

        Log.i("AJ", String.format("Loading module %s", module));

        currentRequireQueue.push(FilenameUtils.getFullPath(module));

        try {
            String source = IOUtils.toString(inputStream);
            IOUtils.closeQuietly(inputStream);

            StringBuilder fullSource = new StringBuilder()
                    .append(String.format("__exports['%s'] = (function() {\n", module))
                    .append("var module = { exports: {} };")
                    .append("var exports = module.exports;\n\n")
                    .append(source)
                    .append("\n\nreturn module;\n")
                    .append("})();\n");

            try {
                jsContext.evaluateScript(fullSource.toString());
            } catch (Throwable t) {
                t.printStackTrace();
                throw t;
            }

            cache.add(module);

            return new JSValue(jsContext, module);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot load module %s", module), e);
        } finally {
            currentRequireQueue.pop();
        }
    }


}
