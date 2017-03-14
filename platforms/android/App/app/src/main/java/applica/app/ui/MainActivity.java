package applica.app.ui;

import android.widget.TextView;

import applica.aj.AJ;
import applica.aj.AJObject;
import applica.aj.ui.AJActivity;
import applica.aj.ui.StoreDefinitions;
import applica.app.Actions;
import applica.app.R;
import applica.app.Stores;
import applica.framework.android.ui.injector.InjectView;
import applica.framework.android.ui.injector.Injector;
import applica.framework.android.utils.Nulls;

public class MainActivity extends AJActivity {

    @InjectView(R.id.text)
    private TextView mTextView;

    @Override
    protected void defineStores(StoreDefinitions definitions) {
        definitions.addStore(Stores.HOME);
    }

    @Override
    protected void onSetupView() {
        setContentView(R.layout.activity_main);

        Injector.resolve(this);
    }

    @Override
    protected void onUpdateView(String store, AJObject state, AJObject lastState) {
        if (state.differsAt("message").from(lastState)) {
            String text = Nulls.require(state.get("message").asString());
            mTextView.setText(text);
        }
    }

    @Override
    protected void onViewLoaded() {
        AJ.run(Actions.GET_MESSAGE);
    }
}
