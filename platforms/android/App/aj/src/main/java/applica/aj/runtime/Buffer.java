package applica.aj.runtime;

import java.util.ArrayList;
import java.util.List;

import applica.framework.android.utils.CollectionUtils;

/**
 * Created by bimbobruno on 19/03/16.
 */
public class Buffer {

    private static List<Buffer> buffers = new ArrayList<Buffer>();
    private static int lastBufferId = 1;

    private byte[] data;
    private int id;

    private Buffer(byte[] data) {
        id = lastBufferId++;
        this.data = data;

        synchronized (buffers) {
            buffers.add(this);
        }
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }


    public static int create(byte[] data) {
        Buffer buffer = new Buffer(data);
        return buffer.getId();
    }

    public static byte[] get(final int id) {
        synchronized (buffers) {
            Buffer buffer = CollectionUtils.first(buffers, new CollectionUtils.Predicate<Buffer>() {
                @Override
                public boolean evaluate(Buffer obj) {
                    return obj.getId() == id;
                }
            });

            if (buffer != null) {
                destroy(id);
                return buffer.getData();
            } else {
                return null;
            }
        }
    }

    public static void destroy(final int id) {
        synchronized (buffers) {
            Buffer buffer = CollectionUtils.first(buffers, new CollectionUtils.Predicate<Buffer>() {
                @Override
                public boolean evaluate(Buffer obj) {
                    return obj.getId() == id;
                }
            });

            if (buffer != null) {
                buffers.remove(buffer);
            }
        }
    }
}
