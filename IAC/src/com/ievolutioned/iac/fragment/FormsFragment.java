package com.ievolutioned.iac.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ievolutioned.iac.R;

/**
 * Created by Daniel on 24/03/2015.
 */
public class FormsFragment extends Fragment {

    public static final String ARG_FORM_NAME = "ARG_FORM_NAME";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forms, container, false);
        bindUI(root);
        return root;
    }

    private void bindUI(View root) {
        Bundle args = this.getArguments();
        if (args == null)
            return;

        bindData(root, args.getString(ARG_FORM_NAME));
    }

    private void bindData(View root, String form) {

    }

}
