package com.ievolutioned.iac.util;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * {@link WebView} Instance Manager. Allows a singleton pattern for a webview.
 * Contains a context instance, that could make memory leaks.
 * <p>
 * Created by Daniel on 11/11/2016.
 */

public enum WebViewManager {
    INSTANCE;
    private WebView webView;

    WebViewManager() {
    }

    /**
     * Inits a new {@link WebView} instance
     *
     * @param context - a {@link Context}
     */
    public void init(Context context) {
        if (this.webView == null) {
            this.webView = new WebView(context);
        } else {
            //Detach?
            ViewGroup parentViewGroup = (ViewGroup) webView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeView(webView);
            }
        }
    }

    /**
     * Gets the {@link WebView}
     *
     * @return - a {@link WebView}
     */
    public WebView getView() {
        return this.webView;
    }
}
