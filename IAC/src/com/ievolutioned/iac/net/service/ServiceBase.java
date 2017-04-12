package com.ievolutioned.iac.net.service;

import android.os.AsyncTask;

import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FormatUtil;

import java.util.Date;

/**
 * Service base class. Contains the main features of services
 * <p/>
 * Created by Daniel on 28/04/2015.
 */
public abstract class ServiceBase {

    /**
     * Protocols
     **/
    private static final String PROTOCOL = "http://";
    private static final String PROTOCOL_SECURE = "https://";

    /**
     * Domains
     **/
    private static final String DOMAIN = "herokuapp.com/";
    private static final String SUBDOMAIN = AppConfig.DEBUG ? "iac-group-stage." : "iacgroup.";

    /**
     * URL prefixes
     */
    private static final String URL_PRE = PROTOCOL + SUBDOMAIN + DOMAIN;
    private static final String URL_PRE_SECURE = PROTOCOL_SECURE + SUBDOMAIN + DOMAIN;

    /**
     * Action for login
     */
    protected static final String ACTION_LOGIN = "access";
    /**
     * Action for create inquest response
     */
    protected static final String ACTION_CREATE = "create";
    /**
     * Action for update inquest response
     */
    protected static final String ACTION_UPDATE = "update";
    /**
     * Action for get all inquest
     */
    protected static final String ACTION_INDEX = "index";
    /**
     * Action for get a single inquest
     */
    protected static final String ACTION_SHOW = "show";
    /**
     * Action for get profile
     */
    protected static final String ACTION_GET_INFO = "get_info_admin";
    /**
     * Action for update admin profile
     */
    protected static final String ACTION_UPDATE_ADMIN = "update_admin";

    /**
     * Action for mobile versions
     */
    protected static final String ACTION_MOBILE = "mobile_versions";

    /**
     * Action for get course attendees
     */
    protected static final String ACTION_COURSE_ATTENDEE = "get_course_attendees";

    /**
     * Action for course attendee info
     */
    protected static final String ACTION_COURSE_ATTENDEE_INFO = "get_course_attendee_info";

    /**
     * Action for dinning validation
     */
    protected static final String ACTION_DINING_VALIDATE = "validate_dining_room";

    /**
     * Controller constant for services
     */
    protected static final String CONTROLLER_SERVICES = "services";
    /**
     * Controller constant for login services
     */
    protected static final String CONTROLLER_LOGIN = "services";
    /**
     * Controller constant for user responses
     */
    protected static final String CONTROLLER_USER = "user_responses";
    /**
     * Controller constant for inquests
     */
    protected static final String CONTROLLER_INQUESTS = "inquests";
    /**
     * Controller constant for profile
     */
    protected static final String CONTROLLER_PROFILE = "admin";
    /**
     * Controller for courses
     */
    protected static final String CONTROLLER_COURSES = "info_courses";
    /**
     * Controller for dining
     */
    protected static final String CONTROLLER_DINING_ROOM = "dining_room";

    /**
     * URL for user log in on server
     */
    protected static final String URL_LOGIN = URL_PRE_SECURE + "api/services/access";
    /**
     * URL for user responses
     */
    protected static final String URL_USER = URL_PRE_SECURE + "api/user_responses/";
    /**
     * URL for form or inquests on the server
     */
    protected static final String URL_FORM = URL_PRE_SECURE + "api/inquests/";
    /**
     * URL for Profile
     */
    protected static final String URL_PROFILE = URL_PRE + "api/admin/";

    /**
     * URL for All courses
     */
    protected static final String URL_COURSES_ACTIVE = URL_PRE + "api/info_courses/";

    /**
     * URL for course attendees
     */
    protected static final String URL_COURSES_ATTENDEES = URL_PRE + "api/info_courses/%d/get_course_attendees";

    /**
     * URL for modify courses
     */
    protected static final String URL_COURSES_MODIFY_ATTENDEES = URL_PRE + "api/info_courses/%d";

    /**
     * URL for information of new attendee
     */
    protected static final String URL_COURSES_ATTENDEE_INFO = URL_PRE + "api/info_courses/get_course_attendee_info";

    /**
     * URL for dining validation
     */
    protected static final String URL_DINING_VALIDATE = URL_PRE + "api/" + CONTROLLER_DINING_ROOM + "/" + ACTION_DINING_VALIDATE;

    /**
     * URL for Mobile Versions
     */
    protected static final String URL_MOBILE_VERSION = URL_PRE + "api/services/mobile_versions";


    /**
     * AsyncTask task for service
     */
    protected AsyncTask<Void, Void, ResponseBase> task;
    /**
     * Required as the unique ID of device for secret
     */
    protected String deviceId = null;
    /**
     * Required admin token to access on system
     */
    protected String adminToken = null;

    /**
     * Constructor
     *
     * @param deviceId
     * @param adminToken
     */
    public ServiceBase(String deviceId, String adminToken) {
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

    /**
     * Gets the login headers by default
     *
     * @return a HttpHeader
     */
    public HttpHeader getHeaders(final String action, final String controller) {
        HttpHeader headers = new HttpHeader();

        String xVersion = AppConfig.API_VERSION;
        String xToken = AppConfig.API_TOKEN;
        String xAdminToken = this.adminToken;
        String reversedID = FormatUtil.reverseString(this.deviceId);
        String xDate = FormatUtil.dateDefaultFormat(new Date());
        String xDevice = this.deviceId;

        //#{X-token}-#{controller}-#{action}-#{X-version}-#{device_id.reverse}-#{X-admin-token}-#{X-device-date}
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

}
