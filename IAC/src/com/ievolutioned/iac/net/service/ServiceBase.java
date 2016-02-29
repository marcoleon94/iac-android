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
     * URL for user log in on server
     */
    protected static final String URL_LOGIN = "https://iacgroup.herokuapp.com/api/services/access";
    /**
     * URL for user responses
     */
    protected static final String URL_USER = "https://iacgroup.herokuapp.com/api/user_responses/";
    /**
     * URL for form or inquests on the server
     */
    protected static final String URL_FORM = "https://iacgroup.herokuapp.com/api/inquests/";
    /**
     * URL for Profile
     */
    protected static final String URL_PROFILE = "http://iacgroup.herokuapp.com/api/admin/";

    /**
     * URL for Mobile Versions
     */
    protected static final String URL_MOBILE_VERSION = "http://iacgroup.herokuapp.com/api/services/mobile_versions";


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
