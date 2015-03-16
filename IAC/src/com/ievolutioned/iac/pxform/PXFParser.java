package com.ievolutioned.iac.pxform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PXFParser {

	private JsonElement jseObject;
	private List<PXWidget> lWidgets = new ArrayList<PXWidget>();

	private PXFParser(){ }

	public List<PXWidget> getWidget(){
		return lWidgets;
	}
	public JsonElement getJsonElement(){
		return jseObject;
	}

	/**
	 * 
	 * @param context
	 * @param json
	 * @return
	 */
	public static PXFParser parseXForm(final Context context, final String json){
		PXFParser p = new PXFParser();
		//final LayoutInflater inflayer = LayoutInflater.from(context);
		JsonElement el = new JsonParser().parse(json);
		p.jseObject = el;

		if(el.isJsonNull())
			return p;

		if(el.isJsonArray()){       
			JsonArray array = el.getAsJsonArray();

			for(int i = 0; i < array.size(); ++i){
				JsonElement e = array.get(i);

				if(!e.isJsonObject())
					continue;

				p.lWidgets.add(getWidgetFromType(context, e.getAsJsonObject()));
			}
		}

		return p;
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

	public static PXWidget getWidgetFromType(final Context context,
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
