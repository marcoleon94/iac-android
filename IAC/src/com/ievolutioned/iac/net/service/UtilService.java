package com.ievolutioned.iac.net.service;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;

import com.ievolutioned.iac.fragment.UpdateDialogFragment;
import com.ievolutioned.iac.net.HttpGetParam;

/**
 * Manages the log in / out services for the user on the system
 * Created by Daniel on 24/02/2016.
 */
public class UtilService {

    private AsyncTask<Void, Void, Boolean> task = null;
    private Context context = null;
    private FragmentManager fragmentManager = null;

    public void getUpdate(final Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (isCancelled())
                    return false;
                try {

                    HttpGetParam params = new HttpGetParam();
                    //params.add("iac_id", id);

                    /*
                    HttpHeader headers = getHeaders(ACTION_LOGIN, CONTROLLER_LOGIN);

                    // Get response
                    NetResponse response = NetUtil.get(URL_LOGIN, params, headers);
                    if (response == null)
                        return new LoginResponse(false, null, "No response", null);
                    if (response.isBadStatus())
                        return new LoginResponse(false, null, response.toString(), null);

                    //Parse response
                    Gson g = new Gson();
                    UserEntity user = g.fromJson(response.result, UserEntity.class);
                    if (user.getIacId() != null)
                        return new LoginResponse(true, user, response.result, null);
                    return new LoginResponse(false, null, null, null);
                    *
                    */
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                showUpdateNotification(aBoolean);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        };
        task.execute();
    }

    private void showUpdateNotification(Boolean isUpdate) {
        if (isUpdate && context != null) {
            DialogFragment dialog = UpdateDialogFragment.newInstance(context);
            dialog.show(fragmentManager, UpdateDialogFragment.TAG);
        }
    }


    /**
     * Cancels the request
     */
    public void cancel() {
        if (task != null)
            task.cancel(true);
    }

}

