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
import com.google.gson.JsonParser;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.adapter.MenuDrawerListAdapter;
import com.ievolutioned.iac.entity.UserRole;
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
 * MenuFragment class, represents the main options of a menu that would be displayed on a drawer
 * <p/>
 * Created by Daniel on 23/03/2015.
 */
public class MenuFragment extends Fragment {

    /**
     * TAG
     */
    public static final String TAG = MenuFragment.class.getName();

    /**
     * ARGS for menu items
     */
    private static final String ARGS_MENU_ITEMS = "ARGS_MENU_ITEMS";
    /**
     * Saved state
     */
    private Bundle savedState;
    /**
     * Static form titles
     */
    private String[] menuFormTitles;
    /**
     * Static menu titles
     */
    private String[] menuSitesTitles;

    /**
     * ListView list form
     */
    private ListView mDrawerListForm;

    /**
     * Drawer form items
     */
    private ArrayList<MenuDrawerItem> drawerFormItems = new ArrayList<MenuDrawerItem>();

    /**
     * MenuDrawerListAdapter adapter for form list
     */
    private MenuDrawerListAdapter adapter_forms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container);
        bindUI(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!restoreFromState(savedInstanceState)) {
            // First run
            bindData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //saveState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(TAG, saveState());
    }

    /**
     * Restores the state from Bundle
     *
     * @param state - Bundle state
     * @return true if it is restored, false otherwise
     */
    private boolean restoreFromState(Bundle state) {
        if (state != null) {
            savedState = state.getBundle(TAG);
        }
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    /**
     * Restore state for menu items
     */
    private void restoreState() {
        if (savedState != null) {
            String items = savedState.getString(ARGS_MENU_ITEMS);
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(items);
            drawerFormItems.addAll(getTitlesFromResponse(json));
            adapter_forms.notifyDataSetChanged();
            showLoading(false);
        }
    }

    /**
     * Save state for menu items
     *
     * @return Bundle saved state
     */
    private Bundle saveState() {
        //Get the items from drawerFormItems
        JsonArray json = new JsonArray();
        for (MenuDrawerItem i : drawerFormItems) {
            JsonObject o = new JsonObject();
            o.addProperty("id", i.getId());
            o.addProperty("name", i.getTitle());
            json.add(o);
        }
        //Set json to bundle
        if (savedState == null)
            savedState = new Bundle();

        savedState.putString(ARGS_MENU_ITEMS, json.getAsJsonArray().toString());
        return savedState;
    }


    /**
     * Binds the main UI
     *
     * @param root - View root
     */
    private void bindUI(View root) {
        //Find UI
        mDrawerListForm = (ListView) root.findViewById(R.id.fragment_menu_form_list);

        //Initialize adapters
        adapter_forms = new MenuDrawerListAdapter(getActivity(), drawerFormItems);
        mDrawerListForm.setAdapter(adapter_forms);

        //Set on click listeners
        mDrawerListForm.setOnItemClickListener(drawer_click);
        root.findViewById(R.id.fragment_menu_home).setOnClickListener(menu_click);
        root.findViewById(R.id.fragment_menu_profile).setOnClickListener(menu_click);
        root.findViewById(R.id.fragment_menu_singout).setOnClickListener(menu_click);
    }

    /**
     * Binds the dynamic menu objects form service
     */
    private void bindData() {
        //Loads the static menu items for the current adapters
        loadStaticMenuItems();
        //Look for forms on service
        FormService fs = new FormService(AppConfig.getUUID(getActivity()),
                AppPreferences.getAdminToken(getActivity()));
        fs.getForms(AppPreferences.getAdminToken(getActivity()), form_service_callback);
    }

    /**
     * Loads the static menu items from resources
     */
    private void loadStaticMenuItems() {
        // static menu items
        menuFormTitles = getResources().getStringArray(R.array.nav_drawer_form_items);
        menuSitesTitles = getResources().getStringArray(R.array.nav_drawer_sites_items);

        //TODO: how to determine static forms for users
        if (!AppPreferences.getRole(getActivity()).contentEquals(UserRole.USER)
                && menuFormTitles != null)
            for (String m : menuFormTitles) {
                drawerFormItems.add(new MenuDrawerItem(m));
            }
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

        /**
         * Saves the response
         * @param response - JsonElement response
         */
        public void save(JsonElement response) {
            boolean isSaved = false;
            com.ievolutioned.pxform.database.FormsDataSet f = new FormsDataSet(getActivity());
            List<Forms> forms = f.selectAll();
            JsonArray inquests = response.getAsJsonArray();
            for (JsonElement i : inquests) {
                isSaved = false;
                JsonObject jo = i.getAsJsonObject();
                for (Forms form : forms) {
                    if (form.getName().equalsIgnoreCase(jo.get("name").getAsString())) {
                        isSaved = true;
                        break;
                    }
                }
                if (!isSaved) {
                    f.insert("CACHE", jo.get("name").getAsString());
                    FileUtil.saveJsonFile(getActivity(), jo.get("name").getAsString(), jo.getAsJsonObject().toString());
                } else {
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


    /**
     * Click listener for select item
     */
    private AdapterView.OnItemClickListener drawer_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            selectItem(drawerFormItems.get(position));
        }
    };

    /**
     * Click listener for select item
     */
    private View.OnClickListener menu_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_menu_home:
                    ((MainActivity) getActivity()).selectItem(menuSitesTitles[0]);
                    break;
                case R.id.fragment_menu_profile:
                    ((MainActivity) getActivity()).showMyProfile();
                    break;
                case R.id.fragment_menu_singout:
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Selects an item from the menu
     *
     * @param item - The item
     */
    protected void selectItem(MenuDrawerItem item) {
        Log.d(MenuFragment.class.getName(), "Selected: " + item);
        ((MainActivity) getActivity()).selectItem(item.getId(), item.getTitle());
    }

    /**
     * Shows the loading message
     *
     * @param b
     */
    protected void showLoading(boolean b) {
        ((MainActivity) getActivity()).showLoading(b);
    }


}
