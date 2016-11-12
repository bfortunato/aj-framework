package applica.aj;

import applica.aj.runtime.Plugin;

/**
 * Contains shortcuts to call AJ framework common methods
 */

public class AJ {

    public static Store getStore(String type) {
        return AJApp.runtime().getStore(type);
    }

    public static void subscribe(String store, Object owner, Store.Subscription subscription) {
        AJApp.runtime().subscribe(store, owner, subscription);
    }

    public static void unsubscribe(String store, Object owner) {
        AJApp.runtime().unsubscribe(store, owner);
    }

    public static void registerPlugin(Plugin plugin) {
        AJApp.runtime().registerPlugin(plugin);
    }

    public static Plugin getPlugin(String plugin) {
        return AJApp.runtime().getPlugin(plugin);
    }

    public static AJObject exec(final String plugin, final String fn, final AJObject data) {
        return AJApp.runtime().exec(plugin, fn, data);
    }

    public static Semaphore run(final String action) {
        return AJApp.runtime().run(action);
    }

    public static Semaphore run(final String action, final AJObject data) {
        return AJApp.runtime().run(action, data);
    }

}
