package applica.framework.android.utils;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class Nulls {

    public static boolean areNotNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                return false;
            }
        }

        return true;
    }

    public static <T> T orElse(T obj, T defaultValue) {
        if (obj != null) {
            return obj;
        } else {
            return defaultValue;
        }
    }

    public static <T> T require(T obj) {
        return require(obj, "Value is required");
    }

    public static <T> T require(T obj, String exceptionMessage) {
        if (obj == null) {
            throw new RuntimeException(exceptionMessage);
        }

        return obj;
    }
}
