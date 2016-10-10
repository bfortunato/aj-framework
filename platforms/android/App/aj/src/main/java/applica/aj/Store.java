package applica.aj;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import applica.aj.runtime.AJRuntime;
import applica.framework.android.utils.CollectionUtils;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class Store {

    private Lock lock = new ReentrantLock(true);
    private AJObject state;

    public interface Subscription {
        void handle(AJObject state);
    }

    private class SubscriptionInfo {
        Object owner;
        Subscription subscription;

        public SubscriptionInfo(Object owner, Subscription subscription) {
            this.owner = owner;
            this.subscription = subscription;
        }
    }

    private String type;
    private AJRuntime runtime;
    private List<SubscriptionInfo> subscriptions = new ArrayList<>();

    public Store(AJRuntime runtime, String type) {
        this.runtime = runtime;
        this.type = type;
    }

    public AJRuntime getRuntime() {
        return runtime;
    }

    public String getType() {
        return type;
    }

    public void subscribe(Object owner, Subscription subscription) {
        lock.lock();
        try {
            this.subscriptions.add(new SubscriptionInfo(owner, subscription));
        } finally {
            lock.unlock();
        }
    }

    public void unsubscribe(final Object owner) {
        Log.i("AJ", String.format("%s unsubscribed from store %s", owner.toString(), type));

        lock.lock();
        try {
            subscriptions = new ArrayList<>(CollectionUtils.filter(subscriptions, new CollectionUtils.Predicate<SubscriptionInfo>() {
                @Override
                public boolean evaluate(SubscriptionInfo obj) {
                    return obj.owner != owner;
                }
            }));
        } finally {
            lock.unlock();
        }
    }

    public void unsubscribeAll() {
        lock.lock();
        try {
            this.subscriptions.clear();
        } finally {
            lock.unlock();
        }
    }

    public void trigger(final AJObject data) {
        this.state = data;

        Handler handler = new Handler(runtime.getContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final List<SubscriptionInfo> safeSubscriptions = new ArrayList<>(subscriptions);
                for (final SubscriptionInfo s : safeSubscriptions) {
                    s.subscription.handle(data);
                }
            }
        });
    }

    public AJObject getState() {
        return state;
    }
}
