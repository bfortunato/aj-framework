package applica.framework.android.services;

/**
 * Created by bimbobruno on 29/01/16.
 */
public interface ServiceBuilder<T> {

    T build(Object... params);

}
