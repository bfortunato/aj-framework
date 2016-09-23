package applica.aj.library.plugins;

import android.content.Context;

import applica.aj.AJArray;
import applica.aj.AJObject;
import applica.aj.library.AJApp;
import applica.aj.runtime.Plugin;
import applica.framework.android.device.BluetoothPeripheral;
import applica.framework.android.utils.Nulls;

/**
 * Created by bimbobruno on 18/09/16.
 */
public class BluetoothPeripheralPlugin extends Plugin {

    private Context context;
    private BluetoothPeripheral peripheral;

    public BluetoothPeripheralPlugin(Context context) {
        super("BluetoothPeripheral");

        this.context = context;
    }

    public AJObject configure(AJObject data) {
        final String name = Nulls.require(data.get("name").asString());
        final AJArray services = Nulls.require(data.get("services").asArray());
        final String onChangeCharacteristicValueAction = Nulls.require(data.get("onChangeCharacteristicValueAction").asString());
        final String onSetReadyAction = Nulls.require(data.get("onSetReadyAction").asString());

        peripheral = new BluetoothPeripheral(context);
        peripheral.setLocalName(name);

        for (int i = 0; i < services.count(); i++) {
            String serviceId = Nulls.require(services.objectAt(i).get("id").asString());

            peripheral.addService(serviceId);

            AJArray characteristics = Nulls.require(services.objectAt(i).get("characteristics").asArray());
            for (int ci = 0; ci < characteristics.count(); ci++) {
                String characteristicId = characteristics.objectAt(ci).get("id").asString();
                String value = characteristics.objectAt(ci).get("value").asString();

                peripheral.addCharacteristic(serviceId, characteristicId, value);
            }
        }

        peripheral.addListener(new BluetoothPeripheral.Listener() {
            @Override
            public void onReady() {
                AJApp.runtime().run(onSetReadyAction);
            }

            @Override
            public void onStartAdvertising() {

            }

            @Override
            public void onWriteValue(String serviceId, String characteristicId, String value) {
                AJApp.runtime().run(
                        onChangeCharacteristicValueAction,
                        AJObject.create()
                            .set("serviceId", serviceId)
                            .set("characteristicId", characteristicId)
                            .set("value", value)
                );
            }
        });

        peripheral.configure();

        return AJObject.empty();
    }

    public AJObject startAdvertising(AJObject data) {
        Nulls.require(peripheral).startAdvertising();

        return AJObject.empty();
    }

    public AJObject stopAdvertising(AJObject data) {
        Nulls.require(peripheral).stopAdvertising();

        return AJObject.empty();
    }

    public AJObject setValue(AJObject data) {
        return AJObject.empty();
    }
}
