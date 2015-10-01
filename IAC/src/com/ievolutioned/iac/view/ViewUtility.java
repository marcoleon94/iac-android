package com.ievolutioned.iac.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ievolutioned.iac.R;

@SuppressLint("InflateParams")
public class ViewUtility {

    public static final int MSG_ERROR = Color.RED;
    public static final int MSG_DEFAULT = Color.LTGRAY;
    public static final int MSG_SUCCESS = Color.BLUE;

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

    /**
     * Shows a Toast message
     *
     * @param c     - Context context
     * @param color - Color an int color
     * @param msg   - The message to be displayed
     */
    public static void showMessage(Context c, int color, String msg) {
        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        View v = toast.getView();
        v.setBackgroundColor(color);
        toast.show();
    }

    /**
     * Shows a Toast message
     *
     * @param c     - Context context
     * @param color - Color an int color
     * @param msg   - The resource string
     */
    public static void showMessage(Context c, int color, int msg) {
        showMessage(c, color, c.getString(msg));
    }

}
