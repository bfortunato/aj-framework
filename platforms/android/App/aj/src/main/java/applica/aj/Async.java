package applica.aj;

import android.util.Log;

/**
 * Created by bimbobruno on 06/10/16.
 */

public class Async {

    private static long count = 0;
    private static long activeThreads;

    public static Thread run(String name, final Runnable runnable) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                activeThreads++;
                Log.i("AJ.Async", String.format("AJ Thread started: %s, active threads: %d", Thread.currentThread().getName(), activeThreads));
                runnable.run();
                activeThreads--;
                Log.i("AJ.Async", String.format("AJ Thread terminated: %s, active threads: %d", Thread.currentThread().getName(), activeThreads));
            }
        });
        thread.setName(String.format("AJ.Async[%d] (%s)", ++count, name));
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
