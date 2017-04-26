package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ievolutioned.iac.MainActivity;

/**
 * Base class for custom navigation fragment
 */
public abstract class BaseFragmentClass extends Fragment {

    protected AppCompatActivity mAttachedActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            mAttachedActivity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onDetach() {
        mAttachedActivity = null;
        super.onDetach();
    }

    /**
     * Set a custom handler to get <b>HOME</b> touch events from the main tool bar
     *
     * @param callback method that will get the events call
     */
    protected void setToolbarNavigationOnClickListener(View.OnClickListener callback) {
        Activity a = getActivity();
        MainActivity ma;

        if (a != null && a instanceof MainActivity) {
            ma = (MainActivity) a;
            ma.setToolbarNavigationOnClickListener(callback);
        }
    }

    /**
     * Set the activity home icon menu to back arrow icon
     */
    protected void setToolbarNavigationDisplayHomeAsUpEnabled(boolean set) {
        Activity a = getActivity();
        MainActivity ma;

        if (a != null && a instanceof MainActivity) {
            ma = (MainActivity) a;
            ma.getSupportActionBar().setDisplayHomeAsUpEnabled(set);
            if (!set)
                ma.DrawerToggleSynchronizeState(); //not needed
        }
    }

    /**
     * Call activity to show fragment as main screen
     *
     * @param fragment this will replace the current fragment in the main activity
     */
    protected void setMainActivityReplaceFragment(Fragment fragment, String tag) {
        setMainActivityReplaceFragment(fragment, tag, false);
    }

    /**
     * Call activity to show fragment as main screen
     *
     * @param fragment   this will replace the current fragment in the main activity
     * @param tag        TAG
     * @param isAnimated animation shown
     */
    protected void setMainActivityReplaceFragment(Fragment fragment, String tag, boolean isAnimated) {
        Activity a = getActivity();
        MainActivity ma;

        if (fragment != null && a != null && a instanceof MainActivity) {
            ma = (MainActivity) a;
            ma.replaceFragment(fragment, tag, isAnimated);
        }
    }
}
