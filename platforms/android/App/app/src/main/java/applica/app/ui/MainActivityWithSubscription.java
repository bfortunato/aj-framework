package applica.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import applica.aj.AJ;
import applica.aj.AJObject;
import applica.aj.Store;
import applica.app.Actions;
import applica.app.R;
import applica.app.Stores;
import applica.framework.android.ui.injector.InjectView;
import applica.framework.android.ui.injector.Injector;
import applica.framework.android.utils.Nulls;

public class MainActivityWithSubscription extends AppCompatActivity {

    @InjectView(R.id.text)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.resolve(this);

        AJ.subscribe(Stores.HOME, this, new Store.Subscription() {
            @Override
            public void handle(AJObject state) {
                String text = Nulls.require(state.get("message").asString());

                mTextView.setText(text);
            }
        });

        AJ.run(Actions.GET_MESSAGE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AJ.unsubscribe(Stores.HOME, this);
    }
}
