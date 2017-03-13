package applica.aj.runtime.websocket;

import android.content.Context;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import applica.aj.AJObject;
import applica.aj.ManualSemaphore;
import applica.aj.Semaphore;
import applica.aj.runtime.AJRuntime;
import applica.aj.runtime.Buffer;
import applica.aj.runtime.Plugin;
import applica.framework.android.utils.CollectionUtils;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by bimbobruno on 26/04/16.
 */
public class AJWebSocketRuntime extends AJRuntime {

    private boolean connecting;
    private Socket io;

    private List<ManualSemaphore> semaphores = new ArrayList<>();

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    public AJWebSocketRuntime(Context context, final URI uri) {
        super(context);

        IO.Options options = new IO.Options();
        options.reconnection = false;

        io = IO.socket(uri, options);

        io.on("trigger", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String type = (String) args[0];
                JSONObject json = (JSONObject) args[1];
                AJObject state = json != null ? AJObject.fromJson(json.toString()) : new AJObject();
                Ack ack = (Ack) args[2];

                try {
                    trigger(type, state);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                ack.call();
            }
        });

        io.on("exec", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String plugin = (String) args[0];
                String fn = (String) args[1];
                JSONObject json = (JSONObject) args[2];
                AJObject data = json != null ? AJObject.fromJson(json.toString()) : new AJObject();
                final Ack ack = (Ack) args[3];

                try {
                    exec(plugin, fn, data, new Plugin.Callback() {
                        @Override
                        public void onSuccess(AJObject data) {
                            ack.call(false, data);
                        }

                        @Override
                        public void onError(AJObject data) {
                            ack.call(true, data);
                        }
                    });
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                ack.call();
            }
        });

        io.on("freeSemaphore", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int id = (int) args[0];
                freeSemaphore(id);
            }
        });

        io.on("device", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

                AJObject device = AJObject.create()
                        .set("scale", displayMetrics.density)
                        .set("height", displayMetrics.heightPixels)
                        .set("width", displayMetrics.widthPixels);

                Ack ack = (Ack) args[0];
                ack.call(device.toJson());
            }
        });

        io.on("createBuffer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[1];

                String base64 = (String) args[0];
                if (StringUtils.isEmpty(base64)) {
                    ack.call(true, "Bad data");
                    return;
                }

                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                if (bytes == null) {
                    ack.call(true, "Bad data");
                    return;
                }

                ack.call(false, Buffer.create(bytes));
            }
        });

        io.on("readBuffer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[1];

                Integer id = (Integer) args[0];
                if (id == null) {
                    ack.call(true, "Bad id");
                    return;
                }

                byte[] bytes = Buffer.get(id);
                if (bytes == null) {
                    ack.call(true, "Buffer not found: " + id.toString());
                    return;
                }

                String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                ack.call(false, base64);
            }
        });

        io.on("deleteBuffer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Ack ack = (Ack) args[1];

                Integer id = (Integer) args[0];
                if (id == null) {
                    ack.call(true, "Bad id");
                    return;
                }

                Buffer.destroy(id);

                ack.call(false);
            }
        });

        io.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                connecting = false;
                Log.i("AJ", "Connected to websocket server");
            }
        });

        io.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("AJ", "Connecting to websocket server on " + uri.toString());
                connecting = true;
            }
        });

        io.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                connecting = false;
                Log.w("AJ", "Error connecting to websocket server");
            }
        });

        io.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                connecting = false;
                Log.w("AJ", "Timeout connecting to websocket server");
            }
        });

        connecting = true;
        io.connect();
    }

    private void freeSemaphore(final int id) {
        ManualSemaphore semaphore = CollectionUtils.first(semaphores, new CollectionUtils.Predicate<ManualSemaphore>() {
            @Override
            public boolean evaluate(ManualSemaphore obj) {
                return obj.getId() == id;
            }
        });

        if (semaphore != null) {
            semaphore.green();
            semaphores.remove(semaphore);
        }
    }

    @Override
    public Semaphore run(String action, AJObject data) {
        data = data != null ? data : new AJObject();

        String json = data.toJson();

        while (connecting) {
            try {
                Log.i("AJ", "Waiting for connection with web socket server...");
                Thread.sleep(250);
            } catch (InterruptedException e) {}
        }

        Log.i("AJ", String.format("Calling %s with data: %s", action, json));

        final ManualSemaphore semaphore = new ManualSemaphore();
        semaphores.add(semaphore);

        io.emit("run", action, json, new Ack() {
            @Override
            public void call(Object... args) {
                freeSemaphore(semaphore.getId());
            }
        });

        return semaphore;

    }
}
