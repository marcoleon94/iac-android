package com.ievolutioned.pxform;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class PXFAdapter extends BaseAdapter {
    private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
    private JsonElement jseObject;
    private Activity aActivity;

    public PXFAdapter(Activity activity, List<PXWidget> widgets){
        lWidgets = widgets;
        aActivity = activity;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }



    private View getWidgetFromType(PXWidget widget){
        return widget.createControl(aActivity);
    }
}
