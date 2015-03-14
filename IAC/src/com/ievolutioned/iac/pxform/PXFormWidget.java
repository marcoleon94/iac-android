package com.ievolutioned.iac.pxform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class PXFormWidget {
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
	
	Map<String, Map.Entry<String,JsonElement>> eEntry;
	private List<View> lViews = new ArrayList<View>();

	public static PXFormWidget getWidgetFromType(final Context context, 
			final LayoutInflater inflater, final JsonObject entry){

		View v = null;
		Map<String, Map.Entry<String,JsonElement>> map = new HashMap<String, Map.Entry<String,JsonElement>>();
		PXFormWidget widget = null;

		//map all the fields by key
		for(Map.Entry<String,JsonElement> e : entry.entrySet()){
			map.put(e.getKey(), e);
		}

		widget = new PXFormWidget(map);

		//check if had a head
		if(map.containsKey(FIELD_HEADER)){
			v = getTextViewHead(context, map);
			widget.lViews.add(v);
		}

		if(map.containsKey(FIELD_TYPE)){
			//we got a well defined field
			if(map.containsKey(FIELD_TYPE_TEXT)){
			}
			else if(map.containsKey(FIELD_TYPE_BOOLEAN)){
			}
			else if(map.containsKey(FIELD_TYPE_DATE)){
			}
			else if(map.containsKey(FIELD_TYPE_LONGTEXT)){
			}
			
			v = getGenericEditText(context, map);
			widget.lViews.add(v);
		}else if(map.containsKey(FIELD_OPTIONS)){
			//we got a spinner control
			v = getSpinner(context, map);
			widget.lViews.add(v);
		}else if(map.containsKey(FIELD_ACTION)){
			//if is not an option then is a button
			v = getButton(context, map);
			widget.lViews.add(v);
		}

		return widget;
	}

	/**
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	private static TextView getTextViewHead(Context context, 
			Map<String, Map.Entry<String,JsonElement>> map){
		TextView t = new TextView(context);
		t.setText(map.get(FIELD_HEADER).getValue().getAsString());
		ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(par);
		return t;
	}

	private static TextView getTextView(Context context, String text){
		TextView t = new TextView(context);
		t.setText(text);
		return t;
	}

	private static LinearLayout getLinearLayout(Context context){
		LinearLayout l = new LinearLayout(context);
		l.setWeightSum(1);
		ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		l.setLayoutParams(par);
		return l;
	}

	private static LinearLayout getSpinner(Context context, 
			Map<String, Map.Entry<String,JsonElement>> map){
		
		//layout container
		LinearLayout linear = getLinearLayout(context);
		
		//field name
		TextView text = getTextView(context, 
				map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		text.setLayoutParams(params);
		
		//initial spinner configuration
		Spinner spinner = new Spinner(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		spinner.setLayoutParams(params);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_item );

		//add fields to the adapter
		if(map.get(FIELD_OPTIONS).getValue().isJsonArray()){
			JsonArray array = map.get(FIELD_OPTIONS).getValue().getAsJsonArray();
			
			for(int x = 0; x < array.size(); ++x){
				final String s = array.get(x).getAsString();
				adapter.add(s);
			}
		}
		
		
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		
		//add views to the container
		linear.addView(text);
		linear.addView(spinner);
		
		return linear;
	}
	
	private static Button getButton(Context context, 
			Map<String, Map.Entry<String,JsonElement>> map){
		Button button = new Button(context);
		ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(par);
		button.setText(map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");
		
		return button;
	}
	
	
	private static LinearLayout getGenericEditText(Context context, 
			Map<String, Map.Entry<String,JsonElement>> map){
		
		//layout container
		LinearLayout linear = getLinearLayout(context);
		
		//field name
		TextView text = getTextView(context, 
				map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		text.setLayoutParams(params);
		
		//edit input control
		EditText edit = new EditText(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		edit.setLayoutParams(params);
		
		if(map.containsKey(FIELD_PLACEHOLDER)){
			edit.setHint(map.get(FIELD_PLACEHOLDER).getValue().getAsString());
		}
		
		//add views to the container
		linear.addView(text);
		linear.addView(edit);
		
		return linear;
	}
	

	public PXFormWidget(Map<String, Map.Entry<String,JsonElement>> entry){
		eEntry = entry;
	}

	public List<View> getViewList(){
		return lViews;
	}
}
