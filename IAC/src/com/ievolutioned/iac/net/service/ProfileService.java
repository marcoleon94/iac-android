package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * ProfileService class, allows the control of a set operation about user profile
 * <p/>
 * Created by Daniel on 17/09/2015.
 */
public class ProfileService extends ServiceBase {

    /**
     * Instantiates a profile service with the current parameters
     *
     * @param deviceId
     * @param adminToken
     */
    public ProfileService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }

    /**
     * Gets the profile information for a user about the adminToken id
     *
     * @param callback - ProfileServiceHandler
     */
    public void getProfileInfo(final ProfileServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected ResponseBase doInBackground(Void... params) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new ProfileResponse("Params are null", null));
                        this.cancel(true);
                    }

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_GET_INFO, CONTROLLER_PROFILE);
                    HttpGetParam getParam = new HttpGetParam();
                    getParam.add("admin-token", adminToken);

                    // Get response
                    NetResponse response = NetUtil.get(URL_PROFILE + ACTION_GET_INFO, getParam, headers);

                    if (response != null) {
                        Gson gson = new Gson();
                        JsonElement adminInfo = gson.fromJson(response.result, JsonElement.class).
                                getAsJsonObject();
                        if (adminInfo.isJsonNull())
                            return null;
                        ProfileEntity profileEntity = gson.fromJson(adminInfo.getAsJsonObject(),
                                ProfileEntity.class);
                        if (profileEntity != null)
                            return new ProfileResponse(profileEntity, response.result, null);
                        return new ProfileResponse(response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new ProfileResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                handleResult(callback, (ProfileResponse) response);
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
     * Updates the info for the profile, the info JSON must have the following structure
     * {admin:{
     * field:"value"
     * }}
     *
     * @param info     - String JSON
     * @param callback ProfileServiceHandler callback
     */
    public void updateInfo(final String info, final ProfileServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected ResponseBase doInBackground(Void... params) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new ProfileResponse("Params are null", null));
                        this.cancel(true);
                    }

                    HttpGetParam getParam = new HttpGetParam();
                    getParam.add("admin-token", adminToken);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_UPDATE_ADMIN, CONTROLLER_PROFILE);

                    // Get response
                    NetResponse response = NetUtil.put(URL_PROFILE + ACTION_UPDATE_ADMIN, getParam,
                            headers, info);

                    if (response != null) {
                        if (response.isBadStatus())
                            return null;
                        return new ProfileResponse(true, response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new ProfileResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase responseBase) {
                super.onPostExecute(responseBase);
                handleResult(callback, (ProfileResponse) responseBase);
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
    protected void handleResult(final ProfileServiceHandler callback, ProfileResponse response) {
        if ((response == null || response.profile == null) && !response.success)
            callback.onError(new ProfileResponse("Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * ProfileServiceHandler interface
     */
    public interface ProfileServiceHandler {
        void onSuccess(final ProfileResponse response);

        void onError(final ProfileResponse response);

        void onCancel();
    }

    /**
     * ProfileResponse class as the service response
     */
    public class ProfileResponse extends ResponseBase {

        /**
         * Successful transaction
         */
        public boolean success = false;
        /**
         * ProfileEntity user profile
         */
        public ProfileEntity profile = null;

        public ProfileResponse(String msg, Throwable e) {
            super(msg, e);
        }

        public ProfileResponse(ProfileEntity profile, String msg, Throwable e) {
            super(msg, e);
            this.profile = profile;
        }

        public ProfileResponse(boolean success, String msg, Throwable e) {
            super(msg, e);
            this.success = success;
        }
    }

}
