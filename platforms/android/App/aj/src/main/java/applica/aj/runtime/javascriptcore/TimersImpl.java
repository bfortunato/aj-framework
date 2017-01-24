package applica.aj.runtime.javascriptcore;

import android.os.Handler;
import android.os.Looper;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.util.ArrayList;
import java.util.List;

import applica.aj.Async;
import applica.framework.android.utils.CollectionUtils;
import applica.framework.android.utils.Nulls;

/**
 * Created by bimbobruno on 16/03/16.
 */
public class TimersImpl extends JSObject implements Timers {

    static int COUNTER = 0;

    private class TimerAction {
        long id = ++COUNTER;
        JSValue action;
        long delay;
        boolean canceled = false;
        boolean loop;

        void execute() {
            Async.runDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!canceled) {
                        try {
                            action.toFunction().call(jsContext, new JSValue[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (loop) {
                            execute();
                            return;
                        }
                    }

                    destroy(TimerAction.this);
                }
            }, delay);
        }

        void cancel() {
            canceled = true;
        }
    }

    private final JSContext jsContext;
    private List<TimerAction> actions = new ArrayList<>();
    private Handler handler;
    private Thread thread;

    public TimersImpl(JSContext jsContext) {
        super(jsContext, Timers.class);
        this.jsContext = jsContext;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler();
                Looper.loop();
            }
        });

        thread.start();
    }

    @Override
    public Long setTimeout(JSValue action, Long delay) {
        if (action == null) {
            return 0L;
        }

        TimerAction timerAction = new TimerAction();
        timerAction.action = action;
        timerAction.delay = Nulls.orElse(delay, 0L);
        timerAction.loop = false;
        actions.add(timerAction);

        timerAction.execute();

        return timerAction.id;
    }

    @Override
    public Long setInterval(JSValue action, Long delay) {
        if (action == null) {
            return 0L;
        }

        TimerAction timerAction = new TimerAction();
        timerAction.action = action;
        timerAction.delay = Nulls.orElse(delay, 0L);
        timerAction.loop = true;
        actions.add(timerAction);

        timerAction.execute();

        return timerAction.id;
    }

    @Override
    public void clearTimeout(final Long timerId) {
        if (timerId == null) {
            return;
        }
        TimerAction action = CollectionUtils.first(actions, new CollectionUtils.Predicate<TimerAction>() {
            @Override
            public boolean evaluate(TimerAction obj) {
                return obj.id == timerId;
            }
        });
        if (action != null) {
            action.cancel();
        }
    }

    private void destroy(TimerAction timerAction) {
        timerAction.action = null; //to gc
        actions.remove(timerAction);
    }
}
