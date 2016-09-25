package applica.framework.android.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by bimbobruno on 29/01/16.
 */
public class CollectionUtils {

    public interface Predicate<T> {
        boolean evaluate(T obj);
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> filtered = new ArrayList<>();

        for (T item : collection) {
            if (predicate.evaluate(item)) {
                filtered.add(item);
            }
        }

        return filtered;
    }

    public static <T> T first(Collection<T> collection, Predicate<T> predicate) {
        return first(collection, predicate, null);
    }

    public static <T> T first(Collection<T> collection, Predicate<T> predicate, T defaultValue) {
        T single = defaultValue;

        for (T item : collection) {
            if (predicate.evaluate(item)) {
                single = item;
            }
        }

        return single;
    }

}
