package applica.aj.runtime;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import applica.aj.AJObject;
import applica.aj.Semaphore;
import applica.aj.Store;

/**
 * Created by bimbobruno on 10/03/16.
 */
public abstract class AJRuntime {

    private Map<String, Store> stores = new HashMap<>();
    private Map<String, Plugin> plugins = new HashMap<>();

    protected final Context context;

    public AJRuntime(Context context) {
        this.context = context;
    }

    public Store getStore(String type) {
        if (stores.containsKey(type)) {
            return stores.get(type);
        } else {
            Store store = new Store(this, type);
            stores.put(type, store);

            return store;
        }
    }

    public void subscribe(String store, Object owner, Store.Subscription subscription) {
        getStore(store).subscribe(owner, subscription);
    }

    public void unsubscribe(String store, Object owner) {
        getStore(store).unsubscribe(owner);
    }

    public void trigger(String store, AJObject data) {
        getStore(store).trigger(data);;
    }

    public void registerPlugin(Plugin plugin) {
        assert plugin != null;

        Log.i("AJ", "Registered plugin " + plugin.getName());

        this.plugins.put(plugin.getName(), plugin);
    }

    public Plugin getPlugin(String plugin) {
        if (plugins.containsKey(plugin)) {
            return plugins.get(plugin);
        }

        throw new RuntimeException("Plugin not registered: " + plugin);
    }

    public AJObject exec(final String plugin, final String fn, final AJObject data) {
        Log.i("AJ", String.format("Executing plugin function %s.%s", plugin, fn));

        return getPlugin(plugin).exec(fn, data);
    }

    public void destroy() {
        for (Store store : stores.values()) {
            store.unsubscribeAll();
        }

        stores.clear();
    }

    public Context getContext() {
        return context;
    }

    public Semaphore run(final String action) {
        return run(action, AJObject.empty());
    }

    public abstract Semaphore run(final String action, final AJObject data);


}
