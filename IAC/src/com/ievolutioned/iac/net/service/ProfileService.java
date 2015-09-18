package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Created by Daniel on 17/09/2015.
 */
public class ProfileService extends ServiceBase {

    public ProfileService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }


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
                    //NetResponse response = NetUtil.post(URL_PROFILE + ACTION_GET_INFO, null, headers, null);
                    NetResponse response = NetUtil.get(URL_PROFILE + ACTION_GET_INFO, getParam, headers);

                    if (response != null) {
                        /*
                        InquestEntity inquestEntity = new Gson().fromJson(response.result,
                                InquestEntity.class);
                        if (inquestEntity != null)
                            return new UserResponse(inquestEntity, response.result, null);
                            */
                        return new ProfileResponse(response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new ProfileResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (ProfileResponse) response);
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
    protected void hanldeResult(final ProfileServiceHandler callback, ProfileResponse response) {
        if (response == null)
            callback.onError(new ProfileResponse("Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    public interface ProfileServiceHandler {
        void onSuccess(final ProfileResponse response);

        void onError(final ProfileResponse response);

        void onCancel();
    }

    public class ProfileResponse extends ResponseBase {

        public ProfileResponse(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
