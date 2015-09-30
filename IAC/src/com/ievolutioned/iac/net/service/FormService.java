package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Provides the forms loaded on the system
 * <p/>
 * Created by Daniel on 22/04/2015.
 */
public class FormService extends ServiceBase {

    /**
     * JSON array key for all inquests
     */
    private static final String JSON_INQUESTS = "inquests";

    /**
     * JSON key for single inquest
     */
    private static final String JSON_INQUEST = "inquest";

    /**
     * Instantiates a Form service object with current parameters
     *
     * @param deviceId   - the UUID of the device
     * @param adminToken - the user admin token
     */
    public FormService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }

    /**
     * Gets all the forms
     *
     * @param callback - ServiceHandler callback
     */
    public void getForms(final String token, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected FormResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new FormResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (token != null)
                        params.add("admin-token", token);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_INDEX, CONTROLLER_INQUESTS);

                    // Get response
                    NetResponse response = NetUtil.get(URL_FORM, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new FormResponse(json.getAsJsonObject().get(JSON_INQUESTS),
                                    response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new FormResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (FormResponse) response);
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
     * Gets a form by its id
     *
     * @param idForm   - the form id
     * @param callback - ServiceHandler callback
     */
    public void getFormById(final int idForm, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected FormResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new FormResponse(null, "Params are null", null));
                        this.cancel(true);
                    }

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_SHOW, CONTROLLER_INQUESTS);

                    // Get response
                    NetResponse response = NetUtil.get(URL_FORM + idForm, null, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new FormResponse(json.getAsJsonObject().get(JSON_INQUEST),
                                    response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new FormResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (FormResponse) response);
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
    protected void hanldeResult(final ServiceHandler callback, final FormResponse response) {
        if (response == null)
            callback.onError(new FormResponse(null, "Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * form service handler
     */
    public interface ServiceHandler {
        public void onSuccess(final FormResponse response);

        public void onError(final FormResponse response);

        public void onCancel();
    }


    /**
     * Form response class. Manages the JSON elements of this response
     */
    public class FormResponse extends ResponseBase {
        /**
         * Contains the complete JSON element on response
         */
        public JsonElement json;

        /**
         * Instantiates a FormResponse object
         *
         * @param json - the JsonElement
         * @param msg  - the message
         * @param e    - the error
         */
        public FormResponse(JsonElement json, String msg, Throwable e) {
            super(msg, e);
            this.json = json;
        }
    }
}
