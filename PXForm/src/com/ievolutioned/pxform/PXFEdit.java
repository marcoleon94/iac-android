package com.ievolutioned.pxform;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFEdit extends PXWidget{

	public PXFEdit(Map<String, Entry<String, JsonElement>> entry) {
		super(entry);
	}

	//protected View addControls(Context context,
	//		Map<String, Map.Entry<String,JsonElement>> map){
    //
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
	//	//edit input control
	//	EditText edit = new EditText(context);
	//	params = new LinearLayout.LayoutParams(
	//			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
	//	edit.setLayoutParams(params);
	//	edit.setLines(1);
	//	editConfigureTypeOfInput(edit, map);
    //
	//	//edit.setMaxLines(1);
    //
	//	if(map.containsKey(FIELD_PLACEHOLDER)){
	//		edit.setHint(map.get(FIELD_PLACEHOLDER).getValue().getAsString());
	//	}
    //
	//	//add views to the container
	//	linear.addView(text);
	//	linear.addView(edit);
    //
	//	return linear;
	//}
    //
	//private void editConfigureTypeOfInput(EditText edit,
	//		Map<String, Map.Entry<String,JsonElement>> map){
	//	int maxsize = 50;
    //
	//	if(FIELD_TYPE_LONGTEXT.equals(map.get(FIELD_TYPE).getValue().getAsString())){
	//		try{
	//			maxsize = Integer.parseInt(map.get(FIELD_TYPE).getValue().getAsString());
	//		}catch(Exception ex){
	//		}
	//	}
    //
	//	InputFilter[] FilterArray = new InputFilter[1];
	//	FilterArray[0] = new InputFilter.LengthFilter(maxsize);
    //
	//	if(FIELD_TYPE_UNSIGNED.equals(map.get(FIELD_TYPE).getValue().getAsString())){
	//		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
	//	}else{
	//		edit.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
	//	}
    //
	//	edit.setFilters(FilterArray);
	//}
}
