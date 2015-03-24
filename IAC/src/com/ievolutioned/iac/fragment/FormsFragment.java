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
 * Created by Daniel on 24/03/2015.
 */
public class FormsFragment extends Fragment {

    public static final String ARG_FORM_INDEX = "ARG_FORM_INDEX";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container);
        bindUI(root);
        return root;
    }

    private void bindUI(View root) {
        
    }

}
