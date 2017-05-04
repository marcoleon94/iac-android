package com.ievolutioned.iac;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.ievolutioned.iac.util.AppPreferences;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by Daniel on 04/05/2017.
 */

public class CustomScannerActivity extends Activity implements View.OnClickListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button mTorch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_barcode_scanner);
        bindUI(savedInstanceState);
    }

    private void bindUI(Bundle savedInstanceState) {
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        View cancel = findViewById(R.id.zxing_barcode_cancel);
        if (cancel != null)
            cancel.setOnClickListener(this);

        mTorch = (Button) findViewById(R.id.zxing_barcode_torch);
        if (mTorch != null) {
            if (!hasTorch())
                mTorch.setVisibility(View.GONE);
            else
                mTorch.setOnClickListener(this);
        }
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
        bindData();
    }

    private void bindData() {
        boolean previousState = AppPreferences.getTorchState(this);
        if (previousState && barcodeScannerView != null) {
            barcodeScannerView.setTorchOn();
            switchTorchDrawableState(previousState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zxing_barcode_cancel:
                cancel();
                break;
            case R.id.zxing_barcode_torch:
                switchTorchState();
                break;
            default:
                break;
        }
    }


    private boolean hasTorch() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void switchTorchState() {
        boolean state = AppPreferences.toggleTorchState(this);
        if (state) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
        switchTorchDrawableState(state);
    }

    private void switchTorchDrawableState(boolean state) {
        if (mTorch != null) {
            Drawable icon = state ? ContextCompat.getDrawable(this, android.R.drawable.presence_online) :
                    ContextCompat.getDrawable(this, android.R.drawable.presence_invisible);
            if (mTorch.getCompoundDrawables().length > 0) {
                Rect bounds = mTorch.getCompoundDrawables()[0].getBounds();
                icon.setBounds(bounds);
                mTorch.setCompoundDrawables(icon, null, null, null);
            }
        }
    }

}
