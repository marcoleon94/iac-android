package com.ievolutioned.pxform.adapters;


import com.ievolutioned.pxform.PXWidget;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/*
remove file form git:

remove crashlytics files from repo, but don't
remove "crashlytics.properties" in root:

 git rm -r --cached directory/filename

force git update local repo.
all not commited changes will be lost

 git fetch --all
 git reset --hard origin/master

*/

/*
remove unused repo head

 git branch -d -r origin/HEAD

 */
/**
 */
public class PXFAdapter extends BaseAdapter {
    private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
    private Activity aActivity;

    public PXFAdapter(Activity activity, List<PXWidget> widgets){
        lWidgets = widgets;
        aActivity = activity;
    }

    @Override
    public int getCount() {
        return lWidgets.size();
    }

    @Override
    public int getItemViewType(int position) {
        return lWidgets.get(position).getAdapterItemType();
    }

    @Override
    public int getViewTypeCount() {
        return PXWidget.getAdapterItemTypeCount();
    }

    @Override
    public PXWidget getItem(int position) {
        return lWidgets.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup group) {
        final PXWidget w = getItem(pos);

        if(view == null){
            view = w.createControl(aActivity);
        }

        w.setWidgetData(view);

        return view;
    }
}
