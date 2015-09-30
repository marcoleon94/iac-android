package com.ievolutioned.iac.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ievolutioned.iac.R;

/**
 * Password fragment class. Shows the main attributes of my profile
 * and allows the user to change some of them
 */
public class PasswordFragment extends Fragment {

    /**
     * EditText for password field
     */
    private EditText mEditPassword;
    /**
     * EditText for retype password field
     */
    private EditText mEditRepassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_password, container, false);
        bindUI(root);
        return root;
    }

    /**
     * Binds the User interface
     */
    private void bindUI(View root) {
        mEditPassword = (EditText) root.findViewById(R.id.fragment_password);
        mEditRepassword = (EditText) root.findViewById(R.id.fragment_password_retype);
    }


    /**
     * Gets the password
     *
     * @return - the password
     */
    public String getPassword() {
        if (mEditPassword != null && !TextUtils.isEmpty(mEditPassword.getText()))
            return mEditPassword.getText().toString();
        return null;
    }

    /**
     * Gets the password retyped
     *
     * @return - the password retyped
     */
    public String getRepassword() {
        if (mEditRepassword != null && !TextUtils.isEmpty(mEditRepassword.getText()))
            return mEditRepassword.getText().toString();
        return null;
    }
}
