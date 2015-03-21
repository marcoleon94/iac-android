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
import com.ievolutioned.iac.model.LoginService;

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
                showToast("*Required field");
                f.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void logIn() {
        LoginService loginService = new LoginService();
        loginService.logIn(mEmail.getText().toString().trim(),mPassword.getText().toString().trim(), login_handler);
    }

    private LoginService.LoginHandler login_handler = new LoginService.LoginHandler() {
        @Override
        public void onSuccess(LoginService.LoginResponse response) {
            showToast("Logged in");
        }

        @Override
        public void onError(LoginService.LoginResponse response) {
            showToast("Logged in");
        }

        @Override
        public void onCancel() {
            showToast("Canceled!");
        }
    };
}