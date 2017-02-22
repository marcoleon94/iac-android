package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;

/**
 * Created by Daniel on 21/02/2017.
 */

public class AttendeesFragment extends BaseFragmentClass {

    private Spinner mCoursesSpinner;
    private ArrayAdapter<String> coursesSpinnerAdapter;
    private String[] mCourses = new String[]{"Seleccione"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_attendees, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        bindData(getArguments());
        setTitle(getString(R.string.string_fragment_attendees_title));
        return root;
    }

    private void bindUI(View root) {
        if (root == null)
            return;
        mCoursesSpinner = (Spinner) root.findViewById(R.id.fragment_attendees_courses_spinner);

        if (mCoursesSpinner != null) {
            coursesSpinnerAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.list_item_right, R.id.list_item_right_text, mCourses);
            mCoursesSpinner.setAdapter(coursesSpinnerAdapter);
            mCoursesSpinner.setGravity(Gravity.END);
            coursesSpinnerAdapter.setDropDownViewResource(R.layout.item_dropdown_right);
        }

    }

    private void bindData(Bundle args) {
        
    }

    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            activity.setTitle(title);
    }

}
