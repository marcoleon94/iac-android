package com.ievolutioned.iac;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ievolutioned.iac.makemachine.FormActivity;

public class MFormGenerator extends FormActivity {

	public static final int OPTION_SAVE = 0;
	public static final int OPTION_POPULATE = 1;
	public static final int OPTION_CANCEL = 2;

	public static final String FILE_FIELDS = "FormFields.json";
	public static final String FILE_SCHEMA = "schemas.json";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mform_generator);
		generateForm(FormActivity.parseFileToString(this, FILE_FIELDS));
		save();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, OPTION_SAVE, 0, "Save");
		menu.add(0, OPTION_POPULATE, 0, "Populate");
		menu.add(0, OPTION_CANCEL, 0, "Cancel");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int id, MenuItem item) {
		switch (item.getItemId()) {
		case OPTION_SAVE:
			save();
			break;
		case OPTION_POPULATE:
			populate(FormActivity.parseFileToString(this, "data.json"));
			break;
		case OPTION_CANCEL:
			break;
		}
		return super.onMenuItemSelected(id, item);
	}
}
