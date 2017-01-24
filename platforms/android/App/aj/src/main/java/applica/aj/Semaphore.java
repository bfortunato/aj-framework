package applica.aj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class Semaphore {

    public interface Listener {
        void onGreen();
    }

    private static int COUNTER = 0;

    private boolean complete = false;
    private int id = ++COUNTER;
    private Collection<Listener> listeners = Collections.synchronizedCollection(new ArrayList<Listener>());
    private Thread thread;

    public Semaphore(Runnable action) {
        if (action != null) {
            runAction(action);
        }
    }

    public void runAction(final Runnable action) {
        //thread = Async.run("Semaphore.runAction", new Runnable() {
        Async.run("Semaphore.runAction", new Runnable() {
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
                //Async.wait(thread, (int) (timeout * 1000));
            } else {
                //Async.wait(thread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }
}
