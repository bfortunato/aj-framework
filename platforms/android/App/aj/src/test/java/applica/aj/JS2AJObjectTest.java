package applica.aj;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by bimbobruno on 11/03/16.
 */
public class JS2AJObjectTest {

    @Test
    public void a2jTest() {
        String json = "{\"name\": \"bruno\", \"array\": [1, 2, 3, 4, 5, [10, 20, 30], {\"a\": 1, \"b\": 2}]}";

        byte[] buffer = new byte[0];
        try {
            buffer = FileUtils.readFileToByteArray(new File("/Users/bimbobruno/Desktop/screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        AJObject aj = AJObject.fromJson(json);
        //aj.set("buffer", buffer);

        String serialized = aj.toJson();
        String deserialized;

        System.out.println(serialized);
        int i = 0;
        long start = System.currentTimeMillis();
        while (i < 10000) {
            AJObject other = AJObject.fromJson(serialized);
            other.toJson();
            i++;
        }
        System.out.println(System.currentTimeMillis() - start);


        Assert.assertEquals(serialized, serialized);


    }

}
