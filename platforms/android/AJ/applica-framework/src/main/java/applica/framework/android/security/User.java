package applica.framework.android.security;

import java.util.Date;
import java.util.List;

public interface User {

    String getUsername();
    String getPassword();
    boolean isActive();
    Date getRegistrationDate();
    List<? extends Role> getRoles();


}
