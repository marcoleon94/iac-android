package com.ievolutioned.iac.fragment;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.HttpGetParam;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;

/**
 * Created by Daniel on 24/03/2015.
 */
public class SitesFragment extends Fragment {

    public static final String ARG_SITE_NAME = "ARG_SITE_NAME";

    private WebView mWebView;
    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sites, container, false);
        bindUI(root);
        return root;
    }

    private void bindUI(View root) {
        mProgress = (ProgressBar) root.findViewById(R.id.fragment_sites_progressBar);
        mProgress.setVisibility(View.GONE);
        Bundle args = getArguments();
        if (args == null)
            return;

        mWebView = (WebView) root.findViewById(R.id.fragment_sites_web_view);
        bindData(root, args.getString(ARG_SITE_NAME));
    }

    private void bindData(View root, String name) {
        if (TextUtils.isEmpty(name))
            return;

        String keys[] = getActivity().getResources().getStringArray(R.array.sites_item_key);
        String values[] = getActivity().getResources().getStringArray(R.array.sites_item_values);

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equalsIgnoreCase(name))
                showPage(values[i], keys[i]);
        }

    }

    private void showPage(String page, String key) {
        mWebView.setWebViewClient(new SiteWebClient());

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        if (AppConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mWebView.setWebContentsDebuggingEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mWebView.loadUrl(setHttpParams(page, key));
    }

    private String setHttpParams(String page, String key) {
        if (key.equalsIgnoreCase(getActivity().getString(R.string.string_site_home))) {
            HttpGetParam params = new HttpGetParam();
            params.add("ref", "xedni/draobhsad");
            params.add("token_access", AppPreferences.getAdminToken(getActivity()));
            page += "?" + params.toString();
        }
        return page;
    }

    private class SiteWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (urlIsPdf(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //user does not have a pdf viewer installed
                }
            } else {
                mWebView.loadUrl(url);
            }
            return true;
        }

        private boolean urlIsPdf(String url) {
            return url.contains("pdf");
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
