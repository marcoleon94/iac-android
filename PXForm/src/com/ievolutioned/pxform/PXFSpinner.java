package com.ievolutioned.pxform;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Map;

public class PXFSpinner extends PXWidget {
    //public static final int VALUE_DEFAULT = -1;
    //--------------------------------------
    // variable declarations
    private ArrayAdapter<String> adapter;
    //private int last_position = VALUE_DEFAULT;
    private String value = null;

    /**
     * class and interface declaration
     */
    public static class HelperSpinner extends HelperWidget{
        protected TextView title;
        protected LinearLayout linearCheckBox;
        protected Spinner spinner;
    }

    //----------------------------------
    //actual code implementation
    public PXFSpinner(Map<String, Map.Entry<String, JsonElement>> entry) {
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
    public void setValue(String value) {
        try{
            //last_position = Integer.parseInt(value);
            this.value = value;
        }catch(Exception ex){
            //last_position = VALUE_DEFAULT;
            this.value = null;
        }
    }
    @Override
    public String getValue() {
        //return last_position == VALUE_DEFAULT?"":String.valueOf(last_position);
        return value;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperSpinner helper = (HelperSpinner) view.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        if(adapter == null){
            adapter = getSpinnerAdapter(view.getContext());
        }

        helper.spinner.setOnItemSelectedListener(spinner_itemSelected);
        helper.spinner.setAdapter(adapter);

        if (value != null) {
            int position = adapter.getPosition(value);
            helper.spinner.setSelection(position < adapter.getCount() ? position : 0);
        }
    }


    @Override
    public View createControl(Activity context) {
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
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
        spinner.setLayoutParams(params);
        adapter = getSpinnerAdapter(context);
        spinner.setAdapter(adapter);

        if(value == null && adapter.getCount()>0)
            spinner.setSelection(0);

        helper.spinner = spinner;
        spinner.setOnItemSelectedListener(spinner_itemSelected);

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
                JsonElement sub = array.get(x);
                String s;

                if(sub.isJsonPrimitive()){
                    s = sub.getAsString();
                }else if(sub.isJsonObject()) {
                    // get the array name only
                    Map.Entry<String,JsonElement> mej =
                            sub.getAsJsonObject().entrySet().iterator().next();
                    s = mej.getKey();
                } else {
                    s = "Unknown";
                }

                adapter.add(s);
            }
        }

        return adapter;
    }

    private AdapterView.OnItemSelectedListener
            spinner_itemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //booleans used to optimize the adapter notifyDataSetChanges() calls
            //boolean changed_1 = false;
            //boolean changed_2 = false;

            //if(PXFSpinner.this.last_position != position){
            //    if(getEventHandler() != null){
            //        changed_1 = getEventHandler().removeChildWidgets(PXFSpinner.this);
            //    }
            //}
            //
            //if(PXFSpinner.this.last_position != position) {
            //    if (PXFSpinner.this.getEventHandler() != null) {
            //        changed_2 = PXFSpinner.this.getEventHandler().addChildWidgets(
            //                PXFSpinner.this, position);
            //    }
            //}

            //PXFSpinner.this.last_position = getSpinnerAdapter(view.getContext()).getItem(position);
            if(adapter != null)
                PXFSpinner.this.value = adapter.getItem(position);


            //if((changed_1 || changed_2) && PXFSpinner.this.getEventHandler() != null){
            //    PXFSpinner.this.getEventHandler().notifyDataSetChanges();
            //}
        }

        @Override public void onNothingSelected(AdapterView<?> parent) { }
    };

    @Override
    public boolean validate() {
        if(value != null)
            return true;
        return false;
    }

    @Override
    public String toString() {
        try {
            return adapter == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }
}
