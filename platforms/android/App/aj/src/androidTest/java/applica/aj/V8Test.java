package applica.aj;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import applica.aj.runtime.v8.AJV8Runtime;

/**
 * Created by bimbobruno on 1/5/17.
 */

public class V8Test {

    @Test
    public void testV8Runtime() {
        AJV8Runtime runtime = new AJV8Runtime(InstrumentationRegistry.getContext());
    }

}
