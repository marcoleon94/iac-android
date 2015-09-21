package com.ievolutioned.iac.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ievolutioned.iac.net.service.ResponseBase;
import com.ievolutioned.iac.util.LogUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Daniel on 21/09/2015.
 */
public class DownloadImageTask {

    public static final String TAG = DownloadImageTask.class.getName();
    private AsyncTask<Void, Void, ResponseBase> task = null;

    public void downloadImageFromURL(final String url, final DownloadHandler callback) {
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
                handleResult(callback, (DownloadImageResponse) responseBase);
            }
        };
        task.execute();
    }

    protected void handleResult(final DownloadHandler callback, final DownloadImageResponse response) {
        if (response == null)
            callback.onError(new DownloadImageResponse(null, "Error", new RuntimeException()));
        else if (response.e != null)
            callback.onError(response);
        else
            callback.onDownloaded(response);
    }

    public interface DownloadHandler {
        void onDownloaded(final DownloadImageResponse response);

        void onError(final DownloadImageResponse response);

        void onCancel();
    }

    public class DownloadImageResponse extends ResponseBase {
        public Bitmap image;

        public DownloadImageResponse(Bitmap image, final String msg, final Throwable e) {
            super(msg, e);
            this.image = image;
        }
    }
}
