package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Service for Dining Room assistance
 * <p>
 * Created by Daniel on 12/04/2017.
 */

public class DiningService extends ServiceBase {

    public static final String ALL_INFO_DINING_ROOM = "all_info_dining_room";
    public static final String COMENSAL = "comensal";
    public static final String MSG = "msg";
    public static final String ERROR_CODE = "error_code";

    /**
     * Constructor
     *
     * @param deviceId
     * @param adminToken
     */
    public DiningService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }


    public void getComensals(final String siteId, final DiningService.ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected DiningResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new DiningResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (adminToken != null)
                        params.add("admin-token", adminToken);
                    if (siteId != null)
                        params.add("site_id", siteId);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_GET_COMMENSALS, CONTROLLER_DINING_ROOM);

                    // Get response
                    NetResponse response = NetUtil.get(URL_GET_COMMENSALS, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new DiningResponse(
                                    json.getAsJsonObject().get(ALL_INFO_DINING_ROOM).getAsJsonArray(),
                                    response.result,
                                    null);
                    }
                    return null;
                } catch (Exception e) {
                    return new DiningResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (DiningResponse) response);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        };
        task.execute();
    }

    /**
     * @param token
     * @param iacId
     * @param callback - ServiceHandler callback
     */
    public void getValidateDiningRoom(final String token, final String iacId, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected DiningResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new DiningResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (token != null)
                        params.add("admin-token", token);
                    if (iacId != null)
                        params.add("iac_id", iacId);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_DINING_VALIDATE, CONTROLLER_DINING_ROOM);

                    // Get response
                    NetResponse response = NetUtil.get(URL_DINING_VALIDATE, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new DiningResponse(json.getAsJsonObject(),
                                    response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new DiningResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (DiningResponse) response);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        };
        task.execute();
    }

    /**
     * Handles the result on callback
     *
     * @param callback - the callback of service
     * @param response - the response of service
     */
    protected void hanldeResult(final ServiceHandler callback, final DiningResponse response) {
        if (response == null)
            callback.onError(new DiningResponse(null, "Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * form service handler
     */
    public interface ServiceHandler {
        void onSuccess(final DiningResponse response);

        void onError(final DiningResponse response);

        void onCancel();
    }

    public interface ErrorCodes {
        int NO_ERROR = 0;
        int NO_USER = 1;
        int NO_ENTER = 2;
    }


    /**
     * Course response class. Manages the JSON elements of this response
     */
    public class DiningResponse extends ResponseBase {
        /**
         * Contains the complete JSON element on response
         */
        public JsonElement json;

        /**
         * Instantiates a Dining object
         *
         * @param json - the JsonElement
         * @param msg  - the message
         * @param e    - the error
         */
        public DiningResponse(JsonElement json, String msg, Throwable e) {
            super(msg, e);
            this.json = json;
        }
    }


}
