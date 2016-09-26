package applica.framework.android.security;

import java.util.ArrayList;
import java.util.List;

import applica.framework.android.utils.CollectionUtils;

/**
 * Created by bimbobruno on 28/01/16.
 */
public class Profile {

    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";

    private String firstName;
    private String lastName;
    private String gender;
    private String image;

    private List<ProfileProperty> properties = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProperty(final String key) {
        ProfileProperty property = CollectionUtils.first(properties, new CollectionUtils.Predicate<ProfileProperty>() {
            @Override
            public boolean evaluate(ProfileProperty obj) {
                return obj.getKey().equals(key);
            }
        });

        if (property != null) {
            return property.getValue();
        }

        return null;
    }

    public String setProperty(final String key, String value) {
        ProfileProperty property = CollectionUtils.first(properties, new CollectionUtils.Predicate<ProfileProperty>() {
            @Override
            public boolean evaluate(ProfileProperty obj) {
                return obj.getKey().equals(key);
            }
        });

        if (property != null) {
            property = Profile.newProperty(key);
            properties.add(property);
        }

        property.setValue(value);

        return null;
    }

    public static final ProfileProperty newProperty() {
        return newProperty(null, null);
    }

    public static final ProfileProperty newProperty(String key) {
        return newProperty(key, null);
    }

    public static final ProfileProperty newProperty(String key, String value) {
        ProfileProperty property = new ProfileProperty(key, value);
        return property;
    }
}
