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
    public static final int ADAPTER_ITEM_TYPE_SUBMENUBUTTON = 7;

    private Map<String, Map.Entry<String,JsonElement>> eEntry;
    private String fieldKey = "";
    private PXWidgetHandler eventHandler = null;

    protected abstract HelperWidget generateHelperClass();
    public abstract int getAdapterItemType();

    public interface PXWidgetHandler{
        public void notifyDataSetChanges();
        public void onClick(PXWidget parent);
    }

    /**
     * Base class for helper list adapter item
     */
    public static abstract class HelperWidget{
        protected LinearLayout container;
        protected TextView headTextView;
    }

    public static int getAdapterItemTypeCount(){
        final Integer[] ids = new Integer[]{
                ADAPTER_ITEM_TYPE_UNKNOWN,
                ADAPTER_ITEM_TYPE_BUTTON,
                ADAPTER_ITEM_TYPE_CHECKBOX,
                ADAPTER_ITEM_TYPE_DATEPICKER,
                ADAPTER_ITEM_TYPE_EDIT,
                ADAPTER_ITEM_TYPE_SPINNER,
                ADAPTER_ITEM_TYPE_TOGGLEBOOLEAN,
                ADAPTER_ITEM_TYPE_SUBMENUBUTTON,
        };
        return ids.length;
    }

    public PXWidgetHandler getEventHandler() { return eventHandler; }
    public void setEventHandler(PXWidgetHandler callback){ eventHandler = callback; }
    /**
     * Get a LinearLayout view container, if the map contains a PXWidget.FIELD_HEADER
     *
     * @param context used to create the view
     * @return return a LinearLayout container
     */
    public View createControl(final Activity context){
        HelperWidget helper = generateHelperClass();
        LinearLayout linear = getGenericLinearLayout(context);

        android.widget.AbsListView.LayoutParams params =
                new android.widget.AbsListView.LayoutParams(
                        android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                        android.widget.AbsListView.LayoutParams.WRAP_CONTENT);

        linear.setLayoutParams(params);
        linear.setOrientation(LinearLayout.VERTICAL);

        TextView v = getTextViewHead(context, eEntry);

        linear.addView(v);

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
        t.setText(map.containsKey(FIELD_HEADER) ?
                map.get(FIELD_HEADER).getValue().getAsString() : "");
        ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t.setTextSize(20.0f);
        t.setGravity(Gravity.BOTTOM);
        t.setPadding(5, 80, 5, 5);
        t.setLayoutParams(par);
        t.setBackgroundColor(Color.parseColor("#DFDDE2"));
        t.setVisibility(map.containsKey(FIELD_HEADER) ?
                View.VISIBLE : View.GONE);
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
    public PXWidget(Map<String, Map.Entry<String,JsonElement>> entry){ eEntry = entry; }
    public Map<String, Map.Entry<String,JsonElement>> getJsonEntries(){ return eEntry; }
    public void setWidgetData(View view){
        HelperWidget helper = (HelperWidget) view.getTag();
        helper.headTextView.setText(getJsonEntries().containsKey(FIELD_HEADER) ?
                getJsonEntries().get(FIELD_HEADER).getValue().getAsString() : "");
        helper.headTextView.setVisibility(getJsonEntries().containsKey(FIELD_HEADER) ?
                View.VISIBLE : View.GONE);

        if (getJsonEntries().get(FIELD_KEY) != null)
            setKey(getJsonEntries().get(FIELD_KEY).getValue().toString());
    }

    public void setKey(String key){ fieldKey = key; }
    public String getKey(){ return fieldKey; }

    public String getWidgetDataString() {
        return this instanceof PXFUnknownControlType ? "" : toString();
    }
}
