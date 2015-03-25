package com.ievolutioned.pxform;

import java.util.Map;

import com.google.gson.JsonElement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class PXWidget {
    public static final String FIELD_HEADER      = "header"     ;
    public static final String FIELD_KEY         = "key"        ;
    public static final String FIELD_TITLE       = "title"      ;
    public static final String FIELD_ACTION      = "action"     ;
    public static final String FIELD_TYPE        = "type"       ;
    public static final String FIELD_PLACEHOLDER = "placeholder";
    public static final String FIELD_VALIDATE    = "validate"   ;
    public static final String FIELD_OPTIONS     = "options"    ;
    public static final String FIELD_CELL        = "cell"       ;
    public static final String FIELD_CLASS       = "class"      ;

    public static final String FIELD_TYPE_TEXT     = "text"    ;
    public static final String FIELD_TYPE_BOOLEAN  = "boolean" ;
    public static final String FIELD_TYPE_DATE     = "date"    ;
    public static final String FIELD_TYPE_LONGTEXT = "longtext";
    public static final String FIELD_TYPE_UNSIGNED = "unsigned";

    public static final String FIELD_CELL_OPTION_SEGMENT = "FXFormOptionSegmentsCell";
    public static final String FIELD_CELL_OPTION_SEGMENT_CUSTOM = "FXFormOptionSegmentsCellCustom";

    public static final int ADAPTER_ITEM_TYPE_UNKNOWN = 0;
    public static final int ADAPTER_ITEM_TYPE_BUTTON = 1;
    public static final int ADAPTER_ITEM_TYPE_CHECKBOX = 2;
    public static final int ADAPTER_ITEM_TYPE_DATEPICKER = 3;
    public static final int ADAPTER_ITEM_TYPE_EDIT = 4;
    public static final int ADAPTER_ITEM_TYPE_SPINNER = 5;
    public static final int ADAPTER_ITEM_TYPE_TOGGLEBOOLEAN = 6;

    private Map<String, Map.Entry<String,JsonElement>> eEntry;

    public static abstract class HelperWidget{
        protected LinearLayout container;
        protected TextView headTextView;
    }

    protected abstract HelperWidget generateHelperClass();
    public abstract int getAdapterItemType();
    //public abstract String getJsonResult();

    public static int getAdapterItemTypeCount(){
        final Integer[] ids = new Integer[]{
                ADAPTER_ITEM_TYPE_UNKNOWN,
                ADAPTER_ITEM_TYPE_BUTTON,
                ADAPTER_ITEM_TYPE_CHECKBOX,
                ADAPTER_ITEM_TYPE_DATEPICKER,
                ADAPTER_ITEM_TYPE_EDIT,
                ADAPTER_ITEM_TYPE_SPINNER,
                ADAPTER_ITEM_TYPE_TOGGLEBOOLEAN,
        };
        return ids.length;
    }
    /**
     * Get a LinearLayout view container, if the map contains a PXWidget.FIELD_HEADER
     *
     * @param context
     * @return
     */
    protected View createControl(final Activity context){
        HelperWidget helper = generateHelperClass();
        LinearLayout linear = getGenericLinearLayout(context);

        android.widget.AbsListView.LayoutParams params =
                new android.widget.AbsListView.LayoutParams(
                        android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                        android.widget.AbsListView.LayoutParams.WRAP_CONTENT);

        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        //        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.VERTICAL);
        TextView v = null;

        if(eEntry.containsKey(PXWidget.FIELD_HEADER)){
            v = getTextViewHead(context, eEntry);
        }

        if(v != null){
            linear.addView(v);
        }

        helper.container = linear;
        helper.headTextView = v;
        linear.setTag(helper);

        return linear;
    }

    /**
     */
    private TextView getTextViewHead(Context context,
                                     Map<String, Map.Entry<String,JsonElement>> map){
        TextView t = new TextView(context);
        t.setText(map.get(FIELD_HEADER).getValue().getAsString());
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t.setTextSize(20.0f);
        t.setGravity(Gravity.BOTTOM);
        t.setPadding(5, 80, 5, 5);
        t.setLayoutParams(par);
        t.setBackgroundColor(Color.parseColor("#DFDDE2"));
        return t;
    }

    protected TextView getGenericTextView(Context context, String text){
        TextView t = new TextView(context);
        t.setText(text);
        t.setPadding(5, 2, 2, 5);
        return t;
    }

    protected LinearLayout getGenericLinearLayout(Context context){
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);
        l.setBackgroundColor(Color.parseColor("#ECEAEC"));
        return l;
    }

    public PXWidget(Map<String, Map.Entry<String,JsonElement>> entry){
        eEntry = entry;
    }

    public Map<String, Map.Entry<String,JsonElement>> getJsonEntries(){
        return eEntry;
    }

    public void setWidgetData(View view){
        HelperWidget helper = (HelperWidget) view.getTag();

        if(helper.headTextView != null && eEntry.containsKey(PXWidget.FIELD_HEADER)){
            helper.headTextView.setText(eEntry.get(FIELD_HEADER).getValue().getAsString());
        }
    }
}
