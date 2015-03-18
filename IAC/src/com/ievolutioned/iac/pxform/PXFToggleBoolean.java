package com.ievolutioned.iac.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ievolutioned.iac.view.CustomSwitch;

public class PXFToggleBoolean extends PXWidget {

	public PXFToggleBoolean(Context context,
			Map<String, Entry<String, JsonElement>> entry) {
		super(context, entry);
	}

	@Override
	protected View addControls(Context context,
			Map<String, Entry<String, JsonElement>> map) {
		//layout container
		LinearLayout linear = getGenericLinearLayout(context);
		linear.setWeightSum(1);
		
		//field name
		TextView text = getGenericTextView(context, 
				map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		text.setLayoutParams(params);

		//initial toggle button configuration
		CustomSwitch toggleBoolean = new CustomSwitch(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		toggleBoolean.setLayoutParams(params);

		//add fields to the adapter
		if(map.get(FIELD_OPTIONS).getValue().isJsonArray()){
			JsonArray array = map.get(FIELD_OPTIONS).getValue().getAsJsonArray();
			final String on = array.get(0).getAsString();
			final String off = array.get(1).getAsString();
			
			toggleBoolean.setTextOn(on);
			toggleBoolean.setTextOff(off);
			//toggleBoolean.setOnCheckedChangeListener(listener);
			
		}
		else
			Log.e("PXFToggleBoolean", "Error: options are not a json array!");

		//add views to the container
		linear.addView(text);
		linear.addView(toggleBoolean);

		return linear;
	}

}
