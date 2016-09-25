package applica.framework.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

/**
 * Created by bimbobruno on 11/05/16.
 */
public class Loader {

    private final Activity context;

    public Loader(Activity context) {
        this.context = context;
    }

    ProgressDialog progressDialog;

    public void show(String title, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressDialog = null;
                }
            });
            progressDialog.show();
        } else {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
    }

    public void hide() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

}
