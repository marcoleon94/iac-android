package com.ievolutioned.pxform.adapters;


import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ievolutioned.pxform.PXWidget;

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
public class PXFAdapter extends BaseAdapter implements Parcelable {
    private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
    private Activity aActivity;

    private PXFAdapter(Parcel in) {
        this.lWidgets = in.readArrayList(PXWidget.class.getClassLoader());
    }

    public PXFAdapter(Activity activity, List<PXWidget> widgets) {
        lWidgets = widgets;
        aActivity = activity;
    }

    public void setActivity(Activity activity){
        aActivity = activity;
    }
    public List<PXWidget> getItems() {
        return lWidgets;
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

        if (view == null) {
            view = w.createControl(aActivity);
        }

        w.setWidgetData(view);

        return view;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        //TODO: write to parcel
    }

    public static final Parcelable.Creator<PXFAdapter> CREATOR = new Parcelable.Creator<PXFAdapter>() {

        @Override
        public PXFAdapter createFromParcel(Parcel source) {
            return new PXFAdapter(source);
        }

        @Override
        public PXFAdapter[] newArray(int size) {
            return new PXFAdapter[size];
        }
    };

}
