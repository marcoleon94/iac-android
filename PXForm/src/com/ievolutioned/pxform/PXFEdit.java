package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFEdit extends PXWidget{

    public class HelperEdit extends HelperWidget{
        protected TextView title;
        protected EditText inputEdit;
        protected LinearLayout linearEdit;
    }

    public PXFEdit(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperEdit();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_EDIT;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperEdit helper = (HelperEdit) view.getTag();

        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        editConfigureTypeOfInput(helper.inputEdit, getJsonEntries());

        //TODO: read json to know what the state of the EditText is
        //helper.inputEdit.setText( );
    }

    @Override
    public View createControl(Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperEdit helper = (HelperEdit) v.getTag();

        //layout container
        LinearLayout linear = getGenericLinearLayout(context);
        linear.setWeightSum(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        helper.linearEdit = linear;

        //field name
        TextView text = getGenericTextView(context, getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        text.setLayoutParams(params);
        helper.title = text;

        //edit input control
        EditText edit = new EditText(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        edit.setLayoutParams(params);
        editConfigureTypeOfInput(edit, getJsonEntries());

        helper.inputEdit = edit;

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(edit);

        //add controls to main container
        v.addView(linear);

        return v;
    }

    private void editConfigureTypeOfInput(final EditText edit,
                                          final Map<String, Map.Entry<String,JsonElement>> map){
        int maxsize = 50;

        if(FIELD_TYPE_LONGTEXT.equals(map.get(FIELD_TYPE).getValue().getAsString())){
            try{
                maxsize = Integer.parseInt(map.get(FIELD_TYPE).getValue().getAsString());
            }catch(Exception ex){
            }
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxsize);

        if(FIELD_TYPE_UNSIGNED.equals(map.get(FIELD_TYPE).getValue().getAsString())){
            edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else{
            edit.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        }

        edit.setFilters(FilterArray);
        edit.setLines(1);

        if(getJsonEntries().containsKey(FIELD_PLACEHOLDER)){
            edit.setHint(getJsonEntries().get(FIELD_PLACEHOLDER).getValue().getAsString());
        }
    }
}
