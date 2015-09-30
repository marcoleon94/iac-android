package com.ievolutioned.iac.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.ievolutioned.iac.net.service.ResponseBase;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Cloud image task class, manages the asynchronous methods to upload an download images from
 * Cloudinary repositories
 *
 * @see <a href="http://cloudinary.com/documentation">Cloudinary.com/documentation</a>
 * Created by Daniel on 21/09/2015.
 */
public class CloudImageTask {

    /**
     * TAG
     */
    public static final String TAG = CloudImageTask.class.getName();
    /**
     * Main task
     */
    private AsyncTask<Void, Void, ResponseBase> task = null;

    private static final String NAME = "public_id";
    private static final String FORMAT = "format";

    /**
     * Downloads a image from any URL as a get method
     *
     * @param url      - the URL of image
     * @param callback - CloudImageHandler callback
     */
    public void downloadImageFromURL(final String url, final CloudImageHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected ResponseBase doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    URL u = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null)
                        return new DownloadImageResponse(null, "No se puede descargar la imagen", null);
                    return new DownloadImageResponse(bitmap, null, null);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseBase responseBase) {
                super.onPostExecute(responseBase);
                handleResult(callback, responseBase);
            }
        };
        task.execute();
    }

    /**
     * Uploads a Image File to a specific cloudinary repository
     *
     * @param file     - File file must exist
     * @param callback - CloudImageHandler callback
     */
    public void uploadImageFile(final File file, final CloudImageHandler callback) {
        task = new AsyncTask<Void, Void, ResponseBase>() {
            @Override
            protected ResponseBase doInBackground(Void... params) {
                Cloudinary cloudinary = new Cloudinary(getCloudinarySettings());
                if (file.exists())
                    try {
                        Map result = cloudinary.uploader().upload(file, null);
                        if (result != null)
                            return new UploadImageResponse(result,
                                    getFileName(result), result.toString(), null);
                        return null;
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage(), e);
                        return null;
                    }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseBase responseBase) {
                super.onPostExecute(responseBase);
                handleResult(callback, responseBase);
            }
        };
        task.execute();
    }

    private String getFileName(Map result) {
        return String.format("%s.%s", result.get(NAME).toString(), result.get(FORMAT).toString());
    }

    /**
     * Gets the cloudinary settings on app
     *
     * @return - Map a set of configurations
     */
    private Map getCloudinarySettings() {
        Map config = new HashMap();
        config.put("cloud_name", AppConfig.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", AppConfig.CLOUDINARY_API_KEY);
        config.put("api_secret", AppConfig.CLOUDINARY_API_SECRET);
        return config;
    }

    /**
     * Handles a result for the callback
     *
     * @param callback - CloudImageHandler callback
     * @param response - The main response
     */
    protected void handleResult(final CloudImageHandler callback, final ResponseBase response) {
        if (response == null)
            callback.onError(new DownloadImageResponse(null, "Error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onSuccess(response);
    }

    /**
     * Defines a set of cloud image operations
     */
    public interface CloudImageHandler {
        void onSuccess(final ResponseBase response);

        void onError(final ResponseBase response);

        void onCancel();
    }

    /**
     * DownloadImageResponse class, contains a set of attributes for the downloaded response
     */
    public class DownloadImageResponse extends ResponseBase {
        /**
         * A Bitmap of a downloaded image
         */
        public Bitmap image;

        public DownloadImageResponse(Bitmap image, final String msg, final Throwable e) {
            super(msg, e);
            this.image = image;
        }
    }

    /**
     * UploadImageResponse class, contains a set of attributes for the uploaded response
     */
    public class UploadImageResponse extends ResponseBase {
        /**
         * A set of results for the response
         */
        public Map response;
        /**
         * The main file name for uploaded image
         */
        public String file;

        public UploadImageResponse(Map response, String file, final String msg, final Throwable e) {
            super(msg, e);
            this.response = response;
            this.file = file;
        }
    }
}
