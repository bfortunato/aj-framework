package applica.framework.android.utils;

/**
 * Created by bimbobruno on 01/02/16.
 */
public class Box<T> {

    public T value;

    public Box(T value) {
        this.value = value;
    }

    public Box() {
    }

    public static <T> void switchValues(Box<T> front, Box<T> back) {
        T tmp = front.value;
        front.value = back.value;
        back.value = tmp;
    }
}
