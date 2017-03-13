package applica.app;

import applica.aj.Async;

/**
 * Created by bimbobruno on 21/04/16.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        Async.init();

        super.onCreate();
    }
}
