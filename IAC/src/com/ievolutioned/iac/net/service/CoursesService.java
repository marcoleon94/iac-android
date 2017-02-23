package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;

import java.util.ArrayList;
import java.util.Locale;

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
     * Gets Info Courses. Gets a list of active courses
     *
     * @param token
     * @param id
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
                    if (id != null)
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
     * Gets course Attendees. Gets a list of id and users for a course id
     *
     * @param token
     * @param iac_id
     * @param callback
     */
    public void getAttendees(final String token, final String iac_id, final int course_id, final ServiceHandler callback) {
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
                    if (iac_id != null)
                        params.add("iac_id", iac_id);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_COURSE_ATTENDEE, CONTROLLER_COURSES);

                    // Get response
                    String urlCoursesAttendees = URL_COURSES_ATTENDEES;
                    NetResponse response = NetUtil.get(String.format(Locale.getDefault(),
                            urlCoursesAttendees, course_id), params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new CoursesResponse(json.getAsJsonArray(), response.result, null);
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
     * Patch and modify the attendees for a course
     *
     * @param token
     * @param iac_id
     * @param course_id
     * @param attendeeIds
     * @param callback
     */
    public void modifyAttendees(final String token, final String iac_id, final int course_id,
                                final ArrayList<Integer> attendeeIds, final ServiceHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected CoursesResponse doInBackground(Void... p) {
                if (isCancelled())
                    return null;
                try {
                    if (deviceId == null || adminToken == null || attendeeIds == null) {
                        callback.onError(new CoursesResponse(null, "Params are null", null));
                        this.cancel(true);
                    }
                    HttpGetParam params = new HttpGetParam();
                    if (token != null)
                        params.add("admin-token", token);
                    if (iac_id != null)
                        params.add("iac_id", iac_id);
                    if (attendeeIds != null)
                        params.add("attendee_ids", attendeeIds.toString());


                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_UPDATE, CONTROLLER_COURSES);
                    // Get response
                    String urlCoursesModifyAttendees = URL_COURSES_MODIFY_ATTENDEES;
                    NetResponse response = NetUtil.patch(String.format(Locale.getDefault(),
                            urlCoursesModifyAttendees, course_id), params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new CoursesResponse(json.getAsJsonObject(), response.result, null);
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
     * Get Attendee info by id
     *
     * @param token
     * @param iac_id
     * @param callback
     */
    public void getNewAttendeeInfo(final String token, final String iac_id, final ServiceHandler callback) {
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
                    if (iac_id != null)
                        params.add("iac_id", iac_id);

                    //Get headers
                    HttpHeader headers = getHeaders(ACTION_COURSE_ATTENDEE_INFO, CONTROLLER_COURSES);
                    // Get response
                    NetResponse response = NetUtil.get(URL_COURSES_ATTENDEE_INFO, params, headers);

                    if (response != null) {
                        JsonElement json = new JsonParser().parse(response.result);
                        if (!json.isJsonNull())
                            return new CoursesResponse(json.getAsJsonObject(), response.result, null);
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
