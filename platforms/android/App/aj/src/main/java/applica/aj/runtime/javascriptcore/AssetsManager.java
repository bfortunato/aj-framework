package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by bimbobruno on 10/03/16.
 */
public interface AssetsManager {

    void load(String path, JSValue cb);
    void exists(String path, JSValue cb);

}
