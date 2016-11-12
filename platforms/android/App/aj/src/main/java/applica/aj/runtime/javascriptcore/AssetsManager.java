package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by bimbobruno on 10/03/16.
 */
public interface AssetsManager {

    void load(String path, JSFunction cb);
    void exists(String path, JSFunction cb);

}
