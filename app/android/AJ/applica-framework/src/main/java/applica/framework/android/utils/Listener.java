package applica.framework.android.utils;

/**
 * Created by bimbobruno on 28/01/16.
 */
public interface Listener<T> {

    void onSuccess(T response);
    void onError(String error);

}
