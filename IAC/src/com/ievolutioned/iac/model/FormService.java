package com.ievolutioned.iac.model;

import android.os.AsyncTask;

/**
 * Provides the forms loaded on the system
 *
 * Created by Daniel on 22/04/2015.
 */
public class FormService {
    private static final String URL_LOGIN = "https://iacgroup.herokuapp.com/api/services/access";

    private AsyncTask<Void, Void, FormResponse> task;

    private String deviceId = null;

    private String adminToken = null;

    public FormService(String deviceId, String adminToken){
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

    protected void hanldeResult(final LoginHandler callback, final FormResponse response) {
        if (response == null)
            callback.onError(new FormResponse("Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * form service handler
     */
    public interface LoginHandler {
        public void onSuccess(final FormResponse response);

        public void onError(final FormResponse response);

        public void onCancel();
    }


    public class FormResponse extends ResponseBase{
        public FormResponse(String msg, Throwable e){
            this.msg = msg;
            this.e = e;
        }
    }
}
