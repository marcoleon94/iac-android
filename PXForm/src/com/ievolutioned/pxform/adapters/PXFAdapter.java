package com.ievolutioned.pxform.adapters;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ievolutioned.pxform.PXFParser;
import com.ievolutioned.pxform.PXFUnknownControlType;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ievolutioned.pxform.PXWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    
    private PXWidget.PXWidgetHandler widgetHandler = new PXWidget.PXWidgetHandler() {
        @Override
        public boolean addChildWidgets(PXWidget parent, int selected_index) {
            List<PXWidget> wl = new ArrayList<PXWidget>();
            PXWidget widget;
            int index = -1;
            final int size = PXFAdapter.this.lWidgets.size();
            JsonArray array;
            JsonElement sub;

            if(!parent.getJsonEntries().containsKey(PXWidget.FIELD_KEY)
                    || existControls(parent)
                    || !parent.getJsonEntries().get(PXWidget.FIELD_OPTIONS).getValue().isJsonArray())
                return false;

            array = parent.getJsonEntries().get(PXWidget.FIELD_OPTIONS).getValue().getAsJsonArray();
            sub = array.get(selected_index);

            if(!sub.isJsonObject()
                    || sub.getAsJsonObject().entrySet().size() < 1
                    || !sub.getAsJsonObject().entrySet().iterator().hasNext()
                    || !sub.getAsJsonObject().entrySet().iterator().next().getValue().isJsonArray())
                return false;

            array = sub.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray();

            for(int z = 0; z < array.size(); ++z) {
                JsonObject entry = array.get(z).getAsJsonObject();

                if (entry.entrySet().size() < 1)
                    continue;

                Map<String, Map.Entry<String, JsonElement>> map
                        = new HashMap<String, Map.Entry<String, JsonElement>>();

                //map all the fields by key
                for (Map.Entry<String, JsonElement> mej : entry.entrySet()) {
                    map.put(mej.getKey(), mej);
                }

                widget = PXFParser.getWidgetFromType(map);

                if (widget == null || widget instanceof PXFUnknownControlType)
                    continue;

                widget.setJsonLevel(parent.getJsonLevel() + 1);
                widget.setJsonKeyParent(parent.getJsonEntries().get(PXWidget.FIELD_KEY)
                        .getValue().getAsString());
                wl.add(widget);
            }

            for(PXWidget w : PXFAdapter.this.lWidgets){
                index++;

                if(w.getJsonLevel() != parent.getJsonLevel()
                        || !w.getJsonEntries().containsKey(PXWidget.FIELD_KEY)
                        || !w.getJsonEntries().get(PXWidget.FIELD_KEY).equals(
                                parent.getJsonEntries().get(PXWidget.FIELD_KEY)))
                    continue;

                break;
            }

            if(index > -1){
                for(PXWidget w : wl){
                    PXFAdapter.this.lWidgets.add(++index, w);
                }
            }else{
                //what to do, what to do..
            }

            return size != PXFAdapter.this.lWidgets.size();
        }

        @Override
        public boolean removeChildWidgets(PXWidget parent) {
            final int level = parent.getJsonLevel() + 1;
            boolean removed = false;
            final int size = PXFAdapter.this.lWidgets.size();

            if(!parent.getJsonEntries().containsKey(PXWidget.FIELD_KEY))
                return false;

            for(int i = PXFAdapter.this.lWidgets.size() - 1; i >= 0; --i){
                PXWidget w = PXFAdapter.this.lWidgets.get(i);

                if(w.getJsonLevel() != level)
                    continue;

                if(!parent.getJsonEntries().get(PXWidget.FIELD_KEY).getValue().getAsString()
                        .equals(w.getJsonKeyParent()))
                    continue;

                removed = true;
                PXFAdapter.this.lWidgets.remove(i);
            }

            return size != PXFAdapter.this.lWidgets.size();
        }

        private boolean existControls(PXWidget parent){
            final int level = parent.getJsonLevel() + 1;

            if(!parent.getJsonEntries().containsKey(PXWidget.FIELD_KEY))
                return true;

            for(int i = PXFAdapter.this.lWidgets.size() - 1; i >= 0; --i){
                PXWidget w = PXFAdapter.this.lWidgets.get(i);

                if(w.getJsonLevel() != level)
                    continue;

                if(!parent.getJsonEntries().get(PXWidget.FIELD_KEY).getValue().getAsString()
                        .equals(w.getJsonKeyParent()))
                    continue;

                return true;
            }

            return false;
        }

        @Override
        public void notifyDataSetChanges() {
            PXFAdapter.this.notifyDataSetChanged();
        }
    };
}
