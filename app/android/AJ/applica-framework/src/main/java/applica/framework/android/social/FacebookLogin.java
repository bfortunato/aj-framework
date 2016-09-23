package applica.framework.android.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import applica.framework.android.utils.Listener;

/**
 * Created by bimbobruno on 29/01/16.
 */
public class FacebookLogin implements FacebookCallback<LoginResult> {

    public static boolean initialized = false;

    private Listener<AccessToken> listener;
    private Activity activity;
    private CallbackManager callbackManager;

    public FacebookLogin(Activity activity) {
        checkInitialization();

        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);
    }

    public static void initialize(Context applicationContext) {
        FacebookSdk.sdkInitialize(applicationContext);
        initialized = true;
    }

    public void login(String readPermissions, Listener<AccessToken> listener) {
        this.listener = null;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            this.listener = listener;
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(readPermissions.split(",")));
        } else {
            listener.onSuccess(accessToken);
        }
    }

    public static void logout() {
        LoginManager.getInstance().logOut();
    }

    private void checkInitialization() {
        if (!initialized) {
            throw new RuntimeException("FacebookLogin not initialized. Please call FacebookLogin.initialize(applicationContext) in Application.onCreate()");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if (this.listener != null) {
            listener.onSuccess(loginResult.getAccessToken());
        }
    }

    @Override
    public void onCancel() {
        if (this.listener != null) {
            listener.onError("cancel");
        }
    }

    @Override
    public void onError(FacebookException error) {
        if (this.listener != null) {
            listener.onError(error.getMessage());
        }
    }
}
