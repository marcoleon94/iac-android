package com.ievolutioned.pxform;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class PXFDatePicker extends PXWidget {

    private static final String SERVER_DATE_FORMAT = "MM/dd/yy";

    private Calendar calendarState;
    private FragmentManager fragmentManager;

    public static class HelperDatePicker extends HelperWidget {
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
    public void setValue(String value) {
        try {
            if (value == null || value.isEmpty())
                return;
            if (this.calendarState == null)
                this.calendarState = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            this.calendarState.setTime(sdf.parse(value));
        } catch (Exception ex) {
            Log.e(PXFDatePicker.class.getName(), "Cant set value", ex);
        }
    }

    @Override
    public String getValue() {
        if (this.calendarState != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            return sdf.format(this.calendarState.getTime());
        }
        return "";
    }

    @Override
    public void setWidgetData(View view) {
        super.setWidgetData(view);
        HelperDatePicker helper = (HelperDatePicker) view.getTag();
        helper.title.setText(getJsonEntries().containsKey(FIELD_TITLE) ?
                getJsonEntries().get(FIELD_TITLE).getValue().getAsString() : " ");
        if(this.calendarState != null)
            helper.buttonDatePicker.setText(getValue());

        helper.buttonDatePicker.setOnClickListener(button_click);
    }

    @Override
    public View createControl(Activity context) {
        fragmentManager = context.getFragmentManager();
        //calendarState = Calendar.getInstance();

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
        button.setOnClickListener(button_click);
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
        private Calendar calendarS;

        public void setIDatePicked(IDatePicked callback) {
            eventHandler = callback;
        }

        static DatePickerFragment getInstance(Calendar cal) {
            DatePickerFragment dial = new DatePickerFragment();
            dial.calendarS = cal;
            return dial;
        }

        @Override
        public Dialog onCreateDialog(Bundle saved) {
            // Use the current date as the default date in the picker
            Calendar c = calendarS; //Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dismiss();
            if (eventHandler != null)
                eventHandler.onDateSet(view, year, month, day);
        }

        public interface IDatePicked {
            void onDateSet(DatePicker view, int year, int month, int day);
        }
    }

    private OnClickListener button_click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (PXFDatePicker.this.calendarState == null)
                PXFDatePicker.this.calendarState = Calendar.getInstance();
            DatePickerFragment newFragment = DatePickerFragment.getInstance(
                    PXFDatePicker.this.calendarState);
            newFragment.show(fragmentManager, "timePicker");
            final Button my_button = (Button) v;
            newFragment.setIDatePicked(new DatePickerFragment.IDatePicked() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    PXFDatePicker.this.calendarState = Calendar.getInstance();
                    PXFDatePicker.this.calendarState.set(year, month, day);
                    my_button.setText(String.format(Locale.US, "%d/%d/%d", year, month, day));
                }
            });
        }
    };

    @Override
    public boolean validate() {
        if(!getValue().isEmpty())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return this.calendarState == null ? "" : this.calendarState.toString();
    }
}
