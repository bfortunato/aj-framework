package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by bimbobruno on 10/03/16.
 */
public interface StorageManager {

    void readText(String path, JSFunction cb);
    void read(String path, JSFunction cb);
    void writeText(String path, String content, JSFunction cb);
    void write(String path, int buffer, JSFunction cb);
    void delete(String path, JSFunction cb);
    void exists(String path, JSFunction cb);

}
