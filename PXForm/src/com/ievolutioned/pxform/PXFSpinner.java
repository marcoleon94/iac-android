package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class PXFSpinner extends PXWidget {


    public static class HelperSpinner extends HelperWidget{
        protected TextView title;
        protected LinearLayout linearCheckBox;
        protected Spinner spinner;
    }

    public PXFSpinner(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperSpinner();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_SPINNER;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperSpinner helper = (HelperSpinner) view.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        helper.spinner.setAdapter(getSpinnerAdapter(view.getContext()));
        //TODO: read json to know what the state of the spinner is

    }

    @Override
    protected View createControl(Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperSpinner helper = (HelperSpinner) v.getTag();

        //layout container
        LinearLayout linear = getGenericLinearLayout(context);
        linear.setWeightSum(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        helper.linearCheckBox = linear;

        //field name
        TextView text = getGenericTextView(context, getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        text.setLayoutParams(params);
        helper.title = text;

        //initial spinner configuration
        Spinner spinner = new Spinner(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        spinner.setLayoutParams(params);
        spinner.setAdapter(getSpinnerAdapter(context));
        spinner.setSelection(0);
        helper.spinner = spinner;

        //add controls to linear parent before main control
        linear.addView(text);
        linear.addView(spinner);

        //add controls to main container
        v.addView(linear);

        return v;
    }

    private ArrayAdapter<String> getSpinnerAdapter(Context context){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item );

        //add fields to the adapter
        if(getJsonEntries().get(FIELD_OPTIONS).getValue().isJsonArray()){
            JsonArray array = getJsonEntries().get(FIELD_OPTIONS).getValue().getAsJsonArray();

            for(int x = 0; x < array.size(); ++x){
                final String s = array.get(x).getAsString();
                adapter.add(s);
            }
        }

        return adapter;
    }
}
