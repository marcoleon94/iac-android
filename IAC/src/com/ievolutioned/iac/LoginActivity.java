package com.ievolutioned.iac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ievolutioned.iac.model.LoginService;
import com.ievolutioned.iac.util.AppConfig;

/**
 * Log in activity class. Manages the log in actions
 */
public class LoginActivity extends Activity {

    /**
     * EditText email control
     */
    private EditText mEmail;
    /**
     * EditText password control
     */
    private EditText mPassword;
    /**
     * Button for sing in action
     */
    private Button mButtonSingIn;
    /**
     * ProgressBar spinner control
     */
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!AppConfig.DEBUG)
            Crashlytics.start(this);
        setContentView(R.layout.activity_login);
        bindUI();
    }

    /**
     * Binds the user interface to control
     */
    private void bindUI() {
        // find controls
        mEmail = (EditText) findViewById(R.id.activity_login_editEmail);
        mPassword = (EditText) findViewById(R.id.activity_login_editPassword);
        mButtonSingIn = (Button) findViewById(R.id.activity_login_btnLogIn);
        mSpinner = (ProgressBar) findViewById(R.id.activity_login_progressBar);

        mSpinner.setVisibility(View.GONE);

        //On click listeners
        mButtonSingIn.setOnClickListener(button_click);
    }

    /**
     * Controls the button click
     */
    private OnClickListener button_click = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.activity_login_btnLogIn:
                    if(AppConfig.DEBUG) {
                        logIn();
                        break;
                    }
                    if (validateForm())
                        logIn();
                    break;
            }
        }
    };

    /**
     * Validates the sing in form
     *
     * @return true if it is valid, false otherwise
     */
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

    /**
     * Shows a message toast
     *
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows the loading view
     *
     * @param l
     */
    private void loading(boolean l) {
        if (l)
            mSpinner.setVisibility(View.VISIBLE);
        else
            mSpinner.setVisibility(View.GONE);
    }

    /**
     * Logs the user in the system
     */
    private void logIn() {
        loading(true);
        LoginService loginService = new LoginService();
        loginService.logIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim(), login_handler);
    }

    /**
     * Log in call back handler
     */
    private LoginService.LoginHandler login_handler = new LoginService.LoginHandler() {
        @Override
        public void onSuccess(LoginService.LoginResponse response) {
            showToast("Logged in");
            loading(false);
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }

        @Override
        public void onError(LoginService.LoginResponse response) {
            showToast("Logged in");
            loading(false);
        }

        @Override
        public void onCancel() {
            showToast("Canceled!");
            loading(false);
        }
    };
}