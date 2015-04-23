package com.ievolutioned.pxform.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.pxform.PXFParser;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
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
    private AdapterEventHandler eventHandler;
    private String parcelJson = "";

    public interface AdapterEventHandler {
        void onClick(PXWidget widget);
    }

    public void setAdapterEventHandler(AdapterEventHandler callback){
        eventHandler = callback;
    }

    private PXFAdapter(Parcel in) {
        Log.e("Parcel in",in.toString());
        this.parcelJson = in.readString();
    }

    public PXFAdapter(Activity activity, List<PXWidget> widgets) {
        lWidgets = widgets;
        aActivity = activity;
    }

    public String getParcelJson() {
        return parcelJson;
    }

    public void setParcelJson(String parcelJson) {
        this.parcelJson = parcelJson;
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

        w.setEventHandler(widgetHandler);
        w.setWidgetData(view);

        return view;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        PXFParser p = new PXFParser(null);
        String json = p.getSavedState(this);
        if(json != null)
            out.writeString(json);
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

    private PXWidget.PXWidgetHandler widgetHandler = new PXWidget.PXWidgetHandler() {
        @Override
        public void notifyDataSetChanges() {
            PXFAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onClick(PXWidget parent) {
            if(eventHandler != null){
                eventHandler.onClick(parent);
            }
        }
    };
}