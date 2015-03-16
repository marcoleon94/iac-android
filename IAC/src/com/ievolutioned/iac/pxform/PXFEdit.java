package com.ievolutioned.iac.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFEdit extends PXWidget{

	public PXFEdit(Context context, Map<String, Entry<String, JsonElement>> entry) {
		super(context, entry);
	}
	
	protected View addControls(Context context, 
			Map<String, Map.Entry<String,JsonElement>> map){

		//layout container
		LinearLayout linear = getGenericLinearLayout(context);
		linear.setWeightSum(1);
		
		//field name
		TextView text = getGenericTextView(context, 
				map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		text.setLayoutParams(params);
		
		//edit input control
		EditText edit = new EditText(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		edit.setLayoutParams(params);
		edit.setLines(1);
		//edit.setMaxLines(1);
		
		if(map.containsKey(FIELD_PLACEHOLDER)){
			edit.setHint(map.get(FIELD_PLACEHOLDER).getValue().getAsString());
		}

		//add views to the container
		linear.addView(text);
		linear.addView(edit);
		
		return linear;
	}
}
