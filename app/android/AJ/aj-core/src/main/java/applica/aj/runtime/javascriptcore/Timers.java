package applica.aj.runtime.javascriptcore;

import org.liquidplayer.webkit.javascriptcore.JSValue;

/**
 * Created by bimbobruno on 16/03/16.
 */
public interface Timers {

    Long setTimeout(JSValue action, Long delay);
    Long setInterval(JSValue action, Long delay);
    void clearTimeout(Long timerId);

}
