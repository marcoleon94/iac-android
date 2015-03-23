package com.ievolutioned.pxform;

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

    public static class HelperDatePicker extends HelperWidget{
        protected TextView title;
        protected LinearLayout linearDatePicker;
        protected Button buttonDatePicker;
    }

    public PXFDatePicker(Map<String, Entry<String, JsonElement>> entry) {
        super(entry);
    }

    @Override
    protected HelperWidget generateHelperClass() {
        return new HelperDatePicker();
    }

    @Override
    public int getAdapterItemType() {
        return PXWidget.ADAPTER_ITEM_TYPE_DATEPICKER;
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperDatePicker helper = (HelperDatePicker) view.getTag();
        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");

        //TODO: read json to know what the state of the date already set
    }

    @Override
    protected View createControl(final Activity context) {
        LinearLayout v = (LinearLayout) super.createControl(context);
        HelperDatePicker helper = (HelperDatePicker) v.getTag();

        //layout container
        LinearLayout linear = getGenericLinearLayout(context);
        linear.setWeightSum(1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        linear.setLayoutParams(params);
        helper.linearDatePicker = linear;

        //field name
        TextView text = getGenericTextView(context, getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        text.setLayoutParams(params);
        helper.title = text;

        Button button = new Button(context);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        button.setLayoutParams(params);
        button.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : "Date Picker");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(context.getFragmentManager(), "timePicker");
                final Button my_button = (Button)v;
                newFragment.setIDatePicked(new DatePickerFragment.IDatePicked() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        my_button.setText(String.format(Locale.US, "%d/%d/%d", year, month, day));
                    }
                });
            }
        });
        helper.buttonDatePicker = button;

        //add controls to linear parent before main container
        linear.addView(text);
        linear.addView(button);

        //add controls to main container
        v.addView(linear);

        return v;
    }

    public static class DatePickerFragment extends DialogFragment
            implements android.app.DatePickerDialog.OnDateSetListener {

        private IDatePicked eventHandler;

        public void setIDatePicked(IDatePicked callback){
            eventHandler = callback;
        }

        @Override
        public Dialog onCreateDialog(Bundle saved) {
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
