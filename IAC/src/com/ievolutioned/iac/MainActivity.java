package com.ievolutioned.iac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.internal.aD;
import com.ievolutioned.iac.adapter.MenuDrawerListAdapter;
import com.ievolutioned.iac.view.MenuDrawerItem;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListForm;

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
        mDrawerListForm = (ListView) findViewById(R.id.activity_main_menu_forms);
        // menu items

        menuFormTitles = getResources().getStringArray(R.array.nav_drawer_form_items);
        menuSitesTitles = getResources().getStringArray(R.array.nav_drawer_form_items);

        drawerFormItems = new ArrayList<MenuDrawerItem>();
        drawerSitesItems = new ArrayList<MenuDrawerItem>();

        for (int i = 0; i < menuFormTitles.length; i++) {
            drawerFormItems.add(new MenuDrawerItem(menuFormTitles[i]));
        }

        adapter_forms = new MenuDrawerListAdapter(this, drawerFormItems);
        mDrawerListForm.setAdapter(adapter_forms);

        mDrawerListForm.setOnItemClickListener(drawer_click_form);

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
