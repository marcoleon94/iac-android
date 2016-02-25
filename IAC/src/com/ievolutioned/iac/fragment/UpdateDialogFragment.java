package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.LastVersionMobile;

/**
 * UpdateDialogFragment class, manages the update dialog to update the app
 * <p/>
 * Created by Daniel on 24/02/2016.
 */
public class UpdateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    /**
     * TAG
     */
    public static final String TAG = UpdateDialogFragment.class.getName();

    /**
     * Context that allows the new intent for update action
     */
    private Context context;
    private LastVersionMobile lastVersionMobile;

    /**
     * Instantiates a new UpdateDialogFragment fragment object with its context
     *
     * @param context - Context that allows the new intent for update action
     * @return UpdateDialogFragment fragment
     */
    public static UpdateDialogFragment newInstance(final Context context) {
        Bundle args = new Bundle();
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        fragment.setArguments(args);
        fragment.context = context;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.string_fragment_update_title);
        if (lastVersionMobile != null)
            builder.setMessage(lastVersionMobile.getDescriptionAndroid());
        else
            builder.setMessage(R.string.string_fragment_update_body);
        builder.setPositiveButton(R.string.string_fragment_update_b_positive, this);
        builder.setNegativeButton(R.string.string_fragment_update_b_negative, this);
        return builder.create();
    }

    /**
     * This method will be invoked when a button in the dialog is clicked.
     *
     * @param dialog The dialog that received the click.
     * @param which  The button that was clicked (e.g.
     *               {@link DialogInterface#BUTTON1}) or the position
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                startActivityUpdate();
                dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * Starts the activity for update app
     */
    private void startActivityUpdate() {
        //Look for url on response
        if (lastVersionMobile != null && !lastVersionMobile.getUrlAndroid().isEmpty())
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(lastVersionMobile.getUrlAndroid())));
        else {
            //Get plays and app package name
            final String appPackageName = context.getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void setLastVersionMobile(LastVersionMobile lastVersionMobile) {
        this.lastVersionMobile = lastVersionMobile;
    }
}
