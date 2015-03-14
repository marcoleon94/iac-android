package com.ievolutioned.iac;

import com.ievolutioned.iac.pxform.PXFParser;
import com.ievolutioned.iac.pxform.PXFormWidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class PXFormActivity extends Activity {
	
	PXFParser p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pxform_activity);

		p = PXFParser.parseXForm(PXFormActivity.this,
				PXFParser.parseFileToString(PXFormActivity.this, "FormFields_Pablo.json"));
		LinearLayout container = (LinearLayout)findViewById(R.id.PXForm_linearPanel);
		
		for(PXFormWidget w : p.getWidget()){
			for(View v : w.getViewList()){
				container.addView(v);
			}
		}

	}
}
