package com.ievolutioned.pxform;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Map;
import java.util.Map.Entry;

public class PXFButton extends PXWidget {

    public static final String ACTION_NONE = "NONE";
    public static final String ACTION_OPEN_CAMERA ="openCamera:";
    public static final String ACTION_SUBMIT = "submitRegistrationForm:";
    public static final String ACTION_BACK_ROOT = "automaticBackRoot";

    private String title;

    private Activity contextActivity;

    public static class HelperButton extends HelperWidget{
        protected Button button;
    }

    public PXFButton(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperButton h = (HelperButton) view.getTag();
        title = getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ";
        h.button.setText(title);
        h.button.setOnClickListener(onclick);
    }
    @Override
    protected HelperButton generateHelperClass() {
        return new HelperButton();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_BUTTON;
    }

    @Override
    public void setValue(String value) {
        try {
            JsonElement v = new JsonParser().parse(value);
            getJsonEntries().get(PXFButton.FIELD_TITLE).setValue(v);
        }catch (Exception e) {
            Log.e(PXFButton.class.getName(), e.getMessage());
        }
    }

    @Override
    public String getValue() {
        if (getJsonEntries().containsKey(PXFButton.FIELD_TITLE)) {
            String jTitle = getJsonEntries().get(PXFButton.FIELD_TITLE).getValue().getAsString();
            if (!title.contentEquals(jTitle))
                return getJsonEntries().get(PXFButton.FIELD_TITLE).getValue().getAsString();
        }
        return "";
    }
    @Override
    public View createControl(Activity context) {
        contextActivity = context;

        LinearLayout v = (LinearLayout)super.createControl(context);
        HelperButton helper = (HelperButton) v.getTag();

        Button button = new Button(context);
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(par);
        button.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        button.setOnClickListener(onclick);
        helper.button = button;

        //add controls to main container
        v.addView(button);

        return v;
    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getEventHandler() != null) {
                getEventHandler().onClick(PXFButton.this);
            }
            IntentIntegrator scanIntegrator = new IntentIntegrator(contextActivity);
            scanIntegrator.initiateScan();
        }
    };
}
