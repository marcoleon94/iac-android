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

        if(a instanceof MainActivity){
            ma = (MainActivity)a;
            ma.setToolbarNavigationOnClickListener(callback);
        }
    }

    /**
     * set the home icon to back arrow icon
     */
    protected void setDisplayHomeAsUpEnabled(){
        Activity a = getActivity();
        MainActivity ma;

        if(a instanceof MainActivity){
            ma = (MainActivity)a;
            ma.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ma.DrawerToggleSynchronizeState();
        }
    }
}
