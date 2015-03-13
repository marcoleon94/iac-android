package com.ievolutioned.iac;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.ievolutioned.iac.json2view.DynamicView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

//https://github.com/Avocarrot/json2view
public class JsonToView extends Activity {

	public static final String FILE_JSON_TO_VIEW = "jsonToview.json";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_jsontoview);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(parseFileToString(
					getApplicationContext(), FILE_JSON_TO_VIEW));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		View sampleView = DynamicView.createView(this, jsonObject);

		sampleView.setLayoutParams(new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT));
		setContentView(sampleView);
	}

	public static String parseFileToString(Context context, String filename) {
		try {
			InputStream stream = context.getAssets().open(filename);
			int size = stream.available();

			byte[] bytes = new byte[size];
			stream.read(bytes);
			stream.close();

			return new String(bytes);

		} catch (IOException e) {
			Log.i("JsonToView", "IOException: " + e.getMessage());
		}
		return null;
	}
}