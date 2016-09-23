package applica.framework.android.device;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import applica.framework.android.utils.CollectionUtils;

/**
 * Created by bimbobruno on 18/09/16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothPeripheral extends BluetoothGattServerCallback {

    public static final int BLUETOOTH_PERMISSION_REQUEST = 1002;

    class Characteristic {
        private String id;
        private String value;
    }

    class Service {
        private String id;
        private List<Characteristic> characteristics = new ArrayList<>();
    }

    public interface Listener {
        void onReady();
        void onStartAdvertising();
        void onWriteValue(String serviceId, String characteristicId, String value);
    }

    private List<Service> services = new ArrayList<>();
    private List<Listener> listeners = new ArrayList<>();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer gattServer;

    private Context context;
    private String localName;

    public BluetoothPeripheral(Context context) {
        this.context = context;
    }

    public static void checkBluetoothPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    BLUETOOTH_PERMISSION_REQUEST
            );
        }
    }

    public static void enableBluetooth(Activity activity) {
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(enableBtIntent);
        }
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    private void doStartAdvertise() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        if (localName != null) {
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_LOCAL_NAME, localName);
        }
        context.startActivity(discoverableIntent);

        for (Listener listener : listeners) {
            listener.onStartAdvertising();
        }
    }

    private void doStartLeAdvertise() {
        if (bluetoothLeAdvertiser == null) { return; }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (localName != null) {
                bluetoothAdapter.setName(localName);
            }

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setConnectable(true)
                    .setTimeout(0)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .build();

            AdvertiseData.Builder data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(localName != null);

            for (Service s : services) {
                data.addServiceUuid(ParcelUuid.fromString(s.id));
            }

            bluetoothLeAdvertiser.startAdvertising(settings, data.build(), new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);

                    for (Listener listener : listeners) {
                        listener.onStartAdvertising();
                    }

                    Log.i("AF", "Bluetooth le advertising started");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);

                    Log.i("AF", "Bluetooth le advertising failed");
                }
            });
        }
    }

    private void doStopLeAdvertise() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeAdvertiser.stopAdvertising(new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                }
            });
        }
    }

    public void configure() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            toast("Please enable bluetooth");
            return;
        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast("No LE Support.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
                toast("No Advertising Support");
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        }

        if (gattServer != null) {
            gattServer.close();
        }

        gattServer = bluetoothManager.openGattServer(context, this);

        for (Service s : services) {
            BluetoothGattService service = new BluetoothGattService(UUID.fromString(s.id), BluetoothGattService.SERVICE_TYPE_PRIMARY);

            for (Characteristic c : s.characteristics) {
                BluetoothGattCharacteristic charateristic = new BluetoothGattCharacteristic(
                        UUID.fromString(c.id),
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE
                );

                service.addCharacteristic(charateristic);
            }

            gattServer.addService(service);
        }

        for (Listener listener : listeners) {
            listener.onReady();
        }
    }

    private void toast(final String message) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            doStartLeAdvertise();
        } else {
            doStartAdvertise();
        }
    }

    public void stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            doStopLeAdvertise();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            gattServer.close();
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void addService(final String id) {
        Service current = CollectionUtils.first(services, new CollectionUtils.Predicate<Service>() {
            @Override
            public boolean evaluate(Service obj) {
                return obj.id.equals(id.toUpperCase());
            }
        });

        if (current != null) {
            throw new RuntimeException("Service already added: " + id);
        }

        Service service = new Service();
        service.id = id;

        services.add(service);
    }

    public void addCharacteristic(final String serviceId, final String characteristicId, final String value) {
        Service service = CollectionUtils.first(services, new CollectionUtils.Predicate<Service>() {
            @Override
            public boolean evaluate(Service obj) {
                return obj.id.equals(serviceId.toUpperCase());
            }
        });

        if (service == null) {
            throw new RuntimeException("Service not found: " + serviceId);
        }

        Characteristic characteristic = new Characteristic();
        characteristic.id = characteristicId;
        characteristic.value = value;

        service.characteristics.add(characteristic);


    }

    public void setValue(final String serviceId, final String characteristicId, final String value) {
        Service service = CollectionUtils.first(services, new CollectionUtils.Predicate<Service>() {
            @Override
            public boolean evaluate(Service obj) {
                return obj.id.equals(serviceId.toUpperCase());
            }
        });

        if (service == null) {
            throw new RuntimeException("Service not found: " + serviceId);
        }

        Characteristic characteristic = CollectionUtils.first(service.characteristics, new CollectionUtils.Predicate<Characteristic>() {
            @Override
            public boolean evaluate(Characteristic obj) {
                return obj.id.equals(characteristicId.toUpperCase());
            }
        });

        if (characteristic == null) {
            throw new RuntimeException("Characteristic not found: " + characteristicId);
        }

        characteristic.value = value;
    }



    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        super.onConnectionStateChange(device, status, newState);

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("AF", "Gatt server ready");
        }
    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        super.onServiceAdded(status, service);

        Log.i("AF", "Bluetooth service added: " + service.getUuid().toString().toUpperCase());
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, final BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

        Service service = CollectionUtils.first(services, new CollectionUtils.Predicate<Service>() {
            @Override
            public boolean evaluate(Service obj) {
                return obj.id.equals(characteristic.getService().getUuid().toString().toUpperCase());
            }
        });

        if (service == null) {
            Log.w("AF", "Bluetooth service not found for read request: " + characteristic.getService().getUuid().toString().toUpperCase());

            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);

            return;
        }

        Characteristic c = CollectionUtils.first(service.characteristics, new CollectionUtils.Predicate<Characteristic>() {
            @Override
            public boolean evaluate(Characteristic obj) {
                return obj.id.equals(characteristic.getUuid().toString().toUpperCase());
            }
        });

        if (c == null) {
            Log.w("AF", "Bluetooth characteristic not found for read request: " + characteristic.getUuid().toString().toUpperCase());

            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);

            return;
        }

        byte[] value = c.value != null ? c.value.getBytes(Charset.forName("UTF8")) : null;

        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, final BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

        Service service = CollectionUtils.first(services, new CollectionUtils.Predicate<Service>() {
            @Override
            public boolean evaluate(Service obj) {
                return obj.id.equals(characteristic.getService().getUuid().toString().toUpperCase());
            }
        });

        if (service == null) {
            Log.w("AF", "Bluetooth service not found for write request: " + characteristic.getService().getUuid().toString().toUpperCase());

            if (responseNeeded) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }

            return;
        }

        Characteristic c = CollectionUtils.first(service.characteristics, new CollectionUtils.Predicate<Characteristic>() {
            @Override
            public boolean evaluate(Characteristic obj) {
                return obj.id.equals(characteristic.getUuid().toString().toUpperCase());
            }
        });

        if (c == null) {
            Log.w("AF", "Bluetooth characteristic not found for write request: " + characteristic.getUuid().toString().toUpperCase());

            if (responseNeeded) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }

            return;
        }

        String sValue = value != null ? new String(value, Charset.forName("UTF8")) : null;

        c.value = sValue;

        if (responseNeeded) {
            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
        }

        for (Listener listener : listeners) {
            listener.onWriteValue(service.id, c.id, sValue);
        }
    }
}
