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
    private static final String URL_FORM = "https://iacgroup.herokuapp.com/api/inquests/";

    private static final String JSON_INQUESTS = "inquests";

    private static final String JSON_INQUEST = "inquest";

    private static final String CONTROLLER = "inquests";
    private static final String ACTION_INDEX = "index";
    private static final String ACTION_SHOW = "show";

    private AsyncTask<Void, Void, FormResponse> task;

    private String deviceId = null;

    private String adminToken = null;

    public FormService(String deviceId, String adminToken) {
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

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

                    HttpHeader headers = getFormHeaders(ACTION_INDEX);

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

    public void getFormById(final int idForm, final ServiceHandler callback){
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

                    HttpHeader headers = getFormHeaders(ACTION_SHOW);

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


    public class FormResponse extends ResponseBase {
        public JsonElement json;

        public FormResponse(JsonElement json, String msg, Throwable e) {
            this.json = json;
            this.msg = msg;
            this.e = e;
        }
    }
}
