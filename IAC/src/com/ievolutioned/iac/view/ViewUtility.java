package com.ievolutioned.iac.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ievolutioned.iac.R;

@SuppressLint("InflateParams")
public class ViewUtility {
    /**
     * Create a loading screen, using an {@link AlertDialog} class
     *
     * @param context the context the control will be shown
     * @param title   the context the control will be shown
     * @return AlertDialog with an animation
     * @see {link http://developer.android.com/guide/topics/ui/dialogs.html}
     * <br />
     * Avoid using Dialog class directly. If a more complex dialog is needed use
     * @see {link http://developer.android.com/guide/topics/ui/dialogs.html#FullscreenDialog}
     */
    public static AlertDialog getLoadingScreen(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loading, null);
        if (title != null) {
            TextView mTitle = (TextView) view.findViewById(R.id.loading_title);
            mTitle.setText(title);
        }
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.ic_mapcross_dummy);
        return alert;
    }

    /**
     * Create a loading screen, using an {@link AlertDialog} class
     *
     * @param context the context the control will be shown
     * @return AlertDialog with an animation
     * @see {link http://developer.android.com/guide/topics/ui/dialogs.html}
     * <br />
     * Avoid using Dialog class directly. If a more complex dialog is needed use
     * @see {link http://developer.android.com/guide/topics/ui/dialogs.html#FullscreenDialog}
     */
    public static AlertDialog getLoadingScreen(Context context) {
        return getLoadingScreen(context, null);
    }

}
