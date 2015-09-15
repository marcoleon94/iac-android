package com.ievolutioned.iac.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ievolutioned.iac.R;

/**
 */
public class PasswordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = bindUI(inflater.inflate(R.layout.fragment_password, container, false));
        //setHasOptionsMenu(true);
        return root;
    }

    /**
     * Binds the User interface
     */
    private View bindUI(View root) {
        return root;
    }

}
