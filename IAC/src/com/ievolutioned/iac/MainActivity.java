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
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.ievolutioned.iac.fragment.FormsFragment;
import com.ievolutioned.iac.fragment.SitesFragment;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.FileUtil;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.database.FormsDataSet;

import java.io.File;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    private AlertDialog mLoading;

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
        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        mLoading = ViewUtility.getLoadingScreen(this);
        showLoading(true);
        setDrawer();
    }

    private void setDrawer() {
        //final CharSequence mTitle, mDrawerTitle;
        //mTitle = mDrawerTitle = getTitle();

        setSupportActionBar(mToolbar);

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

    }

    /**
     * Including the dynamic form menu, sets its fragment for the current menu item
     *
     * @param id
     * @param item
     */
    /*
    public void selectItem(long id, String item) {
        if (id == MenuDrawerItem.ID_DEFAULT)
            selectItem(item);
        else {
            // Only for dynamic form menu
            Fragment mFragment = new FormsFragment();
            Bundle args = new Bundle();
            args.putLong(FormsFragment.ARG_FORM_ID, id);
            replaceFragment(mFragment);
        }
        setTitle(item);
        mDrawerLayout.closeDrawers();
    }
    */

    /**
     * Looks for a static item menu string and sets the menu title and its fragment
     *
     *
     */
    public void selectItem(String item) {
        //String[] forms = getResources().getStringArray(R.array.nav_drawer_form_items);
        String[] sites = getResources().getStringArray(R.array.nav_drawer_sites_items);
        Fragment mFragment = null;
        Bundle args = new Bundle();
        //for (String form : forms) {
        //if (form.equalsIgnoreCase(item)) {

        com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(MainActivity.this);
        List<com.ievolutioned.pxform.database.Forms> formsList = f.selectByName(item);
        f.close();

        if (formsList.size() > 0) {
            args.putLong(FormsFragment.DATABASE_FORM_ID, formsList.get(0).getId());
            args.putInt(FormsFragment.DATABASE_LEVEL, 0);
            args.putString(FormsFragment.DATABASE_KEY_PARENT, "");
            //args.putString(FormsFragment.DATABASE_JSON,com.ievolutioned.pxform.PXFParser.parseFileToString(MainActivity.this,"Form.json"));
            args.putString(FormsFragment.DATABASE_JSON, FileUtil.readJsonFile(this,formsList.get(0).getName()));
            mFragment = new FormsFragment();
        }
        //}
        //}
        if (mFragment == null) {
            for (String site : sites) {
                if (site.equalsIgnoreCase(item)) {
                    mFragment = new SitesFragment();
                    args.putString(SitesFragment.ARG_SITE_NAME, item);
                }
            }
        }

        if (mFragment != null) {
            Bundle b = new Bundle();
            b.putBundle(FormsFragment.class.getName(), args);
            mFragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.activity_main_frame_container, mFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            setTitle(item);
            mDrawerLayout.closeDrawers();
        }
    }

    /**
     * Replaces the current fragment on the frame container
     *
     *
     */
    /*
    public void replaceFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_main_frame_container, mFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    */

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

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();
        } else
            super.onBackPressed();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult barcodeResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (barcodeResult != null) {
            Toast.makeText(this,barcodeResult.getContents(),Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }
    */

    public void showLoading(boolean b) {
        if (mLoading != null)
            if (b)
                mLoading.show();
            else
                mLoading.dismiss();
    }
}
