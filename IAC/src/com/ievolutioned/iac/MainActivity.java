package com.ievolutioned.iac;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ievolutioned.iac.adapter.MenuDrawerListAdapter;
import com.ievolutioned.iac.view.MenuDrawerItem;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListForm;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] menuFormTitles;
    private String[] menuSitesTitles;

    private ArrayList<MenuDrawerItem> drawerFormItems;
    private ArrayList<MenuDrawerItem> drawerSitesItems;

    private MenuDrawerListAdapter adapter_forms;
    private MenuDrawerListAdapter adapter_sites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        bindUI();
    }

    private void bindUI() {
        // find view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        //mDrawerListForm = (ListView) findViewById(R.id.activity_main_menu_forms);

        // menu items

        //menuFormTitles = getResources().getStringArray(R.array.nav_drawer_form_items);
        //menuSitesTitles = getResources().getStringArray(R.array.nav_drawer_form_items);

        //drawerFormItems = new ArrayList<MenuDrawerItem>();
        //drawerSitesItems = new ArrayList<MenuDrawerItem>();

        /*
        for (int i = 0; i < menuFormTitles.length; i++) {
            drawerFormItems.add(new MenuDrawerItem(menuFormTitles[i]));
        }

        adapter_forms = new MenuDrawerListAdapter(this, drawerFormItems);
        mDrawerListForm.setAdapter(adapter_forms);

        mDrawerListForm.setOnItemClickListener(drawer_click_form);
        */

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


    AdapterView.OnItemClickListener drawer_click_form = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}
