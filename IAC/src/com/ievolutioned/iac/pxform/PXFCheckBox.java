package com.ievolutioned.iac.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFCheckBox extends PXWidget {

	public PXFCheckBox(Context context,
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

		//check box control
		CheckBox box = new CheckBox(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		box.setLayoutParams(params);

		//add views to the container
		linear.addView(text);
		linear.addView(box);

		return linear;
	}
}
