package com.ievolutioned.iac.model;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.ievolutioned.iac.entity.UserEntity;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FormatUtil;

import java.util.Date;

/**
 * Manages the log in / out services for the user on the system
 * Created by Daniel on 20/03/2015.
 */
public class LoginService {

    private static final String URL_LOGIN = "https://iacgroup.herokuapp.com/api/services/access";

    private AsyncTask<Void, Void, LoginResponse> task;

    private String deviceId = null;

    public LoginService(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Logs the user in the system
     *
     * @param id
     * @param pass
     * @param callback a LoginHandler callback handler
     */
    public void logIn(final String id, final String pass, final LoginHandler callback) {
        task = new AsyncTask<Void, Void, LoginResponse>() {
            @Override
            protected LoginResponse doInBackground(Void... voids) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null) {
                        callback.onError(new LoginResponse(false, null, "Device id is null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    params.add("iac_id", id);
                    params.add("password", pass);

                    HttpHeader headers = getLoginHeaders();

                    // Get response
                    String response = NetUtil.get(URL_LOGIN, params, headers);
                    if(response == null)
                        return new LoginResponse(false,null,"No response", null);

                    //Parse response
                    Gson g = new Gson();
                    UserEntity user = g.fromJson(response, UserEntity.class);
                    return new LoginResponse(true, user, response, null);
                } catch (Exception e) {
                    return new LoginResponse(false, null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(LoginResponse response) {
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
     * Gets the login headers by default
     * @return a HttpHeader
     */
    private HttpHeader getLoginHeaders() {
        HttpHeader headers = new HttpHeader();

        String xVersion = AppConfig.API_VERSION;
        String xToken = AppConfig.API_TOKEN; // d4e9a9414181819f3a47ff1ddd9b2ca3
        String xAdminToken = "nosession";
        String controller = "services";
        String action = "access";

        String reversedID = FormatUtil.reverseString(this.deviceId);
        String xDate = FormatUtil.dateDefaultFormat(new Date());
        String xDevice = this.deviceId;

        //String preSecret = #{X-token}-#{controller}-#{action}-#{X-version}-#{device_id.reverse}-#{X-admin-token}-#{X-device-date}
        String preSecret = String.format("%s-%s-%s-%s-%s-%s-%s", xToken, controller, action,
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
     * Logs the user out of system
     *
     * @param token
     * @param callback a LoginHandler callback handler
     */
    public void logOut(final String token, final LoginHandler callback) {
        task = new AsyncTask<Void, Void, LoginResponse>() {
            @Override
            protected LoginResponse doInBackground(Void... voids) {
                if (isCancelled())
                    return null;
                try {
                    return new LoginResponse(false, null, null, null);
                } catch (Exception e) {
                    return new LoginResponse(true, null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(LoginResponse response) {
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
     * Cancels the request
     *
     * @param callback a LoginHandler callback handler
     */
    public void cancel(final LoginHandler callback) {
        if (task != null)
            task.cancel(true);
        callback.onCancel();
    }

    /**
     * Handles the result
     *
     * @param callback a LoginHandler callback handler
     * @param response the current LoginResponse
     */
    protected void hanldeResult(final LoginHandler callback, final LoginResponse response) {
        if (response == null)
            callback.onError(new LoginResponse(false, null, "Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * Log in/out handler
     */
    public interface LoginHandler {
        public void onSuccess(final LoginResponse response);

        public void onError(final LoginResponse response);

        public void onCancel();
    }

    /**
     * Login response class, manages the response from service
     */
    public class LoginResponse {
        public String msg;
        public Throwable e;
        public boolean logged;
        public UserEntity user;

        public LoginResponse(final boolean logged, final UserEntity user, final String msg, final Throwable e) {
            this.msg = msg;
            this.e = e;
            this.logged = logged;
            this.user = user;
        }
    }
}

