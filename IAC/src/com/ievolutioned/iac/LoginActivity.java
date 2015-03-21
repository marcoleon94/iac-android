package com.ievolutioned.iac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

public class LoginActivity extends Activity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mButtonSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_login);
        bindUI();
    }

    private void bindUI() {
        mEmail = (EditText) findViewById(R.id.activity_login_editEmail);
        mPassword = (EditText) findViewById(R.id.activity_login_editPassword);
        mButtonSingIn = (Button) findViewById(R.id.activity_login_btnLogIn);

        //On click listeners

        mButtonSingIn.setOnClickListener(button_click);
    }

    private OnClickListener button_click = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.activity_login_btnLogIn:
                    if (validateForm())
                        logIn();
                    break;
            }
        }
    };

    private boolean validateForm() {
        EditText[] forms = {mEmail, mPassword};
        for (EditText f : forms) {
            if (TextUtils.isEmpty(f.getText())) {
                showToast();
                f.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showToast() {
        Toast.makeText(this, "*Campo requerido", Toast.LENGTH_SHORT).show();
    }

    private void logIn() {

    }
}