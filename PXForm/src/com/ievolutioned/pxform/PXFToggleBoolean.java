package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class PXFToggleBoolean extends PXWidget {

	public PXFToggleBoolean(Map<String, Entry<String, JsonElement>> entry) {
		super(entry);
	}

	//@Override
	//protected View addControls(Context context,
	//		Map<String, Entry<String, JsonElement>> map) {
	//	//layout container
	//	LinearLayout linear = getGenericLinearLayout(context);
	//	linear.setWeightSum(1);
    //
	//	//field name
	//	TextView text = getGenericTextView(context,
	//			map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");
	//	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	//			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
	//	text.setLayoutParams(params);
    //
	//	//initial toggle button configuration
	//	CustomSwitch toggleBoolean = new CustomSwitch(context, map);
	//	params = new LinearLayout.LayoutParams(
	//			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
	//	toggleBoolean.setLayoutParams(params);
    //
	//	//add views to the container
	//	linear.addView(text);
	//	linear.addView(toggleBoolean);
    //
	//	return linear;
	//}


	//public class CustomSwitch extends LinearLayout {
	//	private static final String DEFAULT_TEXT_OFF = "No";
	//	private static final String DEFAULT_TEXT_ON = "Si";
    //
	//	private String textOff = DEFAULT_TEXT_OFF;
	//	private String textOn = DEFAULT_TEXT_ON;
	//	private String textSelected = textOff;
    //
	//	private RadioGroup mRadioSwitch;
	//	private RadioButton mRadioButtonOff;
	//	private RadioButton mRadioButtonOn;
    //
	//	public CustomSwitch(Context context, Map<String, Entry<String, JsonElement>> map) {
	//		super(context);
    //
	//		//add fields to the adapter
	//		if(map.get(FIELD_OPTIONS).getValue().isJsonArray()){
	//			JsonArray array = map.get(FIELD_OPTIONS).getValue().getAsJsonArray();
	//			textOn = array.get(0).getAsString();
	//			textOff = array.get(1).getAsString();
	//		}
	//		else
	//			Log.e("PXFToggleBoolean", "Error: options are not a json array!");
    //
	//		mRadioSwitch = new RadioGroup(context);
	//		mRadioSwitch.setOrientation(android.widget.LinearLayout.HORIZONTAL);
    //
	//		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
	//				RadioGroup.LayoutParams.WRAP_CONTENT,
	//				RadioGroup.LayoutParams.WRAP_CONTENT);
	//
	//		mRadioButtonOn = new RadioButton(context);
	//		mRadioButtonOn.setText(DEFAULT_TEXT_ON);
	//		mRadioButtonOn.setId(0);
	//		params = new RadioGroup.LayoutParams(
	//				RadioGroup.LayoutParams.WRAP_CONTENT,
	//				RadioGroup.LayoutParams.WRAP_CONTENT);
	//		mRadioButtonOn.setLayoutParams(params);
	//
	//		mRadioButtonOff = new RadioButton(context);
	//		mRadioButtonOff.setText(DEFAULT_TEXT_OFF);
	//		mRadioButtonOff.setId(1);
	//		mRadioButtonOff.setLayoutParams(params);
	//
	//		mRadioButtonOn.setText(textOn);
	//		mRadioButtonOff.setText(textOff);
    //
	//		mRadioSwitch.setOnCheckedChangeListener(radio_check);
	//		mRadioSwitch.addView(mRadioButtonOn);
	//		mRadioSwitch.addView(mRadioButtonOff);
	//		CustomSwitch.this.addView(mRadioSwitch);
	//	}
    //
	//	public String getSelectedText() {
	//		return this.textSelected;
	//	}
    //
	//	private OnCheckedChangeListener radio_check = new OnCheckedChangeListener() {
	//		@Override
	//		public void onCheckedChanged(RadioGroup group, int id) {
	//			CustomSwitch.this.textSelected =
	//					id == mRadioButtonOff.getId() ? textOff : textOn;
	//		}
	//	};
	//
	//	public void setTextOn(String on) {
	//		this.textOn = on;
	//		mRadioButtonOn.setText(on);
	//	}
    //
	//	public void setTextOff(String off) {
	//		this.textOff = off;
	//		mRadioButtonOff.setText(off);
	//	}
	//}
}
