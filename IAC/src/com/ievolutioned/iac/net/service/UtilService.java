package com.ievolutioned.iac.net.service;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.LastVersionMobile;
import com.ievolutioned.iac.fragment.UpdateDialogFragment;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.net.HttpHeader;
import com.ievolutioned.iac.net.NetResponse;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.util.LogUtil;

/**
 * Manages the log in / out services for the user on the system
 * Created by Daniel on 24/02/2016.
 */
public class UtilService extends ServiceBase {

    public final static String TAG = UtilService.class.getName();

    private final static String adminToken = "nosession";
    private AsyncTask<Void, Void, VersionResult> task = null;
    private Context context = null;
    private FragmentManager fragmentManager = null;

    /**
     * Constructor
     *
     * @param deviceId
     */
    public UtilService(String deviceId) {
        super(deviceId, adminToken);
    }

    public void getUpdate(final Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        task = new AsyncTask<Void, Void, VersionResult>() {
            @Override
            protected VersionResult doInBackground(Void... voids) {
                if (isCancelled())
                    return null;
                try {

                    HttpGetParam params = new HttpGetParam();

                    HttpHeader headers = getHeaders(ACTION_MOBILE, CONTROLLER_SERVICES);

                    // Get response
                    NetResponse response = NetUtil.get(URL_MOBILE_VERSION, params, headers);
                    if (response == null)
                        return null;
                    if (response.isBadStatus())
                        return null;

                    LogUtil.d(TAG, response.result);

                    //Parse response
                    Gson g = new Gson();
                    VersionResult versionMobile = g.fromJson(response.result,
                            VersionResult.class);
                    if (versionMobile != null)
                        return versionMobile;
                    return null;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(VersionResult versionResult) {
                super.onPostExecute(versionResult);
                if (versionResult != null && versionResult.getLastVersionMobile() != null)
                    showUpdateNotification(versionResult.getLastVersionMobile());
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        };
        task.execute();
    }

    public void getUpdate(final IUpdateVersion callback) {
        task = new AsyncTask<Void, Void, VersionResult>() {
            @Override
            protected VersionResult doInBackground(Void... voids) {
                if (isCancelled())
                    return null;
                try {

                    HttpGetParam params = new HttpGetParam();

                    HttpHeader headers = getHeaders(ACTION_MOBILE, CONTROLLER_SERVICES);

                    // Get response
                    NetResponse response = NetUtil.get(URL_MOBILE_VERSION, params, headers);
                    if (response == null)
                        return null;
                    if (response.isBadStatus())
                        return null;

                    LogUtil.d(TAG, response.result);
                    
                    //Parse response
                    Gson g = new Gson();
                    VersionResult versionMobile = g.fromJson(response.result,
                            VersionResult.class);
                    if (versionMobile != null)
                        return versionMobile;
                    return null;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(VersionResult versionResult) {
                super.onPostExecute(versionResult);
                if (versionResult != null && versionResult.getLastVersionMobile() != null)
                    callback.onUpdateVersionResult(versionResult.lastVersionMobile);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        };
        task.execute();
    }

    private void showUpdateNotification(LastVersionMobile versionResult) {
        try {
            if (versionResult != null && context != null && !versionResult.getVersioAndroid()
                    .contentEquals(context.getString(R.string.app_version))) {
                UpdateDialogFragment dialog = UpdateDialogFragment.newInstance(context);
                dialog.setLastVersionMobile(versionResult);
                dialog.show(fragmentManager, UpdateDialogFragment.TAG);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }


    /**
     * Cancels the request
     */
    public void cancel() {
        if (task != null)
            task.cancel(true);
    }

    public class VersionResult {
        private String status;
        @SerializedName("last_versions_mobiles")
        private LastVersionMobile lastVersionMobile;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LastVersionMobile getLastVersionMobile() {
            return lastVersionMobile;
        }

        public void setLastVersionMobile(LastVersionMobile lastVersionMobile) {
            this.lastVersionMobile = lastVersionMobile;
        }
    }

    /**
     * Interface for Update Version
     */
    public interface IUpdateVersion {

        /**
         * Callback for update version result
         *
         * @param lastVersionMobile - Result
         */
        void onUpdateVersionResult(final LastVersionMobile lastVersionMobile);
    }

}

