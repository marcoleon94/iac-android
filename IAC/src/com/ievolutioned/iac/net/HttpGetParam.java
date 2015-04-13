package com.ievolutioned.iac.net;

import android.util.Log;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 13/04/2015.
 */
public class HttpGetParam {

    /**
     * UTF-8 Encode
     */
    private static final String UTF8 = "UTF-8";

    /**
     * Parameters
     */
    private Map<String, String> params = new HashMap<>();

    /**
     * Adds a new key-value pair to set of parameters
     *
     * @param param
     * @param value
     */
    public void add(String param, String value) {
        params.put(param, value);
    }

    /*
     * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> pair : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            try {
                result.append(URLEncoder.encode(pair.getKey(), UTF8));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), UTF8));
            } catch (Exception e) {
                Log.e(HttpGetParam.class.getName(), e.getMessage(), e);
                return null;
            }
        }
        return result.toString();
    }
}
