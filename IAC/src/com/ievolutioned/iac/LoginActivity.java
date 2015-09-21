package com.ievolutioned.iac;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ievolutioned.iac.net.service.LoginService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.database.FormsDataSet;
import io.fabric.sdk.android.Fabric;

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
     * AlertDialog loading control
     */
    private AlertDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_login);
        bindUI();
        if (AppConfig.DEBUG) {
            mEmail.setText("123456789");
            mPassword.setText("123456789");
        }

        com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(LoginActivity.this);

        /*if (f.countAll() < 1) {
            long i = f.insert("", "Encuesta de Salida");
            Log.d("XXX", String.valueOf(i));
        }*/
        f.deleteAll();
    }

    /**
     * Binds the user interface to control
     */
    private void bindUI() {
        // find controls
        mEmail = (EditText) findViewById(R.id.activity_login_editEmail);
        mPassword = (EditText) findViewById(R.id.activity_login_editPassword);
        mButtonSingIn = (Button) findViewById(R.id.activity_login_btnLogIn);
        mLoading = ViewUtility.getLoadingScreen(this);

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
                    if (AppConfig.DEBUG) {
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
                showToast(R.string.activity_login_required_field);
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
    private void showToast(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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
            mLoading.show();
        else
            mLoading.dismiss();
    }

    /**
     * Logs the user in the system
     */
    private void logIn() {
        loading(true);
        LoginService loginService = new LoginService(AppConfig.getUUID(this));
        loginService.logIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim(),
                login_handler);
    }

    /**
     * Log in call back handler
     */
    private LoginService.LoginHandler login_handler = new LoginService.LoginHandler() {
        @Override
        public void onSuccess(LoginService.LoginResponse response) {
            loading(false);
            saveToken(response.user.getAdminToken());
            startMainActivity();
        }

        @Override
        public void onError(LoginService.LoginResponse response) {
            showToast("Error: " + response.msg);
            loading(false);
        }

        @Override
        public void onCancel() {
            showToast("Canceled!");
            loading(false);
        }
    };

    /**
     * Starts the main activity
     */
    private void startMainActivity() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

    /**
     * Saves the token in the shared preferences
     *
     * @param token - The admin token
     */
    private void saveToken(String token) {
        if (token == null)
            return;
        LogUtil.d(LoginActivity.class.getName(), "token: " + token);
        try {
            AppPreferences.setAdminToken(this, token);
        } catch (Exception e) {
            LogUtil.e(LoginActivity.class.getName(), "Can not set ADMIN TOKEN", e);
        }
    }
}