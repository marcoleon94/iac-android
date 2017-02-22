package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;

/**
 * Created by Daniel on 21/02/2017.
 */

public class AttendeesFragment extends BaseFragmentClass {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_courses, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        bindData(getArguments());
        setTitle(getString(R.string.string_fragment_courses_title));
        return root;
    }

    private void bindUI(View root) {

    }

    private void bindData(Bundle args) {

    }

    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            activity.setTitle(title);
    }
}
