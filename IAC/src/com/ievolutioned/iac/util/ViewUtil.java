package com.ievolutioned.iac.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ievolutioned.iac.net.NetUtil;

/**
 * Created by Daniel on 05/04/2017.
 */

public class ViewUtil {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Shows any view for internet connection message
     *
     * @param showView target view
     * @return true if it is connected to internet, false otherwise
     */
    public static boolean internetConnectionView(final View showView) {
        if (showView == null || showView.getContext() == null)
            return false;
        if (!NetUtil.hasNetworkConnection(showView.getContext())) {
            if (showView.getVisibility() != View.VISIBLE)
                showView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation fadeIn = AnimationUtils.loadAnimation(showView.getContext(),
                                android.R.anim.fade_in);
                        showView.startAnimation(fadeIn);
                        showView.setVisibility(View.VISIBLE);
                    }
                }, 300);
            return true;
        } else {
            showView.setVisibility(View.GONE);
        }
        return false;
    }
}
