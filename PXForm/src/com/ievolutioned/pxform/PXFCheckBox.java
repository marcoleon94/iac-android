package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFCheckBox extends PXWidget {

    public static class HelperCheckBox extends HelperWidget{
        protected TextView title;
        protected LinearLayout linearCheckBox;
        protected CheckBox checkBox;
    }

    public PXFCheckBox(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperCheckBox();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_CHECKBOX;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperCheckBox helper = (HelperCheckBox) view.getTag();
        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        //TODO: read json to know what the state of the checkbox is
        //helper.checkBox.setChecked( );
    }

    @Override
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout)super.createControl(context);
        HelperCheckBox helper = (HelperCheckBox) v.getTag();

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

        //check box control
        CheckBox box = new CheckBox(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        box.setLayoutParams(params);
        helper.checkBox = box;

        //add controls to linear parent before main control
        linear.addView(text);
        linear.addView(box);

        //add controls to main container
        v.addView(linear);

        return v;
    }
}
