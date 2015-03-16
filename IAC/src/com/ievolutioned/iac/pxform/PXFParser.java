package com.ievolutioned.iac.pxform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
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
	private Context cContext;

	public interface PXFParserEventHandler{
		public abstract void finish(PXFParser parser, String json);
		public abstract void error(Exception ex, String json);
	}
	
	public PXFParser(Context c, PXFParserEventHandler callback){
		cContext = c;
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
	 * 
	 * @param context
	 * @param json
	 * @return
	 */
	public void parseJson(final String json){
		bhasErrors = false;
		JsonElement el;

		//AsyncTask<Void, Void, Long> t1 = new AsyncTask<Void, Void, Long>(){
		//	@Override
		//	protected Long doInBackground(Void... params) {
		//		// TODO Auto-generated method stub
		//		return null;
		//	}			
		//}; 
		
		try{
			el = new JsonParser().parse(json);
		}catch(Exception ex){
			ex.printStackTrace();
			bhasErrors = true;

			if(eventHandler != null){
				eventHandler.error(ex, json);
			}

			return;
		}
		
		jseObject = el;		

		if(el.isJsonNull()){
			bhasErrors = true;
			return;
		}

		if(el.isJsonArray()){       
			JsonArray array = el.getAsJsonArray();

			for(int i = 0; i < array.size(); ++i){
				JsonElement e = array.get(i);

				if(!e.isJsonObject())
					continue;

				lWidgets.add(getWidgetFromType(cContext, e.getAsJsonObject()));
			}
		}

		if(eventHandler != null){
			eventHandler.finish(PXFParser.this, json);
		}
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

			return new String( bytes );

		} catch ( IOException e ) {
			Log.i("MakeMachine", "IOException: " + e.getMessage() );
		}
		return null;
	}

	private static PXWidget getWidgetFromType(final Context context,
			final JsonObject entry){

		//View v = null;
		Map<String, Map.Entry<String,JsonElement>> map = new HashMap<String, Map.Entry<String,JsonElement>>();
		PXWidget widget = null;

		//map all the fields by key
		for(Map.Entry<String,JsonElement> e : entry.entrySet()){
			map.put(e.getKey(), e);
		}

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
				//TODO date dialog picker
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
