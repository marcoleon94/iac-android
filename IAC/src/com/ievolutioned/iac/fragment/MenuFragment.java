package com.ievolutioned.iac.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.adapter.MenuDrawerListAdapter;
import com.ievolutioned.iac.view.MenuDrawerItem;

import java.util.ArrayList;

/**
 * Created by Daniel on 23/03/2015.
 */
public class MenuFragment extends Fragment {

    private String[] menuFormTitles;
    private String[] menuSitesTitles;

    private ListView mDrawerListForm;
    private ListView mDrawerSiteForm;

    private ArrayList<MenuDrawerItem> drawerFormItems;
    private ArrayList<MenuDrawerItem> drawerSitesItems;

    private MenuDrawerListAdapter adapter_forms;
    private MenuDrawerListAdapter adapter_sites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container);
        bindUI(root);
        return root;
    }

    private void bindUI(View root) {

        mDrawerListForm = (ListView) root.findViewById(R.id.fragment_menu_form_list);
        mDrawerSiteForm = (ListView) root.findViewById(R.id.fragment_menu_site_list);

        // menu items
        menuFormTitles = getResources().getStringArray(R.array.nav_drawer_form_items);
        menuSitesTitles = getResources().getStringArray(R.array.nav_drawer_sites_items);

        drawerFormItems = new ArrayList<MenuDrawerItem>();
        drawerSitesItems = new ArrayList<MenuDrawerItem>();


        for (int i = 0; i < menuFormTitles.length; i++) {
            drawerFormItems.add(new MenuDrawerItem(menuFormTitles[i]));
        }
        for (int i = 0; i < menuSitesTitles.length; i++) {
            drawerSitesItems.add(new MenuDrawerItem(menuSitesTitles[i]));
        }

        adapter_forms = new MenuDrawerListAdapter(getActivity(), drawerFormItems);
        adapter_sites = new MenuDrawerListAdapter(getActivity(), drawerSitesItems);
        mDrawerListForm.setAdapter(adapter_forms);
        mDrawerSiteForm.setAdapter(adapter_sites);

        mDrawerListForm.setOnItemClickListener(drawer_click_form);
        mDrawerSiteForm.setOnItemClickListener(drawer_click_form);

    }

    AdapterView.OnItemClickListener drawer_click_form = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            selectItem(adapterView.getId() == R.id.fragment_menu_form_list ?
                    menuFormTitles[position] : menuSitesTitles[position]);
        }
    };

    protected void selectItem(String item) {
        Log.d(MenuFragment.class.getName(), "Selected: " + item);
        ((MainActivity)getActivity()).selectItem(item);
    }
}
