package com.ievolutioned.iac.model;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FormatUtil;

import java.util.Date;

/**
 * Provides the forms loaded on the system
 * <p/>
 * Created by Daniel on 22/04/2015.
 */
public class FormService {
    /**
     * URL for form or inquests on the server
     */
    private static final String URL_FORM = "https://iacgroup.herokuapp.com/api/inquests/";

    /**
     * JSON array key for all inquests
     */
    private static final String JSON_INQUESTS = "inquests";

    /**
     * JSON key for single inquest
     */
    private static final String JSON_INQUEST = "inquest";

    /**
     * Controller constant
     */
    private static final String CONTROLLER = "inquests";

    /**
     * Action for get all inquest
     */
    private static final String ACTION_INDEX = "index";
    /**
     * Action for get a single inquest
     */
    private static final String ACTION_SHOW = "show";

    /**
     * AsyncTask task for service
     */
    private AsyncTask<Void, Void, FormResponse> task;

    /**
     * Required as the unique ID of device for secret
     */
    private String deviceId = null;

    /**
     * Required admin token to access on system
     */
    private String adminToken = null;

    /**
     * Instantiates a Form service object with current parameters
     *
     * @param deviceId   - the UUID of the device
     * @param adminToken - the user admin token
     */
    public FormService(String deviceId, String adminToken) {
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

    /**
     * Gets all the forms
     *
     * @param callback - ServiceHandler callback
     */
    public void getForms(final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, FormResponse>() {
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

                    //Get headers
                    HttpHeader headers = getFormHeaders(ACTION_INDEX);

                    // Get response
                    String response = NetUtil.get(URL_FORM, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response);
                        if (!json.isJsonNull())
                            return new FormResponse(json.getAsJsonObject().get(JSON_INQUESTS), response, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new FormResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(FormResponse response) {
                hanldeResult(callback, response);
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
        task = new AsyncTask<Void, Void, FormResponse>() {
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
                    HttpHeader headers = getFormHeaders(ACTION_SHOW);
                    
                    // Get response
                    String response = NetUtil.get(URL_FORM + idForm, null, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response);
                        if (!json.isJsonNull())
                            return new FormResponse(json.getAsJsonObject().get(JSON_INQUEST), response, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new FormResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(FormResponse response) {
                hanldeResult(callback, response);
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
     * Gets the form headers for request
     *
     * @param action - the action to perform
     * @return a HttpHeader
     */
    private HttpHeader getFormHeaders(String action) {
        HttpHeader headers = new HttpHeader();

        String xVersion = AppConfig.API_VERSION;
        String xToken = AppConfig.API_TOKEN;
        String xAdminToken = adminToken;

        String reversedID = FormatUtil.reverseString(this.deviceId);
        String xDate = FormatUtil.dateDefaultFormat(new Date());
        String xDevice = this.deviceId;

        //String preSecret = #{X-token}-#{controller}-#{action}-#{X-version}-#{device_id.reverse}-#{X-admin-token}-#{X-device-date}
        String preSecret = String.format("%s-%s-%s-%s-%s-%s-%s", xToken, CONTROLLER, action,
                xVersion, reversedID, xAdminToken, xDate);
        String xSecret = FormatUtil.md5(preSecret);

        headers.add("X-version", xVersion);
        headers.add("X-token", xToken);
        headers.add("X-admin-token", xAdminToken);
        headers.add("X-device-id", xDevice);
        headers.add("X-device-date", xDate);
        headers.add("X-secret", xSecret);


        return headers;
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
            this.json = json;
            this.msg = msg;
            this.e = e;
        }
    }
}
