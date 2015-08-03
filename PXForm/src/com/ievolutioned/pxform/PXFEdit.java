package com.ievolutioned.pxform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFEdit extends PXWidget{

    private String current_text = "";

    public static class HelperEdit extends HelperWidget{
        protected TextView title;
        protected EditTextCustom inputEdit;
        protected LinearLayout linearEdit;
    }

    public class EditTextCustom extends EditText{
        private List<TextWatcher> watcherList;

        public EditTextCustom(Context context) { super(context); }
        public EditTextCustom(Context context, AttributeSet attrs) { super(context, attrs); }
        public EditTextCustom(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void addTextChangedListener(TextWatcher watcher) {
            if(EditTextCustom.this.watcherList == null)
                EditTextCustom.this.watcherList = new ArrayList<TextWatcher>();

            EditTextCustom.this.watcherList.add(watcher);

            super.addTextChangedListener(watcher);
        }

        public void removeAllTextChangedListener(){
            if(EditTextCustom.this.watcherList != null){

                for(TextWatcher t: EditTextCustom.this.watcherList){
                    EditTextCustom.this.removeTextChangedListener(t);
                }

                EditTextCustom.this.watcherList.clear();
            }
        }
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
        try {
            HelperEdit helper = (HelperEdit) view.getTag();


        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        editConfigureTypeOfInput(helper.inputEdit, getJsonEntries());
        helper.inputEdit.removeAllTextChangedListener();
        helper.inputEdit.addTextChangedListener(edit_watcher);
        helper.inputEdit.setText(current_text);
        }catch(Exception e){
            Log.e("WHAT?", e.getMessage(), e);
        }
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
        EditTextCustom edit = new EditTextCustom(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        edit.setLayoutParams(params);
        editConfigureTypeOfInput(edit, getJsonEntries());
        edit.addTextChangedListener(edit_watcher);
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

    private TextWatcher edit_watcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            current_text = s == null ? "" : s.toString();
        }
    };

    public String toString(){
        return this.current_text;
    }
}
