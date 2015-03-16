package com.ievolutioned.iac;

import com.ievolutioned.iac.pxform.PXFParser;
import com.ievolutioned.iac.pxform.PXFParser.PXFParserEventHandler;
import com.ievolutioned.iac.pxform.PXWidget;
import com.ievolutioned.iac.view.ViewUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PXFormActivity extends Activity {
	
	PXFParser p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pxform_activity);

		final LinearLayout container = (LinearLayout)findViewById(R.id.PXForm_linearPanel);
		final AlertDialog loading = ViewUtility.getLoadingScreen(PXFormActivity.this);
		loading.show();
		
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
				loading.dismiss();
			}
			
			@Override
			public void error(Exception ex, String json) {
				loading.dismiss();
				Toast.makeText(PXFormActivity.this, "Can't parse json", Toast.LENGTH_LONG).show();
			}
		});
		
		p.parseJson(PXFParser.parseFileToString(getApplicationContext(), "FormFields_Pablo.json"));
	}
}
