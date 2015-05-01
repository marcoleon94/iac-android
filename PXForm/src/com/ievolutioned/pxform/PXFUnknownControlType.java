package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFUnknownControlType extends PXWidget {

    public static class HelperUnKnownControl extends HelperWidget{
        protected TextView unknownText;
    }

    public PXFUnknownControlType(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperUnKnownControl();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_UNKNOWN;
    }

    @Override
    public void setValue(String value) {
    }
    @Override
    public String getValue() {
        return "";
    }

    @Override
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout)super.createControl(context);
        HelperUnKnownControl helper = (HelperUnKnownControl) v.getTag();

        TextView t = getGenericTextView(context, "Can't find appropriate control to assign");
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t.setBackgroundResource(android.R.color.black);
        t.setTextColor(Color.WHITE);
        t.setLayoutParams(par);
        helper.unknownText = t;

        //add controls to main container
        v.addView(t);

        return v;
    }
}
