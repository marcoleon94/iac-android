package com.ievolutioned.pxform.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ievolutioned.pxform.PXWidget;
import com.ievolutioned.pxform.database.Values;
import com.ievolutioned.pxform.database.ValuesDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PXFAdapter adapter class, manages a set of PXWidget widgets and their event handlers
 */
public class PXFAdapter extends BaseAdapter {
    /**
     * A List of PXWidget widgets that are used as data source
     */
    private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
    /**
     * The main context of the activity
     */
    private Activity aActivity;
    /**
     * The main AdapterEventHandler handler of this adapter
     */
    private AdapterEventHandler eventHandler;

    /**
     * Handles the events on the adapter
     */
    public interface AdapterEventHandler {
        /**
         * Manages all on click events of each PXWidget widget
         *
         * @param widget
         */
        void onClick(PXWidget widget);

        /**
         * Instantiates a new subform with the current parameters
         *
         * @param parentKey - The parent key id
         * @param json      - the JSON of this form
         * @param adapter   - the current PXFAdapter adapter
         */
        void openSubForm(String parentKey, String json, PXFAdapter adapter);
    }

    /**
     * Handles the database events on the adapter
     */
    public interface AdapterSaveHandler {
        /**
         * Data set saved callback
         */
        void saved();

        /**
         * Error callback
         *
         * @param ex
         */
        void error(Exception ex);
    }

    /**
     * Handles the JSON events on the adapter
     */
    public interface AdapterJSONHandler {
        /**
         * Returns a JsonElement object that should contain
         *
         * @param jsonElement
         */
        void success(JsonElement jsonElement);

        /**
         * Error callback
         *
         * @param ex
         */
        void error(Exception ex);
    }

    /**
     * Sets the AdapterEventHandler event handler
     *
     * @param callback - AdapterEventHandler callback
     */
    public void setAdapterEventHandler(AdapterEventHandler callback) {
        eventHandler = callback;
    }

    /**
     * Instantiates a PXFAdapter with the current parameters
     *
     * @param activity - the current context
     * @param widgets  - the set of widgets as data source
     */
    public PXFAdapter(Activity activity, List<PXWidget> widgets) {
        lWidgets = widgets;
        aActivity = activity;
    }

    /**
     * Sets the main context
     *
     * @param activity - Activity activity context
     */
    public void setActivity(Activity activity) {
        aActivity = activity;
    }

    /**
     * Gets the list og PXWidget widgets
     *
     * @return
     */
    public List<PXWidget> getItems() {
        return lWidgets;
    }

    @Override
    public int getCount() {
        return lWidgets.size();
    }

    @Override
    public int getItemViewType(int position) {
        return lWidgets.get(position).getAdapterItemType();
    }

    @Override
    public int getViewTypeCount() {
        return PXWidget.getAdapterItemTypeCount();
    }

    @Override
    public PXWidget getItem(int position) {
        return lWidgets.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup group) {
        final PXWidget w = getItem(pos);

        try {
            if (view == null) {
                view = w.createControl(aActivity);
            }
        } catch (Exception e) {
        }

        w.setEventHandler(widgetHandler);
        w.setWidgetData(view);

        return view;
    }

    /**
     * Validate if the current adapter has required members
     *
     * @param listView - Must be the same
     * @return the title of the widget that is required, string empty otherwise
     */
    public String validate(ListView listView) {
        //TODO: shoud be this on background?
        String title = null;
        int i = 0;
        for (i = 0; i < lWidgets.size(); i++) {
            //Is it required?
            if (lWidgets.get(i).isValidate()) {
                lWidgets.get(i).setValidation(false);
                // Is it not valid?
                if (!lWidgets.get(i).validate()) {
                    //Focus control
                    lWidgets.get(i).setValidation(true);
                    //Get title
                    if (lWidgets.get(i).getJsonEntries().containsKey("title"))
                        title = lWidgets.get(i).getJsonEntries().get("title").getValue().getAsString();
                    else
                        title = "";
                    break;
                }
            }
        }
        notifyDataSetChanged();
        if (i > 0 && i < listView.getCount())
            listView.setSelection(i);
        return title;
    }

