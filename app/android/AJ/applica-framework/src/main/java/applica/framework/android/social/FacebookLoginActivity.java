package applica.framework.android.social;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by bimbobruno on 29/01/16.
 */
public class FacebookLoginActivity extends Activity implements FacebookLoginHost {

    private FacebookLogin facebookLogin;

    public FacebookLoginActivity() {
        facebookLogin = new FacebookLogin(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public FacebookLogin getFacebookLogin() {
        return facebookLogin;
    }
}
