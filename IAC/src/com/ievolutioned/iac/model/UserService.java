package com.ievolutioned.iac.model;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.entity.InquestEntity;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FormatUtil;

import java.util.Date;

/**
 * Provides the submit and save forms loaded on the system
 * <p/>
 * Created by Daniel on 23/04/2015.
 */
public class UserService {

    private static final String URL = "https://iacgroup.herokuapp.com/api/user_responses/";

    private static final String ACTION_CREATE = "create";

    private static final String ACTION_UPDATE = "update";

    private static final String CONTROLLER = "user_responses";

    /**
     * Required as the unique ID of device for secret
     */
    private String deviceId = null;

    /**
     * Required admin token to access on system
     */
    private String adminToken = null;

    public UserService(String deviceId, String adminToken) {
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

    /**
     * AsyncTask task for service
     */
    private AsyncTask<Void, Void, UserResponse> task;


    public void create(final int inquestId, final String iacId, final String json, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, UserResponse>() {
            @Override
            protected UserResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new UserResponse("Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();

                    //Get headers
                    HttpHeader headers = getUserHeaders(ACTION_CREATE);

                    // Get response
                    String response = NetUtil.post(URL, null, headers, json);

                    if (response != null) {
                        InquestEntity inquestEntity = new Gson().fromJson(response,
                                InquestEntity.class);
                        if (inquestEntity != null)
                            return new UserResponse(inquestEntity, response, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new UserResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(UserResponse response) {
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

    public void update(final String json, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, UserResponse>() {
            @Override
            protected UserResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new UserResponse("Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();

                    //Get headers
                    HttpHeader headers = new HttpHeader();

                    // Get response
                    String response = NetUtil.post(URL, params, headers, json);

                    if (response != null) {
                        InquestEntity inquestEntity = new Gson().fromJson(response,
                                InquestEntity.class);
                        if (inquestEntity != null)
                            return new UserResponse(inquestEntity, response, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new UserResponse(e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(UserResponse response) {
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


    private HttpHeader getUserHeaders(String action) {
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

    public class UserResponse extends ResponseBase {

        public InquestEntity inquest;

        public UserResponse(String msg, Throwable e) {
            this.msg = msg;
            this.e = e;
        }

        public UserResponse(InquestEntity i, String msg, Throwable e) {
            this.inquest = i;
            this.msg = msg;
            this.e = e;
        }
    }


}
