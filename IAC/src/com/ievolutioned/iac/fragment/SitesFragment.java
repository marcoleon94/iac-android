package com.ievolutioned.iac.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;

/**
 * SitesFragment class, provides a WebView view to represent a website on the main view
 * <p/>
 * Created by Daniel on 24/03/2015.
 */
public class SitesFragment extends BaseFragmentClass {
    /**
     * TAG
     */
    public static final String TAG = SitesFragment.class.getName();
    /**
     * Site name
     */
    public static final String ARG_SITE_NAME = "ARG_SITE_NAME";
    /**
     * Main WebView
     */
    private WebView mWebView;
    /**
     * Progress bar
     */
    private ProgressBar mProgress;
    /**
     *
     */
    private String site;

    /**
     * File path callback
     */
    private ValueCallback<Uri[]> mFilePathCallback;
    /**
     * Upload message
     */
    private ValueCallback<Uri> mUploadMessage;
    /**
     * Input file
     */
    public static final int INPUT_FILE_REQUEST_CODE = 1104;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sites, container, false);
        bindUI(root);
        drawerToggleSynchronizeState();
        return root;
    }

    /**
     * Binds the root view to the controls
     *
     * @param root
     */
    private void bindUI(View root) {
        mProgress = (ProgressBar) root.findViewById(R.id.fragment_sites_progressBar);
        mProgress.setVisibility(View.GONE);
        Bundle args = getArguments();
        if (args == null)
            return;

        mWebView = (WebView) root.findViewById(R.id.fragment_sites_web_view);
        bindData(args.getString(ARG_SITE_NAME));
        setTitle(args);
    }

    /**
     * Binds the data to the current view
     *
     * @param name - name of site
     */
    private void bindData(String name) {
        if (TextUtils.isEmpty(name))
            return;

        //Verify if its added or not
        if (isAdded()) {
            //Show page
            String keys[] = getActivity().getResources().getStringArray(R.array.sites_item_key);
            String values[] = getActivity().getResources().getStringArray(R.array.sites_item_values);

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equalsIgnoreCase(name)) {
                    site = name;
                    showPage(values[i], keys[i]);
                }
            }
        } else {
            LogUtil.e(TAG, "Fragment is not Added!", null);
        }
    }

    /**
     * Sets the title on the toolbar
     *
     * @param args - Bundle of arguments
     */
    private void setTitle(Bundle args) {
        if (args != null && args.containsKey(ARG_SITE_NAME))
            getActivity().setTitle(args.getString(ARG_SITE_NAME));
    }

    /**
     * Synchronizes state drawer toggle
     */
    private void drawerToggleSynchronizeState() {
        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).DrawerToggleSynchronizeState();
    }

    /**
     * Shows the page
     *
     * @param page - Website page
     * @param key  - Key for parameters
     */
    private void showPage(String page, String key) {
        mWebView.setWebViewClient(new SiteWebClient());
        mWebView.setWebChromeClient(new SiteChromeClient());

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);


        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //Set the web content debugging it is available
        if (AppConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mWebView.setWebContentsDebuggingEnabled(true);

        //Set the mixed content mode allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mWebView.loadUrl(setHttpParams(page));
    }

    /**
     * Sets the Http get parameters for the url
     *
     * @param page
     * @return url + params
     */
    protected String setHttpParams(String page) {
        //HOME site
        HttpGetParam params = new HttpGetParam();
        params.add("ref", "xedni/draobhsad");
        params.add("token_access", AppPreferences.getAdminToken(getActivity()));
        page += "?" + params.toString();
        return page;
    }

    /**
     * Handle back pressed for web view
     */
    public void onBackPressed() {
        if (mWebView != null)
            mWebView.goBack();
    }

    /**
     * SiteWebClient class extends of WebViewClient class that allows
     * an interface main methods of the web client
     */
    private class SiteWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, url);
            if (!isAdded()) {
                LogUtil.e(TAG, "Fragment is not added", null);
                return false;
            }
            //Check if it is log out
            if (url.contains("admins/sign_in") && isAdded() &&
                    site.contentEquals(getString(R.string.string_site_home)))
                bindData(site);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    }

    /**
     * Site chrome client. Used for file chooser only, allows open an intent and take a file path for
     * android 5.0+ and 3.0+
     */
    public class SiteChromeClient extends WebChromeClient {
        /**
         * Show file chooser for Android 5.0+
         *
         * @param view              - WebView
         * @param filePath          - The ValueCallback path
         * @param fileChooserParams - FileChooserParams parameters
         * @return true
         */
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Seleccionar Archivo");
            getActivity().startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        /**
         * Show file chooser for android 3.0+
         *
         * @param uploadMsg  - ValueCallback upload message
         * @param acceptType - String accepType
         */
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            // On select image call onActivityResult method of activity
            getActivity().startActivityForResult(i, INPUT_FILE_REQUEST_CODE);
        }

        /**
         * Show file chooser for android < 3.0
         *
         * @param uploadMsg - ValueCallback uploadMsg
         */
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        /**
         * openFileChooser for other Android versions
         */
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    /**
     * Manages the input file for the activity result data
     *
     * @param data        - Intent data that should contain the callback
     * @param requestCode - Request code
     */
    public void manageInputFile(Intent data, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                return;
            }
            Uri[] results = null;

            String dataString = data.getDataString();
            if (dataString != null) {
                results = new Uri[]{Uri.parse(dataString)};
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode == INPUT_FILE_REQUEST_CODE) {
                if (this.mUploadMessage == null) {
                    return;
                }
                Uri result = null;
                try {
                    result = data.getData();
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

}
