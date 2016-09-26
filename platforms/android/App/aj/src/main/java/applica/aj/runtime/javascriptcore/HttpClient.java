package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by bimbobruno on 10/03/16.
 */
public interface HttpClient {

    void request(
            String urlString,
            String method,
            JSValue data,
            JSValue headers,
            JSValue requestType,
            JSValue responseType,
            Boolean rawResponse,
            JSFunction cb);
}
