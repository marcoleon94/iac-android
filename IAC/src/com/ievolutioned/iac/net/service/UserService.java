package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.ievolutioned.iac.entity.InquestEntity;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Provides the submit and save forms loaded on the system
 * <p/>
 * Created by Daniel on 23/04/2015.
 */
public class UserService extends ServiceBase {

    /**
     * Instantiates a UserService object with the current parameters
     *
     * @param deviceId   - The device id
     * @param adminToken - the admin token
     */
    public UserService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }

    /**
     * Creates a inquest response of the user
     *
     * @param json     - The response JSON, for example
     *                 <code>
     *                 {
     *                 inquest_id: //id especifico del formulario
     *                 iac_id: // usuario del que esta contestando el formulario
     *                 // user_response debe ser un json con la siguiente estructura y cuidar los caracteres de escape
     *                 user_response:
     *                 {
     *                 response: { key:"value", key:"value"...}
     *                 }
     *                 }
     *                 </code>
     * @param callback - the callback of service
     */
    public void create(final String json, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected UserResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new UserResponse("Params are null", null));
                        this.cancel(true);
                    }

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_CREATE, CONTROLLER_USER);

                    // Get response
                    NetResponse response = NetUtil.post(URL_USER, null, headers, json);

                    if (response != null) {
                        InquestEntity inquestEntity = new Gson().fromJson(response.result,
                                InquestEntity.class);
                        if (inquestEntity != null)
                            return new UserResponse(inquestEntity, response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new UserResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (UserResponse) response);
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
     * Updates a inquest response by its id
     *
     * @param id       - The id of the inquest
     * @param json     - The response JSON, for example <code> {// user_response debe ser un json con la siguiente estructura y cuidar los caracteres de escape
     *                 user_response:{
     *                 response: { key:"value", key:"value"...}
     *                 }}
     *                 </code>
     * @param callback - the callback of service
     */
    public void update(final int id, final String json, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected UserResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new UserResponse("Params are null", null));
                        this.cancel(true);
                    }

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_UPDATE, CONTROLLER_USER);

                    // Get response
                    NetResponse response = NetUtil.put(URL_USER + id, null, headers, json);

                    if (response != null) {
                        if (response.isBadStatus())
                            return new UserResponse(false, response.toString(), null);
                        else
                            return new UserResponse(true, "Status:" + response.status, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new UserResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (UserResponse) response);
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
    protected void hanldeResult(final ServiceHandler callback, final UserResponse response) {
        if (response == null)
            callback.onError(new UserResponse("Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * user service handler
     */
    public interface ServiceHandler {
        public void onSuccess(final UserResponse response);

        public void onError(final UserResponse response);

        public void onCancel();
    }

    /**
     * User response class. Manages the JSON elements of this response
     */
    public class UserResponse extends ResponseBase {

        /**
         * InquestEntity inquest
         */
        public InquestEntity inquest;

        /**
         * Is updated inquest
         */
        public boolean updated;


        /**
         * Instantiates a UserResponse object with the current parameters
         *
         * @param msg - The message
         * @param e   - The error
         */
        public UserResponse(String msg, Throwable e) {
            super(msg, e);
        }

        /**
         * Instantiates a UserResponse object with the current parameters
         *
         * @param i   - The inquest
         * @param msg - The message
         * @param e   - The error
         */
        public UserResponse(InquestEntity i, String msg, Throwable e) {
            super(msg, e);
            this.inquest = i;
        }

        /**
         * Instantiates a UserResponse object with the current parameters
         *
         * @param u   - The action
         * @param msg - The message
         * @param e   - The error
         */
        public UserResponse(boolean u, String msg, Throwable e) {
            super(msg, e);
            this.updated = u;
        }
    }


}
