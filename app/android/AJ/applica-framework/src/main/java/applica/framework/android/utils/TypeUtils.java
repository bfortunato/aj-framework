package applica.framework.android.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 14/03/16.
 */
public class TypeUtils {

    public static boolean isList(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();

        doGetAllField(type, fields, false);

        return fields;
    }

    public static List<Field> getAllFields(Class<?> type, boolean ignoreSuperClasses) {
        List<Field> fields = new ArrayList<Field>();

        doGetAllField(type, fields, ignoreSuperClasses);

        return fields;
    }

    public static boolean implementsInterface(Class<?> type, Class<?> interfaceType, boolean searchInSuperclasses) {
        boolean found = false;
        for (Class<?> item : type.getInterfaces()) {
            if (item.equals(interfaceType)) {
                found = true;
                break;
            }
        }

        if (found) {
            return true;
        } else {
            if (searchInSuperclasses && type.getSuperclass() != null) {
                return implementsInterface(type.getSuperclass(), interfaceType, searchInSuperclasses);
            }
        }

        return false;
    }

    public static void doGetAllField(Class<?> type, List<Field> fields, boolean ignoreSuperClasses) {
        for (Field newField : type.getDeclaredFields()) {
            boolean found = false;
            for (Field oldField : fields) {
                if (oldField.getName().equals(newField.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fields.add(newField);
            }
        }

        if (type.getSuperclass() != null && !ignoreSuperClasses) {
            doGetAllField(type.getSuperclass(), fields, ignoreSuperClasses);
        }
    }
}
