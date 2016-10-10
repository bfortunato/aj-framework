package applica.framework.android.device;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import applica.framework.android.utils.BitmapUtils;
import applica.framework.android.utils.Listener;

/**
 * Created by bimbobruno on 08/10/16.
 */

public class CameraImagePicker {

    public static final int REQUEST_CODE = 1003;
    public static final int PERMISSION_REQUEST = 2;

    private final Listener<Bitmap> listener;
    private Uri cameraTempUri;
    private Activity activity;
    private boolean ready;
    private Runnable onReadyAction;

    public CameraImagePicker(Activity activity, Listener<Bitmap> listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void init() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST
            );
        } else {
            if (onReadyAction != null) {
                onReadyAction.run();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (onReadyAction != null) {
                    onReadyAction.run();
                }
            }
        }
    }

    public void pick() {
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Capture Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Capture image from your camera");
        try {
            cameraTempUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {

        }
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraTempUri);
        activity.startActivityForResult(captureIntent, REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null && cameraTempUri == null) {
            invokeListener(null);
            return;
        }

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = null;
            if (data != null) {
                selectedImage = data.getData();
            }
            if (selectedImage == null) {
                selectedImage = cameraTempUri;
            }
            cameraTempUri = null;

            if (selectedImage == null) {
                //image is stored as bitmap in data

                if (data.getExtras() == null) {
                    invokeListener(null);
                    return;
                }

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                invokeListener(bitmap);
            } else {
                Bitmap bitmap = BitmapUtils.loadBitmapResampledAndRotated(activity, selectedImage);
                invokeListener(bitmap);
            }
        }
    }

    private void invokeListener(Bitmap bitmap) {
        if (listener != null) {
            listener.onSuccess(bitmap);
        }
    }


    public void setOnReadyAction(Runnable onReadyAction) {
        this.onReadyAction = onReadyAction;

        if (this.ready) {
            onReadyAction.run();
        }
    }
}
