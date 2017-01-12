package applica.aj;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by bimbobruno on 10/01/2017.
 */

public class AJObjectEqualityTest {

    @Test
    public void valueTest() {
        String me = "{ " +
                        "\"name\": \"bruno\", " +
                        "\"age\": 30, " +
                        "\"active\": true, " +
                        "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                        "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                     "}";

        String equal = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff1 = "{ " +
                "\"name\": \"bruno different\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff2 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": false, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff3 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 5, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff4 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff5 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5], \"last\"]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff6 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43, \"otherPropInside\": 3242378}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
                "}";

        String diff7 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop1\": \"ciao ciao\", \"prop2\": 234}" +
                "}";

        String diff8 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
                "\"inner\": { \"prop2\": 234}" +
                "}";

        String diff9 = "{ " +
                "\"name\": \"bruno\", " +
                "\"age\": 30, " +
                "\"active\": true, " +
                "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]" +
                "}";

        assertAreEquals(me, equal);
        assertAreNotEquals(me, diff1);
        assertAreNotEquals(me, diff2);
        assertAreNotEquals(me, diff3);
        assertAreNotEquals(me, diff4);
        assertAreNotEquals(me, diff5);
        assertAreNotEquals(me, diff6);
        assertAreNotEquals(me, diff7);
        assertAreNotEquals(me, diff8);
        assertAreNotEquals(me, diff9);

        AJObject obj = AJObject.fromJson(me);
        AJObject dob8 = AJObject.fromJson(diff8);
        AJObject dob7 = AJObject.fromJson(diff7);

        Assert.assertEquals(obj.at("inner.prop1").asString(), "ciao");
        Assert.assertFalse(obj.differsAt("inner.prop2").from(dob8));
        Assert.assertTrue(obj.differsAt("inner.prop1").from(dob7));

    }

    private void assertAreEquals(String me, String equal) {
        AJObject o1 = AJObject.fromJson(me);
        AJObject o2 = AJObject.fromJson(equal);

        Assert.assertTrue(o1.equals(o2));
    }

    private void assertAreNotEquals(String me, String equal) {
        AJObject o1 = AJObject.fromJson(me);
        AJObject o2 = AJObject.fromJson(equal);

        Assert.assertFalse(o1.equals(o2));
    }

}
