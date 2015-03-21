package com.ievolutioned.iac.model;

import android.os.AsyncTask;

/**
 * Manages the log in / out services for the user on the system
 * Created by Daniel on 20/03/2015.
 */
public class LoginService {

    private AsyncTask<Void, Void, LoginResponse> task;

    /**
     * Logs the user in the system
     *
     * @param email
     * @param pass
     * @param callback a LoginHandler callback handler
     */
    public void logIn(final String email, final String pass, final LoginHandler callback) {
        task = new AsyncTask<Void, Void, LoginResponse>() {
            @Override
            protected LoginResponse doInBackground(Void... voids) {
                if (isCancelled())
                    return null;
                try {
                    return new LoginResponse(true, null, null);
                } catch (Exception e) {
                    return new LoginResponse(false, e.getMessage(), e);
                }
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
                    return new LoginResponse(false, null, null);
                } catch (Exception e) {
                    return new LoginResponse(true, e.getMessage(), e);
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
            callback.onError(new LoginResponse(false, "Service error", new RuntimeException()));
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

        public LoginResponse(final boolean logged, final String msg, final Throwable e) {
            this.msg = msg;
            this.e = e;
            this.logged = logged;
        }
    }
}

