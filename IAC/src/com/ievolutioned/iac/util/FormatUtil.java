package com.ievolutioned.iac.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formatter utility class. It provides a set of operation about formatting
 * Created by Daniel on 20/04/2015.
 */
public class FormatUtil {
    /**
     * Gets a MD5 format from the current string
     *
     * @param s - the string to be converted
     * @return a MD5 format, empty string if error
     */
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(FormatUtil.class.getName(), e.getMessage(), e);
        }
        return "";
    }

    /**
     * Gets a default format string <i>yyyy-MM-dd</i> from date
     *
     * @param date a Date
     * @return a formatted string, empty string if error
     * @see com.ievolutioned.iac.util.AppConfig API_DATE_FORMAT
     */
    public static final String dateDefaultFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppConfig.API_DATE_FORMAT, Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            LogUtil.e(FormatUtil.class.getName(), e.getMessage(), e);
        }
        return "";
    }

    /**
     * Parses data from ruby in a single format dd/MM - HH:mm
     *
     * @param s date from ruby
     * @return
     */
    public static String parseDate(String s) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss.sssZZZZ";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            Date date;
            date = sdf.parse(s);
            String newFormatString = "dd/MM'-'HH:mm";
            SimpleDateFormat newFormatter = new SimpleDateFormat(newFormatString, Locale.getDefault());
            return newFormatter.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * Parses a date in a single format dd/MM - HH:mm
     *
     * @param date
     * @return
     */
    public static String parseDate(Date date) {
        try {
            String newFormatString = "dd/MM'-'HH:mm";
            SimpleDateFormat newFormatter = new SimpleDateFormat(newFormatString, Locale.getDefault());
            return newFormatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets a reverse String
     *
     * @param s the string to be reversed
     * @return a string reversed. Empty string if error
     */
    public static final String reverseString(String s) {
        try {
            StringBuilder sb = new StringBuilder(s);
            return sb.reverse().toString();
        } catch (Exception e) {
            LogUtil.e(FormatUtil.class.getName(), e.getMessage(), e);
        }
        return "";
    }
}
