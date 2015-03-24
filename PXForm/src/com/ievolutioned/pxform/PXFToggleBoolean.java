package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class PXFToggleBoolean extends PXWidget {

    public static class HelperToggleBoolean extends HelperWidget{
        protected TextView title;
        protected LinearLayout linearCheckBox;
        protected RadioGroup radioGroup;
        protected RadioButton radioOn;
        protected RadioButton radioOff;
    }

    public PXFToggleBoolean(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperToggleBoolean();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_TOGGLEBOOLEAN;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperToggleBoolean helper = (HelperToggleBoolean) view.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        JsonArray array = getJsonEntries().get(FIELD_OPTIONS).getValue().getAsJsonArray();
        helper.radioOn.setText(array.get(0).getAsString());
        helper.radioOff.setText(array.get(1).getAsString());

        //TODO: read json to know what the state of the radio buttons are
    }

    @Override
    protected View createControl(Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperToggleBoolean helper = (HelperToggleBoolean) v.getTag();

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

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setLayoutParams(params);
        helper.radioGroup = radioGroup;

        RadioButton radioOn = new RadioButton(context);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        radioOn.setLayoutParams(params);
        radioOn.setTag(1);
        helper.radioOn = radioOn;

        RadioButton radioOff = new RadioButton(context);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        radioOff.setLayoutParams(params);
        radioOff.setTag(2);
        helper.radioOff = radioOff;

        JsonArray array = getJsonEntries().get(FIELD_OPTIONS).getValue().getAsJsonArray();
        radioOn.setText(array.get(0).getAsString());
        radioOff.setText(array.get(1).getAsString());

        radioGroup.addView(radioOn);
        radioGroup.addView(radioOff);

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(radioGroup);

        //add controls to main container
        v.addView(linear);

        return v;
    }
}
