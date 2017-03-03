package com.ievolutioned.iac.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ievolutioned.iac.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Network connection manager class. Performs a set of operation about networking
 * <p/>
 * Created by Daniel on 13/04/2015.
 */
public class NetUtil {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";

    private static final String CONTENT_TYPE_JSON = "application/json;";
    private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded;";

    /**
     * GET method, gets a simple response
     *
     * @param url     - The full URL of the service
     * @param params  - A set of GET parameters
     * @param headers - A set of headers
     * @return the server response.
     * @throws Exception public static String get(String file, HttpGetParam params, HttpHeader headers)
     */
    public static NetResponse get(String url, HttpGetParam params, HttpHeader headers)
            throws Exception {
        return getContent(url, METHOD_GET, params, headers, CONTENT_TYPE_JSON, null);
    }

    /**
     * POST method, gets a post request
     *
     * @param url     - The full URL of the service
     * @param params  - A set of GET parameters
     * @param headers - A set of headers
     * @param json    - A JSON string
     * @return the response
     * @throws Exception public static String post(String file, HttpGetParam params,
     */

    public static NetResponse post(String url, HttpGetParam params,
                                   HttpHeader headers, String json) throws Exception {
        return getContent(url, METHOD_POST, params, headers, CONTENT_TYPE_JSON, json);
    }

    /**
     * PUT method, updates a request
     *
     * @param url     - The full URL of the service
     * @param params  - A set of GET parameters
     * @param headers - A set of headers
     * @param json    - A JSON string
     * @return the response
     * @throws Exception
     */
    public static NetResponse put(String url, HttpGetParam params,
                                  HttpHeader headers, String json) throws Exception {
        return getContent(url, METHOD_PUT, params, headers, CONTENT_TYPE_JSON, json);
    }

    /**
     * PATCH method, patches a request
     *
     * @param url     - The full URL of the service
     * @param params  - A set of GET parameters
     * @param headers - A set of headers
     * @return the response
     * @throws Exception
     */
    public static NetResponse patch(String url, HttpGetParam params,
                                    HttpHeader headers) throws Exception {
        return getContent(url, METHOD_PATCH, params, headers, CONTENT_TYPE_FORM_URLENCODED, null);
    }

    /**
     * Gets the content of a http request
     *
     * @param url     - The full URL of the service
     * @param method  - The method POST, GET, PUT, and so on.
     * @param params  - A set of GET parameters
     * @param headers - A set of headers
     * @param json    - A JSON string
     * @return the response
     * @throws Exception
     */
    private static NetResponse getContent(String url, final String method, final HttpGetParam params,
                                          final HttpHeader headers, final String contentType, final String json) throws Exception {
        HttpURLConnection connection = null;
        if (params != null)
            url += "?" + params.toString();

        try {
            URL contentUrl = new URL(url);
            connection = (HttpURLConnection) contentUrl.openConnection();
            connection.setRequestMethod(method);

            if (headers != null)
                setHeaderToHttpURLConnection(connection, headers);

            if (json != null) {
                String postLength = String.valueOf(json.getBytes("UTF-8").length);
                connection.setRequestProperty("Content-Length", postLength);
                connection.setRequestProperty("Content-Type",
                        contentType + " charset=UTF-8");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();
                os.close();
            } else
                connection.connect();

            int status = connection.getResponseCode();
            if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new IOException(status + ":" + readStream(connection.getErrorStream()));
            }
            return new NetResponse(readStream(connection.getInputStream()), status);
        } catch (Exception e) {
            LogUtil.e(NetUtil.class.getName(), e.getMessage(), e);
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();

        }
    }

    /**
     * Reads the input stream.
     *
     * @param inputStream a InputStream
     * @return a String that contains the <b>inputStream</b>
     * @throws IOException
     */
    private static String readStream(InputStream inputStream)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        if (reader == null)
            return null;
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Sets headers to a HttpURLConnection object
     *
     * @param conn    the HttpURLConnection
     * @param headers a HttpHeader
     */
    private static void setHeaderToHttpURLConnection(HttpURLConnection conn,
                                                     HttpHeader headers) {
        Map<String, String> hs = headers.getHeaders();
        for (String key : hs.keySet()) {
            conn.addRequestProperty(key, hs.get(key));
        }
    }

    public static boolean hasNetworkConnection(Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.
                CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

}
