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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.model.LoginService;
import com.ievolutioned.iac.model.UserService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;

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
        if (!AppConfig.DEBUG)
            Crashlytics.start(this);
        setContentView(R.layout.activity_login);
        bindUI();
        mEmail.setText("12345678");
        mPassword.setText("12345678");

        String uuid = AppConfig.getUUID(this);
        String at = AppPreferences.getAdminToken(this);
        int inquestId = 9;
        String iacId = "32006988";

        JsonElement json = new JsonParser().parse("{\"response\":{\"gained\":\"No\",\"newJob\":\"\",\"salary\":\"Si\",\"values\":\"Nada\",\"bossWhy\":\"mala onda\",\"opinion\":\"Regular\",\"benefits\":\"Regular\",\"identity\":\"Nada\",\"training\":\"Malo\",\"gainedWhy\":\"\",\"newSalary\":\"\",\"salaryWhy\":\"\",\"teammates\":\"Bueno\",\"unionized\":\"Malo\",\"workplace\":\"Regular\",\"employeeID\":\"32006785\",\"newCompany\":\"\",\"opinionWhy\":\"\",\"trainingOk\":\"Si\",\"benefitsWhy\":\"\",\"bossOpinion\":\"Malo\",\"otherReason\":\"\",\"trainingWhy\":\"por que no me enseñaron nada\",\"unionizedWhy\":\"¿¿ funcionas,no tengo idea tu si ?????\",\"workPromises\":\"No\",\"departmentWhy\":\"\",\"reasonToLeave\":\"Matrimonio\",\"trainingOkWhy\":\"\",\"humanResources\":\"Regular\",\"workInteresting\":\"Si\",\"dateOfWithdrawal\":\"04/10/2015\",\"trainingReceived\":\"Si\",\"departmentOpinion\":\"Regular\",\"humanResourcesWhy\":\"\",\"salaryExpectations\":\"No\",\"trainingReceivedWhy\":\"\",\"humanResourcesService\":\"No\",\"workInterestingAnswer\":\"123 Por ser asi\",\"departmentWithMostRelation\":\"Ingeniería\"}}");

        JsonObject j = new JsonObject();
        j.addProperty("inquest_id", "9");
        j.addProperty("iac_id", "32000011");
        j.add("user_response", json);

        UserService us = new UserService(AppConfig.getUUID(this), AppPreferences.getAdminToken(this));
        us.create(inquestId, iacId, j.getAsJsonObject().toString(), new UserService.ServiceHandler() {
            @Override
            public void onSuccess(UserService.UserResponse response) {
                LogUtil.d(MainActivity.class.getName(), response.msg);
            }

            @Override
            public void onError(UserService.UserResponse response) {
                LogUtil.e(MainActivity.class.getName(), response.msg, response.e);
            }

            @Override
            public void onCancel() {

            }
        });


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
            mSpinner.setVisibility(View.VISIBLE);
        else
            mSpinner.setVisibility(View.GONE);
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

    private void startMainActivity() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

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