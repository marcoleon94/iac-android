package com.ievolutioned.iac.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class PXFSpinner extends PXWidget {

	public PXFSpinner(Context context,
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

}
