package applica.framework.android.social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by bimbobruno on 29/01/16.
 */
public class FacebookLoginFragmentActivity extends FragmentActivity implements FacebookLoginHost {

    private FacebookLogin facebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        facebookLogin = new FacebookLogin(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public FacebookLogin getFacebookLogin() {
        return facebookLogin;
    }
}
