package com.ievolutioned.iac;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ievolutioned.iac.fragment.AttendeesFragment;
import com.ievolutioned.iac.fragment.DiningFragment;
import com.ievolutioned.iac.fragment.DiningGuestsFragment;
import com.ievolutioned.iac.fragment.FormsFragment;
import com.ievolutioned.iac.fragment.MyProfileFragment;
import com.ievolutioned.iac.fragment.SitesFragment;
import com.ievolutioned.iac.fragment.TuobaFragment;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.FileUtil;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.util.ViewUtil;
import com.ievolutioned.iac.view.MenuDrawerItem;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.database.FormsDataSet;

import java.util.List;

import io.fabric.sdk.android.Fabric;


/**
 * Main activity class. Allows a main container of Site and Form fragments.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * TAG string
     */
    public static final String TAG = MainActivity.class.getName();

    public static final String ARGS_DEFAULT_HOME = "ARGS_DEFAULT_HOME";
    /**
     * Activity action for pick a picture
     */
    public static final int ACTION_PICK_PHOTO = 1000;
    /**
     * Activity action for take a picture
     */
    public static final int ACTION_TAKE_PHOTO = 2000;
    /**
     * DrawerLayout the main drawer layout for menu options
     */
    private DrawerLayout mDrawerLayout;
    /**
     * ActionBarDrawerToggle drawer toggle for main menu
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * Toolbar main toolbar that performs the main actions
     */
    private Toolbar mToolbar;
    /**
     * AlertDialog loading view
     */
    private AlertDialog mLoading;

    /**
     * For internet check message
     */
    private View mInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppConfig.DEBUG)
            Fabric.with(this, new Crashlytics());
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        bindUI();
        bindData();
        setToolbarNavigationOnClickListener(mainActivityHomeButton);
    }

    /**
     * Binds the user interface on activity created
     */
    private void bindUI() {
        // find view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        mLoading = ViewUtility.getLoadingScreen(this);
        mInternetConnection = findViewById(R.id.activity_main_internet_msg);
        mInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtility.displayNetworkPreferences(MainActivity.this);
            }
        });
        showLoading(true);
        setDrawer();
    }

    /**
     * Binds the data
     */
    private void bindData() {
        Bundle args = getIntent().getExtras();
        if (args != null && !args.isEmpty()) {
            if (args.getString(ARGS_DEFAULT_HOME, null) != null) {
                getIntent().removeExtra(ARGS_DEFAULT_HOME);
                String typeIac = AppPreferences.getTypeIac(this);
                if (typeIac != null && typeIac.contentEquals(AppPreferences.TYPE_IAC_DINING))
                    showDining();
                else
                    showHome();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnection();
    }

    @Override
    protected void onPause() {
        if (mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        super.onPause();
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mHandler != null) {
                checkInternetConnection();
            }
        }
    };

    /**
     * Checks for internet connection
     */
    private void checkInternetConnection() {
        ViewUtil.internetConnectionView(mInternetConnection);
        if (mHandler != null)
            mHandler.postDelayed(mRunnable, 3000);
    }

    /**
     * Performs a set of functions about toolbar navigation
     */
    private final View.OnClickListener mainActivityHomeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Close drawer if it is open
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
                return;
            }
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.activity_main_frame_container);
            if (fragment instanceof MyProfileFragment || fragment instanceof SitesFragment ||
                    fragment instanceof TuobaFragment || fragment instanceof AttendeesFragment ||
                    fragment instanceof DiningFragment) {
                //Open drawer
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else if (fragment instanceof FormsFragment) {
                //Verify if it is a subform or simple selection
                if (fragment.getTag() == null) {
                    //Open drawer
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
            } else if (fragment instanceof DiningGuestsFragment) {
                ((DiningGuestsFragment)fragment).onBackPressed();
            }
        }
    };

    /**
     * Displays the home view as the main view
     */
    public void showHome() {
        Fragment mFragment = new SitesFragment();
        Bundle args = new Bundle();
        args.putString(SitesFragment.ARG_SITE_NAME, getString(R.string.string_site_home));
        mFragment.setArguments(args);
        replaceFragment(mFragment, null);
        mDrawerLayout.closeDrawers();
    }

    /**
     * Displays the courses for admin users
     */
    public void showAttendees() {
        Fragment fragment = new AttendeesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        replaceFragment(fragment, null);
        mDrawerLayout.closeDrawers();
    }

    /**
     * Displays the dining entrance for users
     */
    public void showDining() {
        Fragment fragment = new DiningFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        replaceFragment(fragment, null);
        mDrawerLayout.closeDrawers();
    }

    /**
     * Displays the profile view in the main container
     */
    public void showMyProfile() {
        Fragment mFragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(SitesFragment.ARG_SITE_NAME, getString(R.string.string_site_home));
        mFragment.setArguments(args);
        replaceFragment(mFragment, null);
        mDrawerLayout.closeDrawers();
    }

    /**
     * Sets the main drawer
     */
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
            Bundle args;

            com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(MainActivity.this);
            List<com.ievolutioned.pxform.database.Forms> formsList = f.selectByName(item);
            f.close();

            //Sets the bundle for forms
            if (formsList.size() > 0) {
                JsonElement jsonElement = new JsonParser().parse(FileUtil.readJsonFile(this,
                        formsList.get(0).getName()));
                String jsonArray = jsonElement.getAsJsonObject().get("content").getAsJsonArray().toString();
                args = getFragmentArgs(item, id, formsList.get(0).getId(), 0, "", jsonArray);
                mFragment = new FormsFragment();
                Bundle b = new Bundle();
                b.putBundle(FormsFragment.class.getName(), args);
                mFragment.setArguments(b);
            }

            replaceFragment(mFragment, null);
        }
        mDrawerLayout.closeDrawers();
    }


    /**
     * Gets the fragment arguments from the current parameters
     *
     * @param item
     * @param dbId
     * @param level
     * @param key
     * @param array
     * @return Bundle args
     */
    private Bundle getFragmentArgs(String item, long dbId, int level, String key, String array) {
        Bundle args = new Bundle();
        args.putString(FormsFragment.ARG_FORM_NAME, item);
        args.putLong(FormsFragment.DATABASE_FORM_ID, dbId);
        args.putInt(FormsFragment.DATABASE_LEVEL, level);
        args.putString(FormsFragment.DATABASE_KEY_PARENT, key);
        args.putString(FormsFragment.DATABASE_JSON, array);
        return args;

    }

    /**
     * Gets the fragment arguments from the current parameters
     *
     * @param item
     * @param id
     * @param dbId
     * @param level
     * @param key
     * @param array
     * @return Bundle args
     */
    private Bundle getFragmentArgs(String item, long id, long dbId, int level, String key,
                                   String array) {
        Bundle args = getFragmentArgs(item, dbId, level, key, array);
        args.putLong(FormsFragment.ARGS_FORM_ID, id);
        return args;
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
                args = getFragmentArgs(item, 0, 0, "",
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
            replaceFragment(mFragment, null);
        }
        mDrawerLayout.closeDrawers();
    }

    /**
     * Replaces the current fragment on the frame container.
     * <br>This can be call from the {@link FormsFragment fragments childs }
     */
    public void replaceFragment(Fragment fragment, String tag) {
        replaceFragment(fragment, tag, false);
    }

    /**
     * Replaces the fragment in the main activity frame
     *
     * @param fragment   - Fragment to be replaced
     * @param tag        - TAG attribute
     * @param isAnimated - Shows animation or not.
     */
    public void replaceFragment(Fragment fragment, String tag, boolean isAnimated) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isAnimated)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.activity_main_frame_container, fragment, tag);
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
        //Catches the keycode back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_container);
            if (fragment != null)
                if (fragment instanceof SitesFragment) {
                    ((SitesFragment) fragment).onBackPressed();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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

    /**
     * Show the AlertDialog mLoading
     *
     * @param b alert dialog shown if true.
     */
    public void showLoading(boolean b) {
        if (mLoading != null)
            if (b)
                mLoading.show();
            else
                mLoading.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case ACTION_PICK_PHOTO:
                case ACTION_TAKE_PHOTO:
                    setPictureOnProfileFragment(data, requestCode);
                    break;
                case SitesFragment.INPUT_FILE_REQUEST_CODE:
                    manageInputFile(data, requestCode);
                default:
                    break;
            }
    }

    /**
     * Looks the fragment site to manage input file
     *
     * @param data        - Intent data result
     * @param requestCode - request code
     */
    private void manageInputFile(Intent data, int requestCode) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_main_frame_container);
        if (fragment instanceof SitesFragment)
            ((SitesFragment) fragment).manageInputFile(data, requestCode);

    }

    /**
     * Sets the profile on profile fragment about the data
     *
     * @param data        - Intent data
     * @param requestCode - request code
     */
    private void setPictureOnProfileFragment(Intent data, int requestCode) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.activity_main_frame_container);
        if (f == null)
            return;
        else if (f instanceof MyProfileFragment)
            ((MyProfileFragment) f).setImageByIntent(data, requestCode);
        LogUtil.d(TAG, data.toString());
    }

    /**
     * Shows the about fragment
     */
    public void showAbout() {
        Fragment mFragment = new TuobaFragment();
        replaceFragment(mFragment, null);
        mDrawerLayout.closeDrawers();
    }


}
