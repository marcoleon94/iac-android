package com.ievolutioned.pxform;

import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PXFText extends PXWidget {

    private String current_text = "";

    public PXFText(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperText();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_TEXT;
    }

    @Override
    public void setValue(String value) {
        current_text = value;
    }

    @Override
    public String getValue() {
        return current_text == null ? "" : current_text;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperText helper = (HelperText) view.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        helper.inputText.setText(current_text);
    }

    @Override
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperText helper = (HelperText) v.getTag();

        //layout container
        LinearLayout linear = getGenericLinearLayout(context);
        linear.setWeightSum(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setPadding(0, 10, 0, 10);
        helper.linearText = linear;

        //field name
        TextView text = getGenericTextView(context, getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        text.setLayoutParams(params);
        helper.title = text;

        //edit input control
        TextCustom textCustom = new TextCustom(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        textCustom.setLayoutParams(params);

        helper.inputText = textCustom;

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(textCustom);

        //add controls to main container
        v.addView(linear);

        return v;
    }


    public String toString() {
        return this.current_text;
    }

    public static class HelperText extends HelperWidget {
        protected TextView title;
        protected TextCustom inputText;
        protected LinearLayout linearText;
    }

    public class TextCustom extends TextView {
        private List<TextWatcher> watcherList;

        public TextCustom(Context context) {
            super(context);
        }

        public TextCustom(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TextCustom(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void addTextChangedListener(TextWatcher watcher) {
            if (TextCustom.this.watcherList == null)
                TextCustom.this.watcherList = new ArrayList<TextWatcher>();

            TextCustom.this.watcherList.add(watcher);

            super.addTextChangedListener(watcher);
        }
    }

    @Override
    public boolean validate() {
        if(!getValue().isEmpty())
            return true;
        return false;
    }
}
