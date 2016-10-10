package applica.aj;

import java.util.Objects;

/**
 * Created by bimbobruno on 06/10/16.
 */

public class Async {

    public static Thread run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void wait(Thread async) {
        Thread thread = (Thread) async;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void wait(Thread async, int timeout) {
        Thread thread = (Thread) async;
        if (thread != null) {
            try {
                thread.join(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
