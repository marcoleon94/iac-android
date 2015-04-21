package com.ievolutioned.iac.net;

import android.util.Log;

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
 *
 * Created by Daniel on 13/04/2015.
 */
public class NetUtil {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    /**
     * GET method, gets a simple response
     *
     * @param url - The full URL of the service
     * @return the server response
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        return get(url, null, null);
    }

    /**
     * GET method, gets a simple response
     *
     * @param url
     *            - The full URL of the service
     * @param params
     *            - A set of GET parameters
     * @param headers
     *            - A set of headers
     * @return the server response.
     * @throws Exception
     */
    public static String get(String url, HttpGetParam params, HttpHeader headers)
            throws Exception {
        HttpURLConnection connection = null;
        if (params != null)
            url += "?" + params.toString();

        try {
            URL contentUrl = new URL(url);
            connection = (HttpURLConnection) contentUrl.openConnection();
            connection.setRequestMethod(METHOD_GET);
            connection.setRequestProperty("Accept", "*/*");

            if (headers != null)
                setHeaderToHttpURLConnection(connection, headers);

            connection.connect();
            if (connection.getResponseCode() >= 400) {
                throw new IOException(connection.getResponseCode() + ":"
                        + readStream(connection.getErrorStream()));
            }
            return readStream(connection.getInputStream());
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    /**
     * POST method, gets a post request
     *
     * @param url
     *            the URL
     * @param params
     *            a set of HttpGetParam
     * @param headers
     *            a set of HttpHeader
     * @param json
     *            a JSON string
     * @return the response
     * @throws Exception
     */
    public static String post(String url, HttpGetParam params,
                              HttpHeader headers, String json) throws Exception {
        HttpURLConnection connection = null;
        if (params != null)
            url += "?" + params.toString();

        try {
            URL contentUrl = new URL(url);
            connection = (HttpURLConnection) contentUrl.openConnection();
            connection.setRequestMethod(METHOD_POST);

            if (headers != null)
                setHeaderToHttpURLConnection(connection, headers);

            if (json != null) {
                String postLength = String.valueOf(json.getBytes("UTF-8").length);
                connection.setRequestProperty("Content-Length", postLength);
                connection.setRequestProperty("Content-Type",
                        "application/json; charset=UTF-8");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();
                os.close();
            }
            else
                connection.connect();

            if (connection.getResponseCode() >= 400) {
                throw new IOException(connection.getResponseCode() + ":"
                        + readStream(connection.getErrorStream()));
            }
            return readStream(connection.getInputStream());
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    /**
     * Reads the input stream.
     *
     * @param inputStream
     *            a InputStream
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
        Log.d(NetUtil.class.getName(), sb.toString());
        return sb.toString();
    }

    /**
     * Sets headers to a HttpURLConnection object
     *
     * @param conn
     *            the HttpURLConnection
     * @param headers
     *            a HttpHeader
     */
    private static void setHeaderToHttpURLConnection(HttpURLConnection conn,
                                                     HttpHeader headers) {
        Map<String, String> hs = headers.getHeaders();
        for (String key : hs.keySet()) {
            conn.addRequestProperty(key, hs.get(key));
        }
    }

}
