package com.ievolutioned.pxform;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Map;

/**
 * Class base for read Json elements
 */
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
    public static final String FIELD_KEY_BARCODE = "barcodeReader";
    public static final String FIELD_KEY_HEADER_EMPTY = "headerEmpty";

    public static final String FIELD_VALIDATE_TRUE = "yes";

    public static final int ADAPTER_ITEM_TYPE_UNKNOWN = 0;
    public static final int ADAPTER_ITEM_TYPE_BUTTON = 1;
    public static final int ADAPTER_ITEM_TYPE_CHECKBOX = 2;
    public static final int ADAPTER_ITEM_TYPE_DATEPICKER = 3;
    public static final int ADAPTER_ITEM_TYPE_EDIT = 4;
    public static final int ADAPTER_ITEM_TYPE_SPINNER = 5;
    public static final int ADAPTER_ITEM_TYPE_TOGGLEBOOLEAN = 6;
    public static final int ADAPTER_ITEM_TYPE_SUBMENUBUTTON = 7;
    public static final int ADAPTER_ITEM_TYPE_TEXT = 8;

    private Map<String, Map.Entry<String,JsonElement>> eEntry;
    private String fieldKey = "";
    private long _ID = 0;
    private PXWidgetHandler eventHandler = null;
    private boolean validate = false;

    /**
     * Hook to call when an event fire
     */
    public interface PXWidgetHandler{
        public void notifyDataSetChanges();
        public void onClick(PXWidget parent);
        public void selectedSubForm(String json, PXWidget widget);
    }

    /**
     * Base class for helper list adapter item
     */
    public static abstract class HelperWidget{
        protected LinearLayout container;
        protected TextView headTextView;
    }

    /**
     * This return the total number of available controls
     * @return Count of controls
     */
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
                ADAPTER_ITEM_TYPE_TEXT
        };
        return ids.length;
    }

    /**
     *
     * @return
     */
    protected abstract HelperWidget generateHelperClass();
    /**
     *
     * @return
     */
    public abstract int getAdapterItemType();
    /**
     * Set the value of the control, normally from the data base
     * @param value The widget will try to parse and set the value
     */
    public abstract void setValue(String value);
    public abstract String getValue();
    /**
     * Default base constructor, set the json element property and key property
     * @param entry A list of KEY - JSON values
     */
    public PXWidget(Map<String, Map.Entry<String,JsonElement>> entry){
        eEntry = entry;

        if(eEntry.containsKey(FIELD_KEY))
            setKey(eEntry.get(FIELD_KEY).getValue().getAsString());
        else
            Log.w(PXWidget.class.getName(), FIELD_KEY + " not found in json");
    }
    /**
     * Get the event handler associated to the class
     * @return {@link PXWidget.PXWidgetHandler} class
     */
    public PXWidgetHandler getEventHandler() { return eventHandler; }
    /**
     * Set the event handler associated to the class
     * @param callback Hook to call when an event fire
     */
    public void setEventHandler(PXWidgetHandler callback){ eventHandler = callback; }
    /**
     * Get a LinearLayout view container, if the map contains a {@link PXWidget#FIELD_HEADER}
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

    public boolean isValidate() {
        if (getJsonEntries().containsKey(FIELD_VALIDATE))
            if (getJsonEntries().get(FIELD_VALIDATE).getValue() != null &&
                    !getJsonEntries().get(FIELD_VALIDATE).getValue().isJsonNull() &&
                    getJsonEntries().get(FIELD_VALIDATE).getValue().getAsString()
                            .contentEquals(FIELD_VALIDATE_TRUE))
                return true;
        return false;
    }

    public abstract boolean validate();

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
    /**
     * Get a map with a String (key) - Json (class) values
     * @return {@link java.util.Map}
     */
    public Map<String, Map.Entry<String,JsonElement>> getJsonEntries(){ return eEntry; }
    /**
     * Used by the adapter to fill the information of the widget, extended widget should call
     * this super method.
     * @param view A container with already created controls
     */
    public void setWidgetData(View view){
        HelperWidget helper = (HelperWidget) view.getTag();
        helper.headTextView.setText(getJsonEntries().containsKey(FIELD_HEADER) ?
                getJsonEntries().get(FIELD_HEADER).getValue().getAsString() : "");
        helper.headTextView.setVisibility(getJsonEntries().containsKey(FIELD_HEADER) ?
                View.VISIBLE : View.GONE);

        if (getJsonEntries().get(FIELD_KEY) != null)
            setKey(getJsonEntries().get(FIELD_KEY).getValue().getAsString());
    }
    /**
     * Set the ID of the field and should be unique at the same level of the form, no validation
     * is perform for repeated IDs. Nested forms can use the same ID.
     * @param key An id that should be unique
     */
    public void setKey(String key){ fieldKey = key; }
    /**
     * Set the unique ID from the data base
     * @param id Unique data base ID
     */
    public void setID(long id){ _ID = id; }
    /**
     * Get the unique ID from the data base
     * @return Unique data base ID
     */
    public long getID() { return _ID; }
    /**
     * The {@link PXWidget#FIELD_KEY} value, the ID of the field and should
     * unique at the same level of the form, still <b>nested forms can have the same ID</b>.
     * @return String with the FIELD_KEY found
     */
    public String getKey(){ return fieldKey; }
}
