package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSFunction;

/**
 * Created by bimbobruno on 02/09/16.
 */
public interface BuffersManager {

    void create(String data, JSFunction cb);
    void read(int id, JSFunction cb);
    void destroy(int id, JSFunction cb);

}
