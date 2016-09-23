package applica.framework.android.social;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bimbobruno on 29/01/16.
 */
public class FacebookLoginAppCompactActivity extends AppCompatActivity implements FacebookLoginHost {

    private FacebookLogin facebookLogin;

    public FacebookLoginAppCompactActivity() {
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
