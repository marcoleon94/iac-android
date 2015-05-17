package com.ievolutioned.iac.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.adapter.MenuDrawerListAdapter;
import com.ievolutioned.iac.net.service.FormService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.FileUtil;
import com.ievolutioned.iac.view.MenuDrawerItem;
import com.ievolutioned.pxform.database.Forms;
import com.ievolutioned.pxform.database.FormsDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 23/03/2015.
 */
public class MenuFragment extends Fragment {

    private String[] menuFormTitles;
    private String[] menuSitesTitles;

    private ListView mDrawerListForm;
    private ListView mDrawerSiteForm;

    private ArrayList<MenuDrawerItem> drawerFormItems = new ArrayList<MenuDrawerItem>();

    private ArrayList<MenuDrawerItem> drawerSitesItems = new ArrayList<MenuDrawerItem>();


    private MenuDrawerListAdapter adapter_forms;
    private MenuDrawerListAdapter adapter_sites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container);
        bindUI(root);
        bindData();
        return root;
    }

    private void bindUI(View root) {
        //Find UI
        mDrawerListForm = (ListView) root.findViewById(R.id.fragment_menu_form_list);
        mDrawerSiteForm = (ListView) root.findViewById(R.id.fragment_menu_site_list);

        //Loads the static menu items for the current adapters
        loadStaticMenuItems();

        //Initialize adapters
        adapter_forms = new MenuDrawerListAdapter(getActivity(), drawerFormItems);
        adapter_sites = new MenuDrawerListAdapter(getActivity(), drawerSitesItems);
        mDrawerListForm.setAdapter(adapter_forms);
        mDrawerSiteForm.setAdapter(adapter_sites);

        //Set on click listeners
        mDrawerListForm.setOnItemClickListener(drawer_click);
        mDrawerSiteForm.setOnItemClickListener(drawer_click);
    }

    /**
     * Loads the static menu items from resources
     */
    private void loadStaticMenuItems() {
        // static menu items
        menuFormTitles = getResources().getStringArray(R.array.nav_drawer_form_items);
        menuSitesTitles = getResources().getStringArray(R.array.nav_drawer_sites_items);

        for (String m : menuFormTitles) {
            drawerFormItems.add(new MenuDrawerItem(m));
        }

        for (String m : menuSitesTitles) {
            drawerSitesItems.add(new MenuDrawerItem(m));
        }
    }

    /**
     * Binds the dynamic menu objects form service
     */
    private void bindData() {
        FormService fs = new FormService(AppConfig.getUUID(getActivity()),
                AppPreferences.getAdminToken(getActivity()));
        fs.getForms(form_service_callback);
    }

    /**
     * Handles the service callback
     */
    private FormService.ServiceHandler form_service_callback = new FormService.ServiceHandler() {
        @Override
        public void onSuccess(FormService.FormResponse response) {
            drawerFormItems.addAll(getTitlesFromResponse(response.json));
            adapter_forms.notifyDataSetChanged();
            save(response.json);
            showLoading(false);
        }

        public void save(JsonElement response) {
            boolean isSaved = false;
            com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(getActivity());
            List<Forms> forms = f.selectAll();
            JsonArray inquests = response.getAsJsonArray();
            for (JsonElement i : inquests) {
                isSaved = false;
                JsonObject jo = i.getAsJsonObject();
                for (Forms form : forms) {
                    if (form.getName().equalsIgnoreCase(jo.get("name").getAsString()))
                    {
                        isSaved = true;
                        break;
                    }
                }
                if (!isSaved) {
                    f.insert("CACHE", jo.get("name").getAsString());
                    FileUtil.saveJsonFile(getActivity(),jo.get("name").getAsString(),jo.getAsJsonObject().toString());
                }
                else {
                    //TODO: f.update()
                }
            }
            f.close();
        }

        @Override
        public void onError(FormService.FormResponse response) {
            showLoading(false);
        }

        @Override
        public void onCancel() {
            showLoading(false);
        }
    };

    /**
     * Gets the menu titles from response
     *
     * @param response
     * @return a list of menu items
     */
    protected List<MenuDrawerItem> getTitlesFromResponse(JsonElement response) {
        List<MenuDrawerItem> items = new ArrayList<MenuDrawerItem>();
        JsonArray inquests = response.getAsJsonArray();
        for (JsonElement i : inquests) {
            JsonObject jo = i.getAsJsonObject();
            items.add(new MenuDrawerItem(jo.get("id").getAsLong(), jo.get("name").getAsString()));
        }
        return items;
    }


    AdapterView.OnItemClickListener drawer_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            selectItem(adapterView.getId() == R.id.fragment_menu_form_list ? drawerFormItems.get(position) :
                    drawerSitesItems.get(position));
        }
    };

    protected void selectItem(MenuDrawerItem item) {
        Log.d(MenuFragment.class.getName(), "Selected: " + item);
        ((MainActivity) getActivity()).selectItem(item.getId(), item.getTitle());
    }

    protected void showLoading(boolean b) {
        ((MainActivity) getActivity()).showLoading(b);
    }



}