    /**
     * Gets a item value for key
     *
     * @param key - the key
     * @return the value of the key field, or null
     */
    public String getItemValueForKey(String key) {
        for (PXWidget w : lWidgets) {
            if (w.getJsonEntries().containsKey("key") &&
                    w.getJsonEntries().get("key").getValue().getAsString().contentEquals(key))
                return w.getValue();
        }
        return null;
    }

    /**
     * Saves the items values to the data base on a background thread
     *
     * @param formID    - Form ID
     * @param level     - Level for current form or sub-form
     * @param parentKey - key for parent
     * @param callback  - AdapterSaveHandler callback
     */
    public void save(final long formID
            , final int level
            , final String parentKey
            , final AdapterSaveHandler callback) {
        (new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                if (callback == null)
                    throw new NullPointerException("Callback must not be null");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return saveToDB(formID, level, parentKey);
            }

            @Override
            protected void onPostExecute(Boolean executed) {
                if (executed)
                    callback.saved();
                else
                    callback.error(new RuntimeException("Error on doInBackgorund"));
            }
        }).execute();
    }

    /**
     * Saves the sate to DB on main thread
     *
     * @param formID    - Form ID
     * @param level     - Level for current form or sub-form
     * @param parentKey - key for parent
     * @return true if it was saved, false otherwise
     */
    public boolean saveState(final long formID, final int level, final String parentKey) {
        return saveToDB(formID, level, parentKey);
    }

    /**
     * Saves the data to DB
     *
     * @param formID    - Form ID
     * @param level     - Level for current form or sub-form
     * @param parentKey - key for parent
     * @return true if it was saved, false otherwise
     */
    private boolean saveToDB(final long formID, final int level, final String parentKey) {
        ValuesDataSet ValuesDS = new ValuesDataSet(aActivity);
        List<Values> valuesList;
        //check if we have the data base ready
        valuesList = ValuesDS.selectByFormIDLevelParentKey(formID, level, parentKey);
        boolean exist = false;

        for (PXWidget widget : lWidgets) {
            exist = false;

            for (Values value : valuesList) {
                if (widget.getKey().equals(value.getKey())) {

                    if (widget.getValue() != null) {
                        value.setValue(widget.getValue());
                        ValuesDS.updateValue(value);
                    }

                    exist = true;
                    break;
                }
            }

            if (!exist) {
                ValuesDS.insert(formID, level, widget.getKey(), parentKey);
            }
        }

        try {
            ValuesDS.importDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Gets the json form as user response
     *
     * @param formID
     * @param callback
     */
    public void getJsonForm(final long formID
            , final AdapterJSONHandler callback) {
        (new AsyncTask<Void, Void, JsonElement>() {
            @Override
            protected void onPreExecute() {
                if (callback == null)
                    throw new NullPointerException("Callback must not be null");
            }

            @Override
            protected JsonElement doInBackground(Void... params) {
                if (callback == null)
                    throw new RuntimeException("Callback must not be null");

                ValuesDataSet ValuesDS = new ValuesDataSet(aActivity);
                List<Values> valuesList;
                JsonObject json = null;

                //get data base values
                valuesList = ValuesDS.selectByFormID(formID);
                if (valuesList == null)
                    callback.error(new NullPointerException("Value list is null"));

                json = new JsonObject();
                for (Values v : valuesList) {
                    if (v.getKey() != null && !v.getKey().isEmpty() &&
                            v.getValue() != null && !v.getValue().isEmpty()) {
                        json.addProperty(v.getKey(), v.getValue());
                    }
                }
                return json;
            }

            @Override
            protected void onPostExecute(JsonElement json) {
                if (json != null) {
                    JsonObject userResponse = new JsonObject();
                    userResponse.add("response", json);
                    callback.success(userResponse);
                } else
                    callback.error(new RuntimeException("Value list is not available"));
            }
        }).execute();
    }

    /**
     * Widget handler that notifies any data set changed, click or subform event.
     */
    private PXWidget.PXWidgetHandler widgetHandler = new PXWidget.PXWidgetHandler() {
        @Override
        public void notifyDataSetChanges() {
            PXFAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onClick(PXWidget parent) {
            if (eventHandler != null) {
                eventHandler.onClick(parent);
            }
        }

        @Override
        public void selectedSubForm(String json, PXWidget widget) {
            if (eventHandler != null) {
                eventHandler.openSubForm(widget.getKey(), json, PXFAdapter.this);
            }
        }
    };
}