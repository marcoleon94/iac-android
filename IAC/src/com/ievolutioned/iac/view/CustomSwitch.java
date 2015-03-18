package com.ievolutioned.iac.view;

import com.ievolutioned.iac.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CustomSwitch extends LinearLayout implements
		android.widget.RadioGroup.OnCheckedChangeListener {

	private static final String DEFAULT_TEXT_OFF = "No";
	private static final String DEFAULT_TEXT_ON = "Sí";

	private String textOff = DEFAULT_TEXT_OFF;
	private String textOn = DEFAULT_TEXT_ON;
	private String textSelected = textOff;

	private RadioGroup mRadioSwitch;
	private RadioButton mRadioButtonOff;
	private RadioButton mRadioButtonOn;

	public CustomSwitch(Context context) {
		super(context);
		View root = LayoutInflater.from(context).inflate(
				R.layout.custom_switch_boolean, this);
		bindUI(root);
	}

	private void bindUI(View root) {
		mRadioSwitch = (RadioGroup) root
				.findViewById(R.id.custom_switch_boolean);
		mRadioButtonOff = (RadioButton) root
				.findViewById(R.id.custom_switch_boolean_off);
		mRadioButtonOn = (RadioButton) root
				.findViewById(R.id.custom_switch_boolean_on);

		mRadioButtonOff.setText(DEFAULT_TEXT_OFF);
		mRadioButtonOn.setText(DEFAULT_TEXT_ON);

		mRadioSwitch.setOnCheckedChangeListener(this);
	}

	public String getSelectedText() {
		return this.textSelected;
	}

	@Override
	public void onCheckedChanged(RadioGroup radio, int id) {
		if (id == R.id.custom_switch_boolean_off)
			this.textSelected = textOff;
		else
			this.textSelected = textOn;
	}

	public void setTextOn(String on) {
		this.textOn = on;
		mRadioButtonOn.setText(on);
	}

	public void setTextOff(String off) {
		this.textOff = off;
		mRadioButtonOff.setText(off);
	}

}
