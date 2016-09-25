package applica.aj;

/**
 * Created by bimbobruno on 10/03/16.
 */

public class AJNumberConverter {
    public static Integer toInt(Object number) {
        if (number == null) {
            return null;
        }
        if (number instanceof Double) {
            return (int) (double) (Double) number;
        }
        if (number instanceof Float) {
            return (int) (float) (Float) number;
        }
        if (number instanceof Long) {
            return (int) (long) (Long) number;
        }
        if (number instanceof Integer) {
            return (int) (int) (Integer) number;
        }

        return null;
    }

    public static Long toLong(Object number) {
        if (number == null) {
            return null;
        }
        if (number instanceof Double) {
            return (long) (double) (Double) number;
        }
        if (number instanceof Float) {
            return (long) (float) (Float) number;
        }
        if (number instanceof Long) {
            return (long) (long) (Long) number;
        }
        if (number instanceof Integer) {
            return (long) (int) (Integer) number;
        }

        return null;
    }

    public static Float toFloat(Object number) {
        if (number == null) {
            return null;
        }
        if (number instanceof Double) {
            return (float) (double) (Double) number;
        }
        if (number instanceof Float) {
            return (float) (float) (Float) number;
        }
        if (number instanceof Long) {
            return (float) (long) (Long) number;
        }
        if (number instanceof Integer) {
            return (float) (int) (Integer) number;
        }

        return null;
    }

    public static Double toDouble(Object number) {
        if (number == null) {
            return null;
        }
        if (number instanceof Double) {
            return (double) (double) (Double) number;
        }
        if (number instanceof Float) {
            return (double) (float) (Float) number;
        }
        if (number instanceof Long) {
            return (double) (long) (Long) number;
        }
        if (number instanceof Integer) {
            return (double) (int) (Integer) number;
        }

        return null;
    }
}
