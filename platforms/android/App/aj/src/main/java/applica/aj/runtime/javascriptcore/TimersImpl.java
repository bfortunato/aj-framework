package applica.aj.runtime.javascriptcore;

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

    private final JSContext jsContext;

    private List<TimerAction> actions = new ArrayList<>();

    private class TimerAction {
        long id = ++COUNTER;
        JSValue action;
        long delay;
        boolean canceled = false;
        boolean loop;

        void execute() {
            Async.run(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {}
                        if (!canceled) {
                            action.toFunction().call(jsContext, new JSValue[0]);

                            if (!loop) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    destroy(TimerAction.this);
                }
            });
        }

        void cancel() {
            canceled = true;
        }
    }

    public TimersImpl(JSContext jsContext) {
        super(jsContext, Timers.class);
        this.jsContext = jsContext;
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
