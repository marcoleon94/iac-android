package com.ievolutioned.pxform.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ievolutioned.pxform.PXWidget;
import com.ievolutioned.pxform.database.Values;
import com.ievolutioned.pxform.database.ValuesDataSet;

import java.io.IOException;
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
public class PXFAdapter extends BaseAdapter{
    private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
    private Activity aActivity;
    private AdapterEventHandler eventHandler;

    public interface AdapterEventHandler {
        void onClick(PXWidget widget);
        void openSubForm(String parentKey, String json, PXFAdapter adapter);
    }

    public interface  AdapterSaveHandler{
        void saved();
        void error(Exception ex);
    }

    public interface AdapterJSONHandler{
        void success(JsonElement jsonElement);
        void error(Exception ex);
    }

    public void setAdapterEventHandler(AdapterEventHandler callback){
        eventHandler = callback;
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

    /**
     * Save the items values to the data base
     */
    public void save(final long formID
            , final int level
            , final String parentKey
            , final AdapterSaveHandler callback){
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ValuesDataSet ValuesDS = new ValuesDataSet(aActivity);
                List<Values> valuesList;
                Runnable sleep = new Runnable() { @Override public void run() {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();

                        if(callback != null){
                            callback.error(e);
                        }
                    }
                }};

                //check if we have the data base ready
                valuesList = ValuesDS.selectByFormIDLevelParentKey(formID, level, parentKey);
                boolean exist = false;

                for(PXWidget widget : lWidgets){
                    exist = false;

                    for(Values value : valuesList){
                        if(widget.getKey().equals(value.getKey())){

                            if(widget.getValue() != null) {
                                value.setValue(widget.getValue());
                                //ValuesDS.update(value);
                                ValuesDS.updateValue(value);
                            }

                            exist = true;
                            break;
                        }
                    }

                    if(!exist){
                        ValuesDS.insert(formID, level, widget.getKey(), parentKey);
                    }

                    // let the thread rest for a bit
                    sleep.run();
                }

                try {
                    ValuesDS.importDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(callback != null){
                    callback.saved();
                }
                return null;
            }
        }).execute();
    }

    public void getJsonForm(final long formID
            , final int level
            , final String parentKey
            , final AdapterJSONHandler callback) {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (callback == null)
                    throw new RuntimeException("Callback must not be null");

                ValuesDataSet ValuesDS = new ValuesDataSet(aActivity);
                List<Values> valuesList;
                JsonObject json = null;

                //get data base values
                valuesList = ValuesDS.selectByFormIDLevelParentKey(formID, level, parentKey);
                if (valuesList == null)
                    callback.error(new NullPointerException("Value list is null"));

                json = new JsonObject();
                for (Values v : valuesList) {
                    if (v.getKey() != null && !v.getKey().isEmpty() &&
                            v.getValue() != null && !v.getValue().isEmpty()) {
                        json.addProperty(v.getKey(),v.getValue());
                    }
                }

                if (callback != null) {
                    callback.success(json);
                }
                return null;
            }
        }).execute();
    }

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

        @Override
        public void selectedSubForm(String json, PXWidget widget) {
            if(eventHandler != null){
                eventHandler.openSubForm(widget.getKey(), json, PXFAdapter.this);
            }
        }
    };
}