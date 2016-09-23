package applica.framework.android.ui.injector;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import applica.framework.android.utils.TypeUtils;

/**
 * Created by bimbobruno on 14/03/16.
 */
public class Injector {

    public static void resolve(final Activity owner) {
        List<Field> fields = TypeUtils.getAllFields(owner.getClass());
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                View v = owner.findViewById(injectView.value());
                field.setAccessible(true);
                try {
                    field.set(owner, v);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Method[] methods = owner.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            Click click = method.getAnnotation(Click.class);
            if (click != null) {
                int id = click.value();

                View v = owner.findViewById(id);
                if (v != null) {
                    method.setAccessible(true);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                method.invoke(owner);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    public static void resolve(final Object owner, final View root) {
        List<Field> fields = TypeUtils.getAllFields(owner.getClass());
        for (Field field : fields) {
            InjectView injectView = field.getAnnotation(InjectView.class);
            if (injectView != null) {
                View v = root.findViewById(injectView.value());
                field.setAccessible(true);
                try {
                    field.set(owner, v);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Method[] methods = owner.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            Click click = method.getAnnotation(Click.class);
            if (click != null) {
                int id = click.value();

                View v = root.findViewById(id);
                if (v != null) {
                    method.setAccessible(true);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                method.invoke(owner);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

}
