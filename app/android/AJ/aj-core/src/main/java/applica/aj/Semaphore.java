package applica.aj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class Semaphore {

    public interface Listener {
        void onGreen();
    }

    private static int COUNTER = 0;
    private static ExecutorService EXECUTOR;

    static {
        EXECUTOR = Executors.newFixedThreadPool(10);
    }

    private boolean complete = false;
    private int id = ++COUNTER;
    private Collection<Listener> listeners = Collections.synchronizedCollection(new ArrayList<Listener>());
    private Future future;

    public Semaphore(Runnable action) {
        if (action != null) {
            runAction(action);
        }
    }

    public void runAction(final Runnable action) {
        future = EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                action.run();
                free();
            }
        });
    }

    private void free() {

        for (Listener listener : listeners) {
            listener.onGreen();
        }

        complete = true;
    }

    public Semaphore then(Listener listener) {
        listeners.add(listener);

        if (complete) {
            listener.onGreen();
        }

        return this;
    }

    public void await() {
        await(null);
    }

    public void await(Double timeout) {
        try {
            if (timeout != null) {
                future.get((long) (timeout * 1000), TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }
}
