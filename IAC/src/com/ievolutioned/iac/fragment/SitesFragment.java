package com.ievolutioned.iac.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        String keys[] = getActivity().getResources().getStringArray(R.array.sites_item_key);
        String values[] = getActivity().getResources().getStringArray(R.array.sites_item_values);

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equalsIgnoreCase(name)) {
                site = name;
                showPage(values[i], keys[i]);
            }
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

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        //Set the web content debugging it is available
        if (AppConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mWebView.setWebContentsDebuggingEnabled(true);

        //Set the mixed content mode allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mWebView.loadUrl(setHttpParams(page, key));
    }

    /**
     * Sets the Http get parameters for the url
     *
     * @param page
     * @param key
     * @return
     */
    private String setHttpParams(String page, String key) {
        //HOME site
        if (key.equalsIgnoreCase(getActivity().getString(R.string.string_site_home))) {
            HttpGetParam params = new HttpGetParam();
            params.add("ref", "xedni/draobhsad");
            params.add("token_access", AppPreferences.getAdminToken(getActivity()));
            page += "?" + params.toString();
        }
        else if(key.equalsIgnoreCase(getActivity().getString(R.string.string_site_asks))){
            HttpGetParam params = new HttpGetParam();
            params.add("ref", "xedni/draobhsad");
            page += "?" + params.toString();
        }
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
            //Check if it is log out
            if (url.contains("admins/sign_in") &&
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


}
