package com.ievolutioned.pxform;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Map;

/**
 *
 */
public class PXFSubMenuButton extends PXWidget {
    /**
     * The default place holder
     */
    public static final String PLACEHOLDER_DEFAULT = "Ninguno";

    private String current_title = "";
    private String current_option_text = "";
    private int current_option = -1;

    public static class HelperSubMenuButton extends HelperWidget {
        protected Button button;
        protected TextView title;
        protected LinearLayout linearButton;
    }

    public PXFSubMenuButton(Map<String, Map.Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperSubMenuButton();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_SUBMENUBUTTON;
    }

    @Override
    public void setWidgetData(View v) {
        super.setWidgetData(v);
        HelperSubMenuButton helper = (HelperSubMenuButton) v.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        helper.button.setText(getJsonEntries().containsKey(FIELD_PLACEHOLDER) ?
                getJsonEntries().get(FIELD_PLACEHOLDER).getValue().getAsString() : PLACEHOLDER_DEFAULT);
        helper.button.setOnClickListener(onclick);
    }

    @Override
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperSubMenuButton helper = (HelperSubMenuButton) v.getTag();

        //layout container
        LinearLayout linear = getGenericLinearLayout(context);
        linear.setWeightSum(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        helper.linearButton = linear;

        //field name
        TextView text = getGenericTextView(context, getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        text.setLayoutParams(params);
        helper.title = text;

        //sub button menu
        Button button = new Button(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        button.setLayoutParams(params);
        button.setText(getJsonEntries().containsKey(FIELD_PLACEHOLDER) ?
                getJsonEntries().get(FIELD_PLACEHOLDER).getValue().getAsString() : PLACEHOLDER_DEFAULT);
        button.setOnClickListener(onclick);
        helper.button = button;

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(button);

        //add controls to main container
        v.addView(linear);

        return v;
    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getEventHandler() != null){
                getEventHandler().onClick(PXFSubMenuButton.this);
            }
        }
    };
}
