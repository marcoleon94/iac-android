package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.google.zxing.integration.android.IntentIntegrator;

public class PXFButton extends PXWidget {

    public class HelperButton extends HelperWidget{
        protected Button button;
    }

    public PXFButton(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperButton h = (HelperButton) view.getTag();
        h.button.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
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
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout)super.createControl(context);
        HelperButton helper = (HelperButton) v.getTag();

        Button button = new Button(context);
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(par);
        button.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        helper.button = button;

        //action
        if (getJsonEntries().containsKey(FIELD_ACTION))
            setButtonAction(button, context , getJsonEntries().get(FIELD_ACTION).getValue().getAsString());

        //add controls to main container
        v.addView(button);

        return v;
    }

    private void setButtonAction(final Button b, final Activity context, final String action) {
        if (b == null)
            return;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Validate action
                openCamera(context);
            }
        });
    }

    private void openCamera(Activity context) {
        //TODO: Arguments
        IntentIntegrator scanIntegrator = new IntentIntegrator(context);
        scanIntegrator.initiateScan();
    }
}
