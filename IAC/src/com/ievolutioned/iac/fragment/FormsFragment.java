package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.adapters.PXFAdapter;
import com.ievolutioned.pxform.PXFParser;

/**
 * Created by Daniel on 24/03/2015. For project IAC
 */
public class FormsFragment extends Fragment {

    /**
     * Argument key for bundle extras
     */
    public static final String ARG_FORM_NAME = "ARG_FORM_NAME";
    /**
     * PXFParser parser
     */
    private PXFParser p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forms, container, false);
        bindUI(root);
        return root;
    }

    /**
     * Binds the User interface
     *
     * @param root
     */
    private void bindUI(View root) {
        Bundle args = this.getArguments();
        if (args == null)
            return;

        bindData(root, args.getString(ARG_FORM_NAME));
    }

    /**
     * Binds the data from the form
     *
     * @param root - layout
     * @param form - the form identifier
     */
    private void bindData(View root, String form) {
        final ListView listView = (ListView) root.findViewById(R.id.PXForm_linearPanel);
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        String json = getJsonFileName(getActivity(), form);

        p = new PXFParser(new PXFParser.PXFParserEventHandler() {
            @Override
            public void finish(PXFAdapter adapter, String json) {
                listView.setAdapter(adapter);
                loading.dismiss();
            }

            @Override
            public void error(Exception ex, String json) {
                Toast.makeText(getActivity(), "can't parse json", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });

        p.parseJson(getActivity(), PXFParser.parseFileToString(getActivity(), json));
    }

    /**
     * Gets the JSON file name from resources key-value
     *
     * @param c    - Context
     * @param form - name of the form
     * @return the JSON file name
     */
    private String getJsonFileName(Context c, String form) {
        String[] forms = c.getResources().getStringArray(R.array.forms_item_key);
        String[] values = c.getResources().getStringArray(R.array.forms_item_values);
        for (int i = 0; i < forms.length; i++)
            if (forms[i].equalsIgnoreCase(form))
                return values[i];
        return null;
    }

}
