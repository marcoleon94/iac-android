package com.ievolutioned.iac.model;

import android.os.AsyncTask;

import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FormatUtil;

import java.util.Date;

/**
 * Created by Daniel on 28/04/2015.
 */
public abstract class ServiceBase {
    protected static final String URL_LOGIN = "https://iacgroup.herokuapp.com/api/services/access";

    protected static final String ACTION_LOGIN = "access";

    protected static final String CONTROLLER_LOGIN = "services";

    protected AsyncTask<Void, Void, ResponseBase> task;
    protected String deviceId = null;
    protected String adminToken = null;

    public ServiceBase(String deviceId, String adminToken) {
        this.deviceId = deviceId;
        this.adminToken = adminToken;
    }

    /**
     * Gets the login headers by default
     *
     * @return a HttpHeader
     */
    public HttpHeader getHeaders(final String action, final String controller){
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
