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

import com.crashlytics.android.Crashlytics;
import com.ievolutioned.iac.entity.UserEntity;
import com.ievolutioned.iac.net.NetUtil;
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
     * TAG
     */
    public static final String TAG = LoginActivity.class.getName();

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
                    if (!NetUtil.hasNetworkConnection(LoginActivity.this)) {
                        ViewUtility.displayNetworkPreferences(LoginActivity.this);
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
                ViewUtility.showMessage(this, ViewUtility.MSG_ERROR,
                        R.string.activity_login_required_field);
                f.requestFocus();
                return false;
            }
        }
        return true;
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
            saveUser(response.user);
            startMainActivity();
        }

        @Override
        public void onError(LoginService.LoginResponse response) {
            ViewUtility.showMessage(LoginActivity.this, ViewUtility.MSG_ERROR,
                    R.string.activity_login_error);
            loading(false);
        }

        @Override
        public void onCancel() {
            ViewUtility.showMessage(LoginActivity.this, ViewUtility.MSG_ERROR,
                    R.string.activity_login_error);
            loading(false);
        }
    };


    /**
     * Starts the main activity
     */
    private void startMainActivity() {
        Intent main = new Intent(getBaseContext(), MainActivity.class);
        main.putExtra(MainActivity.ARGS_DEFAULT_HOME, getString(R.string.string_site_home));
        startActivity(main);
    }

    /**
     * Saves the user response
     *
     * @param user - UserEntity user
     */
    private void saveUser(UserEntity user) {
        if (user == null)
            return;
        try {
            LogUtil.d(TAG, "USER: " + user.getAdminToken() + ":" + user.getAdminRol() + ":" +
                    user.getIacId());
            AppPreferences.setIacId(this, user.getIacId());
            AppPreferences.setAdminToken(this, user.getAdminToken());
            AppPreferences.setRole(this, user.getAdminRol());
        } catch (Exception e) {
            LogUtil.e(LoginActivity.class.getName(), "Can not set property on User", e);
        }
    }
}