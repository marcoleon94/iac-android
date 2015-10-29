package com.ievolutioned.iac.net;

import java.net.HttpURLConnection;

/**
 * Network result handler class
 * <p/>
 * Created by Daniel on 28/04/2015.
 */
public class NetResponse {
    /**
     * The response in general
     */
    public String result;
    /**
     * The code status, {@link java.net.HttpURLConnection} status, <code>0</code> by default
     */
    public int status = 0;

    /**
     * Instantiates a NetResponse object
     *
     * @param result - The response in general
     * @param status - The code status
     */
    public NetResponse(String result, int status) {
        this.result = result;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Result: " + this.result + ", status: " + this.status;
    }

    /**
     * Verifies if it is a bad status
     *
     * @return true if it is bad, false otherwise
     */
    public boolean isBadStatus() {
        return status == 0 || status >= HttpURLConnection.HTTP_BAD_REQUEST;
    }
}
