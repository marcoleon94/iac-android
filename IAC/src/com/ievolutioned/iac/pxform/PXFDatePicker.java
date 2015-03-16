package com.ievolutioned.iac.pxform;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

public class PXFDatePicker extends PXWidget {
	private Context context;

	public PXFDatePicker(Context cont,
			Map<String, Entry<String, JsonElement>> entry) {
		super(cont, entry);
		context = cont;
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

		final Button button = new Button(context);
		params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
		button.setLayoutParams(params);
		button.setText(map.containsKey(FIELD_TITLE) ? map.get(FIELD_TITLE).getValue().getAsString() : "Date Picker");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerFragment newFragment = new DatePickerFragment();
				Activity activity = (Activity)PXFDatePicker.this.context;
				newFragment.show(activity.getFragmentManager(), "timePicker");
				newFragment.setIDatePicked(new DatePickerFragment.IDatePicked() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						button.setText(String.format(Locale.US, "%d/%d/%d", year, month, day));
					}
				});
			}
		});

		//add views to the container
		linear.addView(text);
		linear.addView(button);

		return linear;
	}

	public static class DatePickerFragment extends DialogFragment
	implements android.app.DatePickerDialog.OnDateSetListener {
		
		private IDatePicked eventHandler;

		public void setIDatePicked(IDatePicked callback){
			eventHandler = callback;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			dismiss();
			if(eventHandler != null)
				eventHandler.onDateSet(view, year, month, day);
		}
		
		public interface IDatePicked{
			void onDateSet(DatePicker view, int year, int month, int day);
		}
	}
}
