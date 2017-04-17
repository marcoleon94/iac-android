package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.entity.Support;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

/**
 * Service for Dining Room assistance
 * <p>
 * Created by Daniel on 12/04/2017.
 */

public class DiningService extends ServiceBase {

    public static final String ALL_INFO_DINING_ROOM = "all_info_dining_room";
    public static final String COMENSAL = "comensal";
    public static final String MSG = "msg";
    public static final String ERROR_CODE = "error_code";

    /**
     * Constructor
     *
     * @param deviceId
     * @param adminToken
     */
    public DiningService(String deviceId, String adminToken) {
        super(deviceId, adminToken);
    }


    /**
     * Gets all attendees for a dining room
     *
     * @param siteId
     * @param callback
     */
    public void getComensals(final String siteId, final DiningService.ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected DiningResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new DiningResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (adminToken != null)
                        params.add("admin-token", adminToken);
                    if (siteId != null)
                        params.add("site_id", siteId);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_GET_COMMENSALS, CONTROLLER_DINING_ROOM);

                    // Get response
                    NetResponse response = NetUtil.get(URL_GET_COMMENSALS, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new DiningResponse(
                                    json.getAsJsonObject().get(ALL_INFO_DINING_ROOM).getAsJsonArray(),
                                    response.result,
                                    null);
                    }
                    return null;
                } catch (Exception e) {
                    return new DiningResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (DiningResponse) response);
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
     * Validates if a user can go in a dining room
     *
     * @param restricted
     * @param callback   - ServiceHandler callback
     */
    public void getValidateDiningRoom(final String iacId, final boolean restricted, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected DiningResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null) {
                        callback.onError(new DiningResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (adminToken != null)
                        params.add("admin-token", adminToken);
                    if (iacId != null)
                        params.add("iac_id", iacId);
                    //Restricted or not
                    params.add("restricted", String.valueOf(restricted));

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_DINING_VALIDATE, CONTROLLER_DINING_ROOM);

                    // Get response
                    NetResponse response = NetUtil.get(URL_DINING_VALIDATE, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new DiningResponse(json.getAsJsonObject(),
                                    response.result, null);
                    }
                    return null;
                } catch (Exception e) {
                    return new DiningResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (DiningResponse) response);
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
     * Register a new commensal for a dining room
     *
     * @param body
     * @param callback
     */
    public void registerNewCommensal(final String body, final DiningService.ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected DiningResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null || body == null) {
                        callback.onError(new DiningResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    params.add("admin-token", adminToken);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_CREATE, CONTROLLER_DINING_ROOM);

                    // Get response
                    NetResponse response = NetUtil.post(URL_DINING_REGISTER, params, headers, body);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull()) {
                            if (json.getAsJsonObject().get("status").getAsString().contentEquals("success"))
                                return new DiningResponse(json.getAsJsonObject(),
                                        response.result, null);
                        }
                        return null;
                    }
                    return null;
                } catch (Exception e) {
                    return new DiningResponse(null, e.getMessage(), e);
                }
            }

            @Override
            protected void onPostExecute(ResponseBase response) {
                hanldeResult(callback, (DiningResponse) response);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }
        }

        ;
        task.execute();
    }

    /**
     * Create a Dining register string body for service call #registerNewCommensal
     *
     * @param siteId
     * @param type
     * @param category
     * @param employee
     * @param guests
     * @return
     */
    public static String getDiningRegisterBody(final long siteId, final String type, final String category,
                                               final JsonElement employee, final JsonElement guests) {
        JsonObject root = new JsonObject();
        JsonObject register = new JsonObject();
        JsonObject commensal = new JsonObject();

        register.addProperty("site_id", siteId);
        register.addProperty("support_id", Support.Type.getSupportType(type));
        register.addProperty("clasification_id", Support.Category.getSupportCategoryId(category));

        employee.getAsJsonObject().addProperty("commensal_type", "empleado");
        commensal.add("0", employee);
        register.add("commensals_attributes", commensal);

        if (guests != null) {
            //TODO: Add guests? how?
        }

        root.add("dining_register", register);
        return root.toString();
    }


    /**
     * Handles the result on callback
     *
     * @param callback - the callback of service
     * @param response - the response of service
     */
    protected void hanldeResult(final ServiceHandler callback, final DiningResponse response) {
        if (response == null)
            callback.onError(new DiningResponse(null, "Service error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * form service handler
     */
    public interface ServiceHandler {
        void onSuccess(final DiningResponse response);

        void onError(final DiningResponse response);

        void onCancel();
    }

    public interface ErrorCodes {
        int NO_ERROR = 0;
        int NO_USER = 1;
        int NO_ENTER = 2;
    }


    /**
     * Course response class. Manages the JSON elements of this response
     */
    public class DiningResponse extends ResponseBase {
        /**
         * Contains the complete JSON element on response
         */
        public JsonElement json;

        /**
         * Instantiates a Dining object
         *
         * @param json - the JsonElement
         * @param msg  - the message
         * @param e    - the error
         */
        public DiningResponse(JsonElement json, String msg, Throwable e) {
            super(msg, e);
            this.json = json;
        }
    }


}
