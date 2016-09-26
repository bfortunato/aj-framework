package applica.framework.android.device;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

import com.google.zxing.Result;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by bimbobruno on 17/09/16.
 */

public class QRCodeScanner implements ZXingScannerView.ResultHandler {

    public static final int CAMERA_PERMISSION_REQUEST = 1001;

    private final Activity context;

    public interface Listener {

        void onScan(String result);
    }
    private final ViewGroup container;

    private List<Listener> listeners = new ArrayList<>();
    private ZXingScannerView scannerView;

    public QRCodeScanner(Activity context, ViewGroup container) {
        this.context = context;
        this.container = container;

        requestPermissionsAndInit();
    }

    private void requestPermissionsAndInit() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        } else {
            initCamera();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initCamera();
                        open();
                    }
                }, 100);
            }
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private void initCamera() {
        scannerView = new ZXingScannerView(context);
        container.addView(scannerView);
    }

    public void open() {
        Assert.notNull(scannerView);

        scannerView.setResultHandler(QRCodeScanner.this);
        scannerView.startCamera();
    }

    public void close() {
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        for (Listener listener : listeners) {
            //Vibrator vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            //vibrator.vibrate(100);

            listener.onScan(result.getText());
        }
    }

}
