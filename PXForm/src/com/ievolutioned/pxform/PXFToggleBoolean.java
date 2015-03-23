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

        RadioGroup mRadioSwitch = new RadioGroup(context);
        mRadioSwitch.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        mRadioSwitch.setOrientation(LinearLayout.HORIZONTAL);
        mRadioSwitch.setLayoutParams(params);
        helper.radioGroup = mRadioSwitch;

        RadioButton mRadioButtonOn = new RadioButton(context);
        //mRadioButtonOn.setText(DEFAULT_TEXT_ON);
        //mRadioButtonOn.setId(0);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        mRadioButtonOn.setLayoutParams(params);
        mRadioButtonOn.setTag(1);
        helper.radioOn = mRadioButtonOn;

        RadioButton mRadioButtonOff = new RadioButton(context);
        //mRadioButtonOff.setText(DEFAULT_TEXT_OFF);
        //mRadioButtonOff.setId(1);
        params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        mRadioButtonOff.setLayoutParams(params);
        mRadioButtonOff.setTag(2);
        helper.radioOff = mRadioButtonOff;

        JsonArray array = getJsonEntries().get(FIELD_OPTIONS).getValue().getAsJsonArray();
        mRadioButtonOn.setText(array.get(0).getAsString());
        mRadioButtonOff.setText(array.get(1).getAsString());

        //mRadioSwitch.setOnCheckedChangeListener(radio_check);
        mRadioSwitch.addView(mRadioButtonOn);
        mRadioSwitch.addView(mRadioButtonOff);

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(mRadioSwitch);

        //add controls to main container
        v.addView(linear);

        return v;
    }
}
