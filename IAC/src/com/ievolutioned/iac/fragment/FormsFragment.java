package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.PXFButton;
import com.ievolutioned.pxform.PXFParser;
import com.ievolutioned.pxform.PXWidget;
import com.ievolutioned.pxform.adapters.PXFAdapter;

/**
 *
 */
public class FormsFragment extends Fragment {

    public static final String DATABASE_FORM_ID = "DATABASE_FORM_ID";
    public static final String DATABASE_LEVEL = "DATABASE_LEVEL";
    public static final String DATABASE_KEY_PARENT = "DATABASE_KEY_PARENT";
    public static final String DATABASE_JSON = "DATABASE_JSON";

    //public static final String ARG_FORM_ID = "ARG_FORM_ID";

    /**
     * PXFParser parser
     */
    private ListView listView;
    private PXFParser p;
    private Bundle savedState;
    //private PXFAdapter currentAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_forms, container, false);
        bindUI(root);
        return root;
    }

    /**
     * Binds the User interface
     *
     */
    private void bindUI(View root) {
        listView = (ListView) root.findViewById(R.id.PXForm_linearPanel);
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        // restore sate
        if (!restoreStateFromArgs()) {
            // first run
        }

        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        p = new PXFParser(new PXFParser.PXFParserEventHandler() {
            @Override
            public void finish(PXFAdapter adapter, String json) {
                //currentAdapter = adapter;
                adapter.setAdapterEventHandler(adapterEventHandler);
                listView.setAdapter(adapter);
                loading.dismiss();
            }

            @Override
            public void error(Exception ex, String json) {
                Toast.makeText(getActivity(), "can't parse json", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });

        JsonElement jsonElement = new JsonParser().parse(savedState.getString(DATABASE_JSON));
        String jsonArray = jsonElement.getAsJsonObject().get("content").getAsJsonArray().toString();

        p.parseJson(getActivity()
                , jsonArray
                , savedState.getLong(DATABASE_FORM_ID)
                , savedState.getInt(DATABASE_LEVEL)
                , savedState.getString(DATABASE_KEY_PARENT)
        );
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

        if(savedState != null)
            return savedState;

        Bundle state = new Bundle();
        // save the current state

        return state;
    }

    PXFButton buttonBarCode;

    private PXFAdapter.AdapterEventHandler adapterEventHandler = new PXFAdapter.AdapterEventHandler(){
        @Override
        public void onClick(PXWidget widget) {
            if(widget.getJsonEntries().containsKey(PXFButton.FIELD_ACTION)
                    && widget.getAdapterItemType() == PXWidget.ADAPTER_ITEM_TYPE_BUTTON){
                buttonBarCode = (PXFButton)widget;

                if(PXFButton.ACTION_OPEN_CAMERA.equalsIgnoreCase(buttonBarCode.getJsonEntries()
                        .get(PXFButton.FIELD_ACTION).getValue().getAsString())){
                    IntentIntegrator.forFragment(FormsFragment.this).initiateScan();
                }
            }
        }

        @Override
        public void openSubForm(final String parentKey, final String json, PXFAdapter adapter) {
            final Bundle my_args = savedState;

            final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
            loading.show();

            //currentAdapter = adapter;

            adapter.save(
                    savedState.getLong(DATABASE_FORM_ID)
                    , savedState.getInt(DATABASE_LEVEL)
                    , savedState.getString(DATABASE_KEY_PARENT)
                    , new PXFAdapter.AdapterSaveHandler() {
                        @Override
                        public void saved() {
                            loading.dismiss();
                            getActivity().runOnUiThread(new Runnable() { @Override public void run() {
                                Bundle a = new Bundle();
                                a.putLong(FormsFragment.DATABASE_FORM_ID, my_args.getLong(FormsFragment.DATABASE_FORM_ID));
                                a.putInt(FormsFragment.DATABASE_LEVEL, my_args.getInt(FormsFragment.DATABASE_LEVEL) + 1);
                                a.putString(FormsFragment.DATABASE_KEY_PARENT, parentKey);
                                a.putString(FormsFragment.DATABASE_JSON, json);

                                Bundle args = new Bundle();
                                args.putBundle(FormsFragment.class.getName(), a);

                                FormsFragment fragment = new FormsFragment();
                                fragment.setArguments(args);

                                FragmentManager fragmentManager = getFragmentManager();

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.activity_main_frame_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }});
                        }

                        @Override
                        public void error(Exception ex) {
                            Toast.makeText(getActivity(), "error al salvar", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null || !TextUtils.isEmpty(result.getContents())) {
            Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_SHORT).show();
            if (buttonBarCode != null) {
                try {
                    buttonBarCode.setValue(result.getContents());
                    buttonBarCode.getEventHandler().notifyDataSetChanges();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        } else
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
    }
}
