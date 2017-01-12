package applica.aj;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by bimbobruno on 10/01/2017.
 */

public class AJDiff {


    private final AJObject original;
    private String path;

    public AJDiff(AJObject original) {
        this.original = original;
    }

    public AJDiff at(String path) {
        this.path = path;
        return this;
    }

    public boolean differsFrom(AJObject other) {
        if (StringUtils.isNotEmpty(path)) {
            if (original != null && other != null) {
                AJValue o1 = original.at(path);
                AJValue o2 = other.at(path);

                return !o1.equals(o2);
            } else {
                return true;
            }
        } else {
            return !original.equals(other);
        }
    }

    public boolean from(AJObject other) {
        return differsFrom(other);
    }
}
