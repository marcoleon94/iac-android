package com.ievolutioned.iac.pxform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PXFParser {
	private boolean bhasErrors = false;
	private JsonElement jseObject;
	private List<PXWidget> lWidgets = new ArrayList<PXWidget>();
	private PXFParserEventHandler eventHandler;
	//private Context cContext;
	private final Handler handler;
	Activity aActivity;

	public interface PXFParserEventHandler{
		public abstract void finish(PXFParser parser, String json);
		public abstract void error(Exception ex, String json);
	}

	public PXFParser(Activity activity, PXFParserEventHandler callback){
		handler = new Handler();
		//cContext = c;
		aActivity = activity;
		eventHandler = callback;
	}

	public List<PXWidget> getWidget(){
		return lWidgets;
	}
	public JsonElement getJsonElement(){
		return jseObject;
	}

	public boolean hasErrors(){
		return bhasErrors;
	}

	/**
	 * @param context
	 * @param json
	 * @return
	 */
	public void parseJson(final String json){
		bhasErrors = false;
		AsyncTask<Void, Void, Void> t1 = new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params1234) {
				JsonElement json_tmp = null;
				final List<PXWidget> w = new ArrayList<PXWidget>();

				try{
					json_tmp = new JsonParser().parse(json);
				}catch(final Exception ex){
					handler.post(new Runnable() { @Override public void run() {
						ex.printStackTrace();
						bhasErrors = true;

						if(eventHandler != null){
							eventHandler.error(ex, json);
						}
					}});
					return null;
				}

				if(json_tmp.isJsonNull()){
					handler.post(new Runnable() { @Override public void run() {
						bhasErrors = true;

						if(eventHandler != null){
							eventHandler.error(new Exception("Json is not valid"), json);
						}
					}});
					return null;
				}

				if(json_tmp.isJsonArray()){
					JsonArray array = json_tmp.getAsJsonArray();					

					for(int i = 0; i < array.size(); ++i){
						JsonElement element = array.get(i);

						if(!element.isJsonObject())
							continue;

						JsonObject entry = element.getAsJsonObject();
						final Map<String, Map.Entry<String,JsonElement>> map = 
								new HashMap<String, Map.Entry<String,JsonElement>>();

						//map all the fields by key
						for(Map.Entry<String,JsonElement> mej : entry.entrySet()){
							map.put(mej.getKey(), mej);
						}

						try{
							handler.post(new Runnable() { @Override public void run() {
								PXWidget px = getWidgetFromType(aActivity, map);
								w.add(px);
							}});
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}

					final JsonElement json_tmp_copy = json_tmp;
					handler.post(new Runnable() { @Override public void run() {
						jseObject = json_tmp_copy;
						lWidgets = w;

						if(eventHandler != null){
							eventHandler.finish(PXFParser.this, json);
						}

					}});
				}else{
					handler.post(new Runnable() {  @Override public void run() {
						bhasErrors = true;

						if(eventHandler != null){
							eventHandler.error(new Exception("Json is not valid"), json);
						}
					}});
				}

				return null;
			}
		};
		t1.execute();
	}

	public static String parseFileToString(Context context, String filename )
	{
		try
		{
			InputStream stream = context.getAssets().open( filename );
			int size = stream.available();

			byte[] bytes = new byte[size];
			stream.read(bytes);
			stream.close();

			return new String( bytes, "UTF-8" );

		} catch ( IOException e ) {
			Log.i("MakeMachine", "IOException: " + e.getMessage() );
		}
		return null;
	}

	private static PXWidget getWidgetFromType(final Activity context,
			final Map<String, Map.Entry<String,JsonElement>> map){
		PXWidget widget = null;

		//we got a well defined field
		if(map.containsKey(PXWidget.FIELD_TYPE)){
			if(map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
					.equals(PXWidget.FIELD_TYPE_TEXT)){
				widget = new PXFEdit(context, map);
			} 
			else if(map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
					.equals(PXWidget.FIELD_TYPE_BOOLEAN)){
				widget = new PXFCheckBox(context, map);
			} 
			else if(map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
					.equals(PXWidget.FIELD_TYPE_DATE)){
				widget = new PXFDatePicker(context, map);
			} 
			else if(map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
					.equals(PXWidget.FIELD_TYPE_LONGTEXT)){
				widget = new PXFEdit(context, map);
			} 
			else if(map.get(PXWidget.FIELD_TYPE).getValue().getAsString()
					.equals(PXWidget.FIELD_TYPE_UNSIGNED)){
				widget = new PXFEdit(context, map);				
			}
		} 
		else if(map.containsKey(PXWidget.FIELD_OPTIONS)){
			widget = new PXFSpinner(context, map);
		} 
		else if(map.containsKey(PXWidget.FIELD_ACTION)){
			widget = new PXFButton(context, map);
		}

		return widget == null ? new PXFUnknownControlType(context, map) : widget;
	}
}
