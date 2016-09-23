package applica.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import applica.aj.AJObject;
import applica.aj.Store;
import applica.aj.library.AJApp;
import applica.app.Actions;
import applica.app.R;
import applica.app.Stores;
import applica.framework.android.ui.injector.InjectView;
import applica.framework.android.ui.injector.Injector;
import applica.framework.android.utils.Nulls;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.text)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.resolve(this);

        AJApp.runtime().subscribe(Stores.HOME, this, new Store.Subscription() {
            @Override
            public void handle(AJObject state) {
                String text = Nulls.require(state.get("message").asString());

                mTextView.setText(text);
            }
        });

        AJApp.runtime().run(Actions.GET_MESSAGE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AJApp.runtime().unsubscribe(Stores.HOME, this);
    }
}
