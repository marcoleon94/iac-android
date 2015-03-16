package com.ievolutioned.iac;

import com.ievolutioned.iac.pxform.PXFParser;
import com.ievolutioned.iac.pxform.PXFParser.PXFParserEventHandler;
import com.ievolutioned.iac.pxform.PXWidget;

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

		final LinearLayout container = (LinearLayout)findViewById(R.id.PXForm_linearPanel);
		
		p = new PXFParser(
				//getApplicationContext()
				PXFormActivity.this
				, new PXFParserEventHandler() {
			@Override
			public void finish(PXFParser parser, String json) {
				for(PXWidget w : p.getWidget()){
					for(View v : w.getViewList()){
						container.addView(v);
					}
				}
			}
			
			@Override
			public void error(Exception ex, String json) {
			}
		});
		
		p.parseJson(PXFParser.parseFileToString(getApplicationContext(), "FormFields_Pablo.json"));
	}
}
