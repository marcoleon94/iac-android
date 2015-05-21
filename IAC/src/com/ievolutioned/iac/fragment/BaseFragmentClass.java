package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

import com.ievolutioned.iac.MainActivity;

/**
 * Base class for custom navigation fragment
 */
public abstract class BaseFragmentClass  extends Fragment{
    /**
     * Set a custom handler to get <b>HOME</b> touch events from the main tool bar
     * @param callback method that will get the events call
     */
    protected void setToolbarNavigationOnClickListener(View.OnClickListener callback){
        Activity a = getActivity();
        MainActivity ma;

        if( a!= null && a instanceof MainActivity){
            ma = (MainActivity)a;
            ma.setToolbarNavigationOnClickListener(callback);
        }
    }
    /**
     * Set the activity home icon menu to back arrow icon
     */
    protected void setToolbarNavigationDisplayHomeAsUpEnabled(){
        Activity a = getActivity();
        MainActivity ma;

        if( a!= null && a instanceof MainActivity){
            ma = (MainActivity)a;
            ma.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //ma.DrawerToggleSynchronizeState(); //not needed
        }
    }

    /**
     * Call activity to show fragment as main screen
     * @param fragment this will replace the current fragment in the main activity
     */
    protected void setMainActivityReplaceFragment(Fragment fragment){
        Activity a = getActivity();
        MainActivity ma;

        if(fragment != null && a!= null && a instanceof MainActivity) {
            ma = (MainActivity)a;
            ma.replaceFragment(fragment);
        }
    }
}
