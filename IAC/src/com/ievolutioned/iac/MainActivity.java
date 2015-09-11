package com.ievolutioned.iac;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.fragment.FormsFragment;
import com.ievolutioned.iac.fragment.SitesFragment;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FileUtil;
import com.ievolutioned.iac.view.MenuDrawerItem;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.database.FormsDataSet;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/*
https://github.com/excilys/androidannotations
 */

/**
 *
 */
public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private AlertDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        bindUI();
        setToolbarNavigationOnClickListener(mainActivityHomeButton);
    }

    private void bindUI() {
        // find view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        mLoading = ViewUtility.getLoadingScreen(this);
        showLoading(true);
        setDrawer();
        showHome();
    }

    private final View.OnClickListener mainActivityHomeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mDrawerLayout.isDrawerOpen(R.layout.fragment_menu)) {
                mDrawerLayout.closeDrawers();
                return;
            }

            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.activity_main_frame_container);
            if (fragment instanceof SitesFragment) {
                mDrawerLayout.openDrawer(R.layout.fragment_menu);
                return;
            } else if (fragment instanceof FormsFragment) {
                if (fragment.getTag() == null) {
                    mDrawerLayout.openDrawer(R.layout.fragment_menu);
                    return;
                } else
                    onBackPressed();
            }
        }
    };

    private void showHome() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment mFragment = new SitesFragment();
        Bundle args = new Bundle();
        args.putString(SitesFragment.ARG_SITE_NAME, getString(R.string.string_site_home));
        mFragment.setArguments(args);
        replaceFragment(mFragment);
    }

    private void setDrawer() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //disable static back arrow
        DrawerToggleSynchronizeState(); //refresh all menu state
    }

    /**
     * Method to take control of the <b>HOME</b> button of the navigation bar, used for
     * custom user experience
     *
     * @param callback method to be linked to the event
     */
    public void setToolbarNavigationOnClickListener(View.OnClickListener callback) {
        mToolbar.setNavigationOnClickListener(callback);
    }

    /**
     * call to synchronize the <b>HOME</b> menu icon drawer
     *
     * @see com.ievolutioned.iac.fragment.BaseFragmentClass
     */
    public void DrawerToggleSynchronizeState() {
        mDrawerToggle.syncState(); //refresh all menu state
    }

    /**
     * Including the dynamic form menu, sets its fragment for the current menu item
     * is called by {@link com.ievolutioned.iac.fragment.MenuFragment}
     */
    public void selectItem(long id, String item) {
        if (id == MenuDrawerItem.ID_DEFAULT)
            selectItem(item);
        else {
            // Only for dynamic form menu
            Fragment mFragment = new FormsFragment();
            Bundle args = new Bundle();

            com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(MainActivity.this);
            List<com.ievolutioned.pxform.database.Forms> formsList = f.selectByName(item);
            f.close();

            if (formsList.size() > 0) {

                JsonElement jsonElement = new JsonParser().parse(FileUtil.readJsonFile(this,
                        formsList.get(0).getName()));
                String jsonArray = jsonElement.getAsJsonObject().get("content").getAsJsonArray().toString();

                args.putString(FormsFragment.ARG_FORM_NAME, item);
                args.putLong(FormsFragment.ARGS_FORM_ID, id);
                args.putLong(FormsFragment.DATABASE_FORM_ID, formsList.get(0).getId());
                args.putInt(FormsFragment.DATABASE_LEVEL, 0);
                args.putString(FormsFragment.DATABASE_KEY_PARENT, "");
                //args.putString(FormsFragment.DATABASE_JSON,com.ievolutioned.pxform.PXFParser.parseFileToString(MainActivity.this,"Form.json"));
                args.putString(FormsFragment.DATABASE_JSON, jsonArray);
                mFragment = new FormsFragment();

                Bundle b = new Bundle();
                b.putBundle(FormsFragment.class.getName(), args);
                mFragment.setArguments(b);
            }

            replaceFragment(mFragment);
        }
        mDrawerLayout.closeDrawers();
    }

    /**
     * Looks for a static item menu string and sets the menu title and its fragment
     */
    public void selectItem(String item) {
        String[] forms = getResources().getStringArray(R.array.nav_drawer_form_items);
        String[] sites = getResources().getStringArray(R.array.nav_drawer_sites_items);
        Fragment mFragment = null;
        Bundle args = new Bundle();

        for (String form : forms) {
            if (form.equalsIgnoreCase(item)) {
                args.putString(FormsFragment.ARG_FORM_NAME, item);
                args.putLong(FormsFragment.DATABASE_FORM_ID, 0);
                args.putInt(FormsFragment.DATABASE_LEVEL, 0);
                args.putString(FormsFragment.DATABASE_KEY_PARENT, "");
                args.putString(FormsFragment.DATABASE_JSON,
                        com.ievolutioned.pxform.PXFParser.parseFileToString(MainActivity.this,
                                "Form.json"));
                mFragment = new FormsFragment();
                Bundle b = new Bundle();
                b.putBundle(FormsFragment.class.getName(), args);
                mFragment.setArguments(b);
                break;
            }
        }

        //if fragment already found skip sites
        if (mFragment == null) {
            for (String site : sites) {
                if (site.equalsIgnoreCase(item)) {
                    mFragment = new SitesFragment();
                    args.putString(SitesFragment.ARG_SITE_NAME, item);
                    mFragment.setArguments(args);
                    break;
                }
            }
        }

        if (mFragment != null) {
            replaceFragment(mFragment);
        }
    }

    /**
     * Replaces the current fragment on the frame container.
     * <br>This can be call from the {@link FormsFragment fragments childs }
     */
    public void replaceFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_main_frame_container, mFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //mDrawerToggle.syncState();
        DrawerToggleSynchronizeState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        //super.setTitle(title);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.activity_main_frame_container);
            if (fragment != null)
                if (fragment instanceof SitesFragment)
                    ((SitesFragment) fragment).onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();

            //call setDrawer always when there is not other fragment
            //to retake control of the navigation
            if (fragmentManager.getBackStackEntryCount() < 1) {
                setDrawer();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void showLoading(boolean b) {
        if (mLoading != null)
            if (b)
                mLoading.show();
            else
                mLoading.dismiss();
    }
}
