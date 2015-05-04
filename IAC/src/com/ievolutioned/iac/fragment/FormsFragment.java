package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.PXFButton;
import com.ievolutioned.pxform.PXFParser;
import com.ievolutioned.pxform.adapters.PXFAdapter;

/**
 * Created by Daniel on 24/03/2015. For project IAC
 */
public class FormsFragment extends Fragment {

    /**
     * Argument key for bundle extras
     */
    public static final String ARG_FORM_NAME = "ARG_FORM_NAME";

    public static final String ARG_FORM_ID = "ARG_FORM_ID";

    public static final String ARG_LIST_FORM = "ARG_LIST_FORM";
    /**
     * PXFParser parser
     */

    private ListView listView;

    private PXFParser p;

    private Bundle savedState;

    /*
    Button special cases?
     */
    private Button mButtonBarCode;

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
        listView = (ListView) root.findViewById(R.id.PXForm_linearPanel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // restore sate
        if (!restoreStateFromArgs()) {
            // first run
            bindData(getArguments().getString(ARG_FORM_NAME));
        }
    }

    private boolean restoreStateFromArgs() {
        Bundle b = getArguments();
        savedState = b.getBundle(FormsFragment.class.getName());
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    private void restoreState() {
        if (savedState != null) {
            // Call the restore
            PXFAdapter adapter = savedState.getParcelable(ARG_LIST_FORM);
            adapter.setActivity(getActivity());
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save state
        saveSateToArgs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveSateToArgs();
    }

    private void saveSateToArgs() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle args = getArguments();
            args.putBundle(FormsFragment.class.getName(), savedState);
        }
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        // save the current state
        state.putParcelable(ARG_LIST_FORM, (PXFAdapter) listView.getAdapter());
        return state;
    }

    /**
     * Binds the data from the form
     *
     * @param form - the form identifier
     */
    private void bindData(String form) {
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

            @Override
            public void onSaved(String json) {
                Log.d("Forms Saved", json);
            }
        });

        p.parseJson(getActivity(), PXFParser.parseFileToString(getActivity(), json), button_handler);
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

    private View.OnClickListener button_handler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String action = getAction(v);
            if (action == null)
                return;

            if (action.equalsIgnoreCase(PXFButton.ACTION_OPEN_CAMERA)) {
                IntentIntegrator.forFragment(FormsFragment.this).initiateScan();
                mButtonBarCode = (Button)v;
            } else if (action.equalsIgnoreCase(PXFButton.ACTION_SUBMIT)) {

            } else if (action.equalsIgnoreCase(PXFButton.ACTION_BACK_ROOT)) {

            } else {
                // Unregistered action
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private String getAction(View v) {
        return PXFButton.getAction(v);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // For Barcode reader
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null || !TextUtils.isEmpty(result.getContents())) {
            //TODO: How to know clicked view on activity result
            Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_SHORT).show();
            if (mButtonBarCode != null) {
                PXFButton pxfButton = ((PXFButton.HelperButton) mButtonBarCode.getTag()).getPXFButton();
                if (pxfButton.getEventHandler().setWidgetValue(pxfButton, PXFButton.FIELD_TITLE,
                        result.getContents()))
                    pxfButton.getEventHandler().notifyDataSetChanges();
            }
        } else
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
    }
}
