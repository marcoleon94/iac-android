package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonElement;

public class PXFButton extends PXWidget {

	public PXFButton(Context context,
			Map<String, Entry<String, JsonElement>> entry) {
		super(context, entry);
	}

	@Override
	protected View addControls(Context context,
			Map<String, Entry<String, JsonElement>> map) {
		Button button = new Button(context);
		ViewGroup.LayoutParams par = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(par);
		button.setText(map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : " ");

		return button;
	}
}
