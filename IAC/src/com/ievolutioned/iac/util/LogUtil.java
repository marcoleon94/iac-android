package com.ievolutioned.iac.util;

import android.util.Log;

/**
 * Log utility class, it performs the same log as {@link android.util.Log}
 * but it determines if it is a DEBUG environment.
 * <p/>
 * Created by Daniel on 20/04/2015.
 */
public class LogUtil {

    /**
     * Logs in debug mode
     * @see android.util.Log d
     *
     * @param tag - the tag
     * @param msg - the message
     */
    public static void d(String tag, String msg) {
        if (AppConfig.DEBUG)
            Log.d(tag, msg);
    }

    /**
     * Logs in debug mode
     * @param tag - the tag
     * @param msg - the message
     * @param e - the error
     */
    public static void e(String tag, String msg, Throwable e) {
        if (AppConfig.DEBUG)
            Log.e(tag, msg, e);
    }
}
