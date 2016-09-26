package applica.aj;

import android.graphics.Bitmap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import applica.aj.runtime.Buffer;
import applica.framework.android.utils.CollectionUtils;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class AJObject {

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private List<AJValue> values = new ArrayList<>();

    public AJObject() {}

    public AJObject(String key, Object value) {
        set(key, value);
    }

    public static AJObject single(Object value) {
        return new AJObject("", value);
    }

    public static AJObject fromJson(String json) {
        AJObject a = new AJObject();
        a.loadJson(json);
        return a;
    }

    public AJObject set(final String key, final Object value) {
        AJValue v = CollectionUtils.first(values, new CollectionUtils.Predicate<AJValue>() {
            @Override
            public boolean evaluate(AJValue obj) {
                return key.equals(obj.getKey());
            }
        });

        if (v == null) {
            v = new AJValue(key, value);
            values.add(v);
        } else {
            v.setValue(value);
        }

        return this;
    }

    public AJValue get(final String key) {
        return CollectionUtils.first(values, new CollectionUtils.Predicate<AJValue>() {
            @Override
            public boolean evaluate(AJValue obj) {
                return key.equals(obj.getKey());
            }
        }, AJValue.EMPTY);
    }

    public AJValue first() {
        if (values.size() > 0) {
            return values.get(0);
        }

        return AJValue.EMPTY;
    }

    public int count() {
        return values.size();
    }
    
    public String asString() {
        return first().asString();
    }

    public Integer asInt() {
        return first().asInt();
    }

    public Long asLong() {
        return first().asLong();
    }

    public Boolean asBoolean() {
        return first().asBoolean();
    }

    public Float asFloat() {
        return first().asFloat();
    }

    public Double asDouble() {
        return first().asDouble();
    }

    public AJArray asArray() {
        return first().asArray();
    }

    public AJObject asObject() {
        return first().asObject();
    }

    public Bitmap asBitmap() {
        return first().asBitmap();
    }

    public Integer asColor() {
        return first().asColor();
    }

    public byte[] asBuffer() {
        return first().asBuffer();
    }

    private void loadJson(String json) {
        JsonParser parser;
        try {
            parser = JSON_FACTORY.createParser(json);
            parser.nextToken();

            if (parser.isClosed()) {
                return;
            }

            if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                loadJsonObj(this, parser);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Object loadJsonVal(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            AJObject child = new AJObject();
            loadJsonObj(child, parser);
            return child;
        } else if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            AJArray arr = new AJArray();
            loadJsonArr(arr, parser);
            return arr;
        } else if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
            throw new RuntimeException("Cannot be here");
        } else if (parser.getCurrentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {
            System.out.println("VALUE_EMBEDDED_OBJECT");
            return parser.getCurrentValue();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_FALSE) {
            return false;
        } else if (parser.getCurrentToken() == JsonToken.VALUE_TRUE) {
            return true;
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            return parser.getFloatValue();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            return parser.getIntValue();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return parser.getValueAsString();
        } else {
            return parser.getCurrentValue();
        }
    }

    private void loadJsonObj(AJObject obj, JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new RuntimeException("In loadJsonObj but parser is not on object start");
        }

        String currentFieldName = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                currentFieldName = parser.getCurrentName();
            } else {
                obj.set(currentFieldName, loadJsonVal(parser));
            }
        }
    }

    private void loadJsonArr(AJArray arr, JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new RuntimeException("In loadJsonArr but parser is not on array start");
        }

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            arr.append(loadJsonVal(parser));
        }
    }


    public String toJson() {

        try {
            StringWriter writer = new StringWriter();
            JsonGenerator generator = JSON_FACTORY.createGenerator(writer);

            writeJsonObj(this, generator);
            generator.close();

            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{}";
    }

    private void writeJsonObj(AJObject aj, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        for (AJValue value : aj.getValues()) {
            generator.writeFieldName(value.getKey());
            writeJsonValue(value.getValue(), generator);
        }
        generator.writeEndObject();
    }

    private void writeJsonValue(Object value, JsonGenerator generator) throws IOException {
        if (value == null) {
            generator.writeNull();
        } else if (value instanceof AJArray) {
            generator.writeStartArray();
            for (Object item : ((AJArray) value).getList()) {
                writeJsonValue(item, generator);
            }
            generator.writeEndArray();
        } else if (value instanceof AJObject) {
            writeJsonObj((AJObject) value, generator);
        } else {
            generator.writeObject(value);
        }
    }

    public List<AJValue> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static AJObject empty() {
        return new AJObject();
    }

    public static AJObject create() {
        return new AJObject();
    }
}
