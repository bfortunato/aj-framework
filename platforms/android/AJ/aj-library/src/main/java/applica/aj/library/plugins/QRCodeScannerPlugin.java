package applica.aj.library.plugins;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import applica.aj.AJObject;
import applica.aj.library.AJApp;
import applica.aj.runtime.Plugin;
import applica.framework.android.device.QRCodeScanner;

/**
 * Created by bimbobruno on 17/09/16.
 */
public class QRCodeScannerPlugin extends Plugin {
    public QRCodeScannerPlugin() {
        super("QRCodeScanner");
    }

    private boolean initialized = false;
    private QRCodeScanner qrCodeScanner;

    public void configureInView(Activity context, ViewGroup viewGroup) {
        if (qrCodeScanner != null) {
            qrCodeScanner.close();
        }

        initialized = false;

        qrCodeScanner = new QRCodeScanner(context, viewGroup);
    }

    public AJObject open(final AJObject data) {
        Assert.notNull(qrCodeScanner, "Please call configureInView before");

        if (!initialized) {
            qrCodeScanner.addListener(new QRCodeScanner.Listener() {
                @Override
                public void onScan(String result) {
                    String onQrCodeAction = data.get("onQrCodeAction").asString();

                    if (StringUtils.isNotEmpty(onQrCodeAction)) {
                        AJApp.runtime().run(onQrCodeAction, AJObject.create().set("result", result));
                    }
                }
            });

            initialized = true;
        }

        qrCodeScanner.open();

        return AJObject.empty();
    }

    public AJObject close(AJObject data) {
        Assert.notNull(qrCodeScanner, "Please call configureInView before");

        qrCodeScanner.close();

        return AJObject.empty();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        qrCodeScanner.onRequestPermissionsResult(requestCode,   permissions, grantResults);
    }
}
