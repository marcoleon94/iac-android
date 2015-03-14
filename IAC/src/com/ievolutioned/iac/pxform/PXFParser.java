package com.ievolutioned.iac.pxform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PXFParser {

	private JsonElement jseObject;
	private List<PXFormWidget> lWidgets = new ArrayList<PXFormWidget>();

	private PXFParser(){ }

	public List<PXFormWidget> getWidget(){
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
		final LayoutInflater inflayer = LayoutInflater.from(context);
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

				p.lWidgets.add(PXFormWidget.getWidgetFromType(context, inflayer, e.getAsJsonObject()));
			}
		}

		return p;
	}
	/**
	 * 
	 * @param context
	 * @param inflater
	 * @param jsonOnj
	 * @return
	 */
	/*
	private static List<PXFormWidget> getWidget(final Context context, final LayoutInflater inflater, 
			final JsonObject jsonOnj){
		Set<Map.Entry<String,JsonElement>> entrySet = jsonOnj.entrySet();

		List<PXFormWidget> list = new ArrayList<PXFormWidget>();

		for(Map.Entry<String,JsonElement> e : entrySet){
			PXFormWidget w = PXFormWidget.getWidgetFromType(context, inflater, jsonOnj, e);

			if(w == null)
				continue;

			list.add(w);
		}

		return list;
	}
	 */

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
}
