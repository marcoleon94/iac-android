package com.ievolutioned.pxform;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ievolutioned.pxform.database.Values;
import com.ievolutioned.pxform.database.ValuesDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PXFParser {
    private PXFParserEventHandler eventHandler;

    /**
     *
     */
    public interface PXFParserEventHandler {
        public abstract void finish(com.ievolutioned.pxform.adapters.PXFAdapter adapter, String json);

        public abstract void error(Exception ex, String json);
    }

    /**
     * @param callback
     */
    public PXFParser(PXFParserEventHandler callback) {
        eventHandler = callback;
    }

    /**
     * @param activity  Context used to create the controls and save data
     * @param json      String to be parse to controls
     * @param formID    Owner of the form
     * @param level     Indicate the order in the tree
     * @param parentKey Owner of the form if is a child in the tree
     */
    public void parseJson(final Activity activity,
                          final String json,
                          final long formID,
                          final int level,
                          final String parentKey) {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params1234) {
                JsonElement json_tmp;
                final List<PXWidget> widgetList = new ArrayList<PXWidget>();
                com.ievolutioned.pxform.database.ValuesDataSet ValuesDS = new ValuesDataSet(activity);
                List<Values> valuesList;

                Runnable sleep = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {
                    json_tmp = new JsonParser().parse(json);
                } catch (final Exception ex) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ex.printStackTrace();

                            if (eventHandler != null) {
                                eventHandler.error(ex, json);
                            }
                        }
                    });
                    return null;
                }

                if (json_tmp.isJsonNull()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (eventHandler != null) {
                                eventHandler.error(new Exception("Json is not valid"), json);
                            }
                        }
                    });
                    return null;
                }

                if (json_tmp.isJsonArray()) {
                    JsonArray array = json_tmp.getAsJsonArray();

                    for (int i = 0; i < array.size(); ++i) {
                        JsonElement element = array.get(i);

                        if (!element.isJsonObject())
                            continue;

                        JsonObject entry = element.getAsJsonObject();
                        final Map<String, Map.Entry<String, JsonElement>> map =
                                new HashMap<>();

                        //map all the fields by key
                        for (Map.Entry<String, JsonElement> mej : entry.entrySet()) {
                            map.put(mej.getKey(), mej);
                        }

                        PXWidget px = getWidgetFromType(map);
                        widgetList.add(px);

                        // let the thread rest for a bit
                        sleep.run();
                    }

                    //check if we have the data base ready
                    valuesList = ValuesDS.selectByFormIDLevelParentKey(formID, level, parentKey);
                    boolean exist = false;

                    for (PXWidget widget : widgetList) {
                        exist = false;

                        for (Values value : valuesList) {
                            if (widget.getKey().equals(value.getKey())) {

                                if (value.getValue() != null)
                                    widget.setValue(value.getValue());

                                exist = true;
                                break;
                            }
                        }

                        if (!exist) {
                            ValuesDS.insert(formID, level, widget.getKey(), parentKey);
                        }

                        // let the thread rest for a bit
                        sleep.run();
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (eventHandler != null) {
                                eventHandler.error(new Exception("Json is not valid"), json);
                            }
                        }
                    });
                    return null;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (eventHandler != null) {
                            com.ievolutioned.pxform.adapters.PXFAdapter adapter
                                    = new com.ievolutioned.pxform.adapters.PXFAdapter(activity, widgetList);
                            //adapter.setParcelJson(json);
                            eventHandler.finish(adapter, json);
                        }
                    }
                });

                return null;
            }
        }).execute();
    }

    public static String parseFileToString(Context context, String filename) {
        try {
            InputStream stream = context.getAssets().open(filename);
            int size = stream.available();

            byte[] bytes = new byte[size];
            stream.read(bytes);
            stream.close();

            return new String(bytes, "UTF-8");
        } catch (IOException e) {
        }
        return null;
    }

    public static PXWidget getWidgetFromType(final Map<String, Map.Entry<String, JsonElement>> map) {
        PXWidget widget = null;

        //we got a well defined field
        if (map.containsKey(PXWidget.FIELD_TYPE)) {
            if (map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
                    .equals(PXWidget.FIELD_TYPE_TEXT)) {
                widget = new PXFEdit(map);
            } else if (map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
                    .equals(PXWidget.FIELD_TYPE_BOOLEAN)) {
                widget = new PXFCheckBox(map);
            } else if (map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
                    .equals(PXWidget.FIELD_TYPE_DATE)) {
                widget = new PXFDatePicker(map);
            } else if (map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
                    .equals(PXWidget.FIELD_TYPE_LONGTEXT)) {
                widget = new PXFEdit(map);
            } else if (map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
                    .equals(PXWidget.FIELD_TYPE_UNSIGNED)) {
                widget = new PXFEdit(map);
            }
        } else if (map.containsKey(PXWidget.FIELD_OPTIONS)) {
            if (map.containsKey(PXWidget.FIELD_CELL) &&
                    isCELL_OPTION_SEGMENT(map.get(PXWidget.FIELD_CELL).getValue())) {
                widget = new PXFToggleBoolean(map);
            } else if (isSubForm(map.get(PXWidget.FIELD_OPTIONS).getValue())) {
                widget = new PXFSubMenuButton(map);
            } else {
                widget = new PXFSpinner(map);
            }
        } else if (map.containsKey(PXWidget.FIELD_ACTION)) {
            widget = new PXFButton(map);
        } else if (map.containsKey(PXWidget.FIELD_KEY)) {
            if (map.get(PXWidget.FIELD_KEY).getValue().getAsString()
                    .contains(PXWidget.FIELD_KEY_BARCODE)) {
                //This is an special case about a barcode reader
                widget = new PXFButton(map);
            }
            if (map.get(PXWidget.FIELD_KEY).getValue().getAsString()
                    .contains(PXWidget.FIELD_KEY_HEADER_EMPTY))
                widget = new PXFText(map);

        }

        return widget == null ? new PXFUnknownControlType(map) : widget;
    }

    private static boolean isCELL_OPTION_SEGMENT(JsonElement cell) {
        return PXWidget.FIELD_CELL_OPTION_SEGMENT.equalsIgnoreCase(cell.getAsString()) ||
                PXWidget.FIELD_CELL_OPTION_SEGMENT_CUSTOM.equalsIgnoreCase(cell.getAsString());
    }

    private static boolean isSubForm(JsonElement cell) {
        int index = -1;
        JsonArray array;
        JsonElement sub;

        if (!cell.isJsonArray())
            return false;

        array = cell.getAsJsonArray();

        for (int z = 0; z < array.size(); ++z) {
            if (!array.get(z).isJsonObject())
                continue;

            sub = array.get(z).getAsJsonObject();

            if (!sub.isJsonObject()
                    || sub.getAsJsonObject().entrySet().size() < 1
                    || !sub.getAsJsonObject().entrySet().iterator().hasNext()
                    || !sub.getAsJsonObject().entrySet().iterator().next().getValue().isJsonArray())
                continue;

            return true;
        }

        return false;
    }
}
