package applica.aj.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import applica.aj.AJ;
import applica.aj.AJObject;
import applica.aj.Store;

/**
 * Created by bimbobruno on 14/03/2017.
 */

public abstract class AJActivity extends AppCompatActivity {

    private StoreDefinitions mDefinitions = new StoreDefinitions();
    private Map<String, AJObject> mLastStates = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defineStores(mDefinitions);
        initStores();
        onSetupView();
        onViewLoaded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deinitStore();
    }

    protected void initStores() {
        for (final String store : mDefinitions.getStores()) {
            AJ.subscribe(store, this, new Store.Subscription() {
                @Override
                public void handle(AJObject state) {
                    AJObject lastState = getLastState(store);
                    onUpdateView(store, state, lastState);
                }
            });
        }
    }

    private AJObject getLastState(String store) {
        if (mLastStates.containsKey(store)) {
            return mLastStates.get(store);
        } else {
            return AJObject.empty();
        }
    }

    protected void deinitStore() {
        for (final String store : mDefinitions.getStores()) {
            AJ.unsubscribe(store, this);
        }
    }

    protected abstract void defineStores(StoreDefinitions definitions);
    protected abstract void onSetupView();
    protected abstract void onUpdateView(String store, AJObject state, AJObject lastState);
    protected abstract void onViewLoaded();
}
