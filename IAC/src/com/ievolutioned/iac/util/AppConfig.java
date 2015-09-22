package com.ievolutioned.iac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Daniel on 23/03/2015.
 */
public class AppConfig {
    /**
     * Indicates if it is in a debug mode
     */
    public static final boolean DEBUG = true;

    // ------------------------------------------------
    // API Configurations
    // ------------------------------------------------

    /**
     * IAC web API version
     */
    public static final String API_VERSION = "V1";

    /**
     * IAC web API token
     */
    public static final String API_TOKEN = "d4e9a9414181819f3a47ff1ddd9b2ca3";

    /**
     * IAC web API date format for headers
     */
    public static final String API_DATE_FORMAT = "yyyy-MM-dd";

    // --------------------------------------------
    // Cloudinary Configuration
    // --------------------------------------------
    /**
     * Cloudinary cloud name
     */
    public static final String CLOUDINARY_CLOUD_NAME = "iacgroup";
    /**
     * Cloudinary api key
     */
    public static final String CLOUDINARY_API_KEY = "855275749257973";
    /**
     * Cloudinary api secret
     */
    public static final String CLOUDINARY_API_SECRET = "xcWDVYFPZ9eeigoVMgrr2AjE3go";

    // --------------------------------------------
    // UUID Configuration
    // --------------------------------------------
    protected static UUID uuid;

    private static final String PREFS_DEVICE_ID_FILE = "device_id_file";
    private static final String PREFS_DEVICE_ID = "device_id";

    /**
     * Sets the UUID of the phone
     *
     * @param context - the current context for SharedPreferences
     */
    private static void setUUID(Context context) {
        final String fakeId = "9774d56d682e549c";
        if (uuid == null) {
            synchronized (AppConfig.class) {
                // Get shared preferences
                final SharedPreferences sp = context.getSharedPreferences(
                        PREFS_DEVICE_ID_FILE, 0);
                final String id = sp.getString(PREFS_DEVICE_ID, null);
                // identify UUID
                if (id != null) {
                    uuid = UUID.fromString(id);
                } else {
                    // Get android id
                    final String androidId = Settings.Secure.getString(
                            context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    try {
                        // verify for fake id
                        if (!fakeId.equals(androidId)) {
                            uuid = UUID.nameUUIDFromBytes(androidId
                                    .getBytes("utf8"));
                        } else {
                            // Get device Id if necessary
                            final String deviceId = ((TelephonyManager) context
                                    .getSystemService(Context.TELEPHONY_SERVICE))
                                    .getDeviceId();
                            uuid = deviceId != null ? UUID
                                    .nameUUIDFromBytes(deviceId
                                            .getBytes("utf8")) : UUID
                                    .randomUUID();
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    // Save UUID on SP
                    sp.edit().putString(PREFS_DEVICE_ID, uuid.toString())
                            .commit();
                }
            }
        }
    }

    /**
     * Gets the UUID string. No warranties indeed.
     *
     * @param context the current context for SharedPreferences
     * @return the UUID if it exists.
     */
    public static String getUUID(Context context) {
        if (DEBUG)
            return "7027E1C7-8215-43AA-98EE-F7E5EC2DDE67";
        if (uuid == null)
            setUUID(context);
        return uuid.toString();
    }
}
