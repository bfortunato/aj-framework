package applica.aj.runtime.javascriptcore;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.liquidplayer.webkit.javascriptcore.JSArray;
import org.liquidplayer.webkit.javascriptcore.JSArrayBuffer;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSDataView;
import org.liquidplayer.webkit.javascriptcore.JSFunction;
import org.liquidplayer.webkit.javascriptcore.JSInt8Array;
import org.liquidplayer.webkit.javascriptcore.JSObject;
import org.liquidplayer.webkit.javascriptcore.JSUint8Array;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import applica.aj.runtime.Buffer;

/**
 * Created by bimbobruno on 14/03/16.
 */
public class HttpClientImpl extends JSObject implements HttpClient {

    private Context context;
    private JSContext jsContext;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";

    public HttpClientImpl(Context context, JSContext jsContext) {
        super(jsContext, HttpClient.class);
        this.context = context;
        this.jsContext = jsContext;
    }

    @Override
    public void request(final String urlString, final String method, final JSValue data, final JSValue headers, final Boolean raw, final JSFunction cb) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String queryString = buildQueryString(data);

                    Log.i("AJ", String.format("HTTP url %s, data: %s", urlString, queryString));

                    String finalMethod = method.toUpperCase();
                    String finalUrlString = urlString;

                    if (!(METHOD_POST.equals(method) || METHOD_PUT.equals(method))) {
                        String separator = urlString.contains("?") ? "&" : "?";
                        finalUrlString = urlString.concat(separator).concat(queryString);
                    }

                    URL url = new URL(finalUrlString);
                    connection = ((HttpURLConnection) url.openConnection());
                    connection.setRequestMethod(finalMethod);
                    connection.setDoInput(true);

                    if (METHOD_GET.equals(finalMethod)) {
                        //do nothing
                    } else if (METHOD_POST.equals(finalMethod)) {
                        connection.setDoOutput(true);
                    }

                    addHeaders(connection, headers);

                    if (METHOD_POST.equals(finalMethod) || METHOD_PUT.equals(finalMethod)) {
                        IOUtils.write(buildQueryString(data), connection.getOutputStream());
                    }

                    Object output;
                    if (raw != null && raw) {
                        byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
                        output = Buffer.create(bytes);
                    } else {
                        output = IOUtils.toString(connection.getInputStream());
                    }

                    cb.call(null, new JSValue[]{new JSValue(jsContext, false), new JSValue(jsContext, output)});
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    cb.call(null, new JSValue[]{new JSValue(jsContext, true), new JSValue(jsContext, "error.bad.url")});
                } catch (IOException e) {
                    e.printStackTrace();
                    cb.call(null, new JSValue[]{new JSValue(jsContext, true), new JSValue(jsContext, "error.io")});
                } finally {
                    if (connection != null) { connection.disconnect(); }
                }
            }
        });
    }

    private void addHeaders(HttpURLConnection connection, JSValue headers) {
        JSObject obj = headers.toObject();

        for (String property : obj.propertyNames()) {
            connection.setRequestProperty(property, obj.property(property).toString());
        }
    }

    private String buildQueryString(JSValue js) {
        JSObject obj = js.toObject();
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (String property : obj.propertyNames()) {
            if (!first) {
                query.append("&");
            }
            try {
                query.append(property)
                        .append("=")
                        .append(URLEncoder.encode(obj.property(property).toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            first = false;
        }

        return query.toString();
    }

}
