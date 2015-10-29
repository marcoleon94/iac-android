package com.ievolutioned.iac.net;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP header utility class
 * Created by Daniel on 13/04/2015.
 */
public class HttpHeader {
    /**
     * A set of headers of HTTP
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Adds a new header as key-value pair
     *
     * @param header
     * @param value
     */
    public void add(String header, String value) {
        headers.put(header, value);
    }

    /**
     * Gets the headers
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
