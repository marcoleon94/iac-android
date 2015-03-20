package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFUnknownControlType extends PXWidget {

	public PXFUnknownControlType(Map<String, Entry<String, JsonElement>> entry) {
		super(entry);
	}

	//@Override
	//protected View addControls(Context context,
	//		Map<String, Entry<String, JsonElement>> entry) {
	//	getViewList().clear(); //cleanup head if any
	//
	//	TextView t = getGenericTextView(context, "Can't find appropriate control to assign");
	//	ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
	//			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	//	t.setBackgroundResource(android.R.color.black);
	//	t.setTextColor(Color.WHITE);
	//	t.setLayoutParams(par);
	//	return t;
	//}
}
