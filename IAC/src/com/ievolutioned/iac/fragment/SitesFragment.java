package com.ievolutioned.iac.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ProgressBar;

import com.ievolutioned.iac.R;

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
        mProgress =(ProgressBar) root.findViewById(R.id.fragment_sites_progressBar);
        mProgress.setVisibility(View.GONE);
        Bundle args = getArguments();
        if (args == null)
            return;

        mWebView =(WebView) root.findViewById(R.id.fragment_sites_web_view);
        bindData(root, args.getString(ARG_SITE_NAME));
    }

    private void bindData(View root, String name) {
        if (TextUtils.isEmpty(name))
            return;

        String keys[] = getActivity().getResources().getStringArray(R.array.sites_item_key);
        String values[] = getActivity().getResources().getStringArray(R.array.sites_item_values);

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equalsIgnoreCase(name))
                showPage(values[i]);

        }

    }

    private void showPage(String page) {
        mWebView.setWebViewClient(new SiteWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(page);

    }

    private class SiteWebClient extends WebViewClient {
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
    }


}
