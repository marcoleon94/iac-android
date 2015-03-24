package com.ievolutioned.iac;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.ievolutioned.iac.fragment.FormsFragment;
import com.ievolutioned.iac.fragment.SitesFragment;
import com.ievolutioned.iac.util.AppConfig;


public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppConfig.DEBUG)
            Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        bindUI();
    }

    private void bindUI() {
        // find view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);

        setDrawer();
    }

    private void setDrawer() {

        final CharSequence mTitle, mDrawerTitle;
        mTitle = mDrawerTitle = getTitle();


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    public void selectItem(String item) {
        String[] forms = getResources().getStringArray(R.array.nav_drawer_form_items);
        String[] sites = getResources().getStringArray(R.array.nav_drawer_sites_items);
        Fragment mFragment = null;
        Bundle args = new Bundle();
        for (String form : forms) {
            if (form.equalsIgnoreCase(item)) {
                mFragment = new FormsFragment();
                args.putString(FormsFragment.ARG_FORM_NAME, item);
            }
        }
        for (String site : sites) {
            if (site.equalsIgnoreCase(item)) {
                mFragment = new SitesFragment();
                args.putString(SitesFragment.ARG_SITE_NAME, item);
            }
        }
        if (mFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_container, mFragment).commit();
            setTitle(item);
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }


}
