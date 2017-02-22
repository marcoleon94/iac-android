package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Created by Daniel on 21/02/2017.
 */

public class CoursesService extends ServiceBase {

    protected final String INFO_ATTENDEE = "info_attendee";

    /**
     * Constructor
     *
     * @param deviceId
     * @param adminToken
     */
    public CoursesService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }

    /**
     * Gets all the forms
     *
     * @param callback - ServiceHandler callback
     */
    public void getActiveCourses(final String token, final String id, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected CoursesResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new CoursesResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (token != null)
                        params.add("admin-token", token);
                    if(id != null)
                        params.add("iac_id", id);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_INDEX, CONTROLLER_COURSES);

                    // Get response
                    NetResponse response = NetUtil.get(URL_COURSES_ACTIVE, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new CoursesResponse(json.getAsJsonArray(),
                                    response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new CoursesResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (CoursesResponse) response);
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
    protected void hanldeResult(final ServiceHandler callback, final CoursesResponse response) {
        if (response == null)
            callback.onError(new CoursesResponse(null, "Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * form service handler
     */
    public interface ServiceHandler {
        public void onSuccess(final CoursesResponse response);

        public void onError(final CoursesResponse response);

        public void onCancel();
    }


    /**
     * Course response class. Manages the JSON elements of this response
     */
    public class CoursesResponse extends ResponseBase {
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
        public CoursesResponse(JsonElement json, String msg, Throwable e) {
            super(msg, e);
            this.json = json;
        }
    }


}
