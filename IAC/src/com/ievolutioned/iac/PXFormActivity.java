package com.ievolutioned.iac;

import com.ievolutioned.pxform.PXFAdapter;
import com.ievolutioned.pxform.PXFParser;
import com.ievolutioned.pxform.PXFParser.PXFParserEventHandler;
import com.ievolutioned.iac.view.ViewUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PXFormActivity extends Activity {
	
	PXFParser p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pxform_activity);

		//final LinearLayout container = (LinearLayout)findViewById(R.id.PXForm_linearPanel);
        final ListView listView  = (ListView)findViewById(R.id.PXForm_linearPanel);
		final AlertDialog loading = ViewUtility.getLoadingScreen(PXFormActivity.this);
		loading.show();
		
		p = new PXFParser(new PXFParserEventHandler() {
            @Override
            public void finish(PXFAdapter adapter, String json) {
                listView.setAdapter(adapter);
                loading.dismiss();
            }

            @Override
            public void error(Exception ex, String json) {
                Toast.makeText(PXFormActivity.this, "can't parse json", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });

		p.parseJson(PXFormActivity.this, PXFParser.parseFileToString(getApplicationContext(),
                "FormFields_PabloUTF8.json"));
	}
}
