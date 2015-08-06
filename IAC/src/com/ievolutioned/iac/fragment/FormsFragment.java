package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.service.UserService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.view.ViewUtility;
import com.ievolutioned.pxform.PXFButton;
import com.ievolutioned.pxform.PXFParser;
import com.ievolutioned.pxform.PXWidget;
import com.ievolutioned.pxform.adapters.PXFAdapter;

/**
 *
 */
public class FormsFragment extends BaseFragmentClass {

    public static final String TAG = FormsFragment.class.getName();

    public static final String DATABASE_FORM_ID = "DATABASE_FORM_ID";
    public static final String DATABASE_LEVEL = "DATABASE_LEVEL";
    public static final String DATABASE_KEY_PARENT = "DATABASE_KEY_PARENT";
    public static final String DATABASE_JSON = "DATABASE_JSON";

    private ListView listView;
    private Bundle savedState;
    private PXFButton buttonBarCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = bindUI(inflater.inflate(R.layout.fragment_forms, container, false));
        setHasOptionsMenu(true);
        return root;
    }

    /**
     * Binds the User interface
     */
    private View bindUI(View root) {
        listView = (ListView) root.findViewById(R.id.PXForm_linearPanel);

        setToolbarNavigationOnClickListener(mainActivityHomeButton);
        setToolbarNavigationDisplayHomeAsUpEnabled();
        return root;
    }

    private final View.OnClickListener mainActivityHomeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_forms_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Runnable saveR;
        switch (item.getItemId()) {
            case R.id.menu_fragment_form_save:
                saveR = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "salvado", Toast.LENGTH_SHORT).show();
                    }
                };
                save(saveR);
                break;
            case R.id.menu_fragment_form_upload:
                saveAndUpload();
                Toast.makeText(getActivity(), "upload", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "????", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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

        PXFParser p = new PXFParser(new PXFParser.PXFParserEventHandler() {
            @Override
            public void finish(PXFAdapter adapter, String json) {
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

        p.parseJson(getActivity()
                , savedState.getString(DATABASE_JSON)
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

        if (savedState != null)
            return savedState;

        Bundle state = new Bundle();
        // save the current state

        return state;
    }

    private final PXFAdapter.AdapterEventHandler adapterEventHandler = new PXFAdapter.AdapterEventHandler() {
        @Override
        public void onClick(PXWidget widget) {
            if (widget.getAdapterItemType() == PXWidget.ADAPTER_ITEM_TYPE_BUTTON &&
                    widget.getJsonEntries().get(PXWidget.FIELD_KEY).getValue().getAsString()
                            .contains(PXWidget.FIELD_KEY_BARCODE)) {
                buttonBarCode = (PXFButton) widget;
                IntentIntegrator.forFragment(FormsFragment.this).initiateScan();

            }
        }

        @Override
        public void openSubForm(final String parentKey, final String json, PXFAdapter adapter) {
            //final Bundle my_args = savedState;
            //final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
            //loading.show();

            //adapter.save(
            //        savedState.getLong(DATABASE_FORM_ID)
            //        , savedState.getInt(DATABASE_LEVEL)
            //        , savedState.getString(DATABASE_KEY_PARENT)
            //        , new PXFAdapter.AdapterSaveHandler() {
            //            @Override
            //            public void saved() {
            //                loading.dismiss();
            //                getActivity().runOnUiThread();
            //            }
            //
            //            @Override
            //            public void error(Exception ex) {
            //                loading.dismiss();
            //                Toast.makeText(getActivity(), "Por el momento no se ha podido salvar", Toast.LENGTH_SHORT).show();
            //            }
            //        }
            //);

            Runnable saveRunnable = new Runnable() {
                @Override
                public void run() {
                    Bundle a = new Bundle();
                    a.putLong(FormsFragment.DATABASE_FORM_ID,
                            savedState.getLong(FormsFragment.DATABASE_FORM_ID));
                    a.putInt(FormsFragment.DATABASE_LEVEL,
                            savedState.getInt(FormsFragment.DATABASE_LEVEL) + 1);
                    a.putString(FormsFragment.DATABASE_KEY_PARENT, parentKey);
                    a.putString(FormsFragment.DATABASE_JSON, json);

                    Bundle args = new Bundle();
                    args.putBundle(FormsFragment.class.getName(), a);

                    FormsFragment fragment = new FormsFragment();
                    fragment.setArguments(args);

                    setMainActivityReplaceFragment(fragment);
                }
            };

            save(saveRunnable);
        }
    };

    /**
     * Save current state of the Form data
     *
     * @param pos_execute Runnable to be executed after save
     */
    private final void save(final Runnable pos_execute) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        if (listView.getAdapter() == null || !(listView.getAdapter() instanceof PXFAdapter)) {
            return;
        }

        PXFAdapter adapter = (PXFAdapter) listView.getAdapter();

        adapter.save(
                savedState.getLong(DATABASE_FORM_ID)
                , savedState.getInt(DATABASE_LEVEL)
                , savedState.getString(DATABASE_KEY_PARENT)
                , new PXFAdapter.AdapterSaveHandler() {
                    @Override
                    public void saved() {
                        loading.dismiss();

                        if (pos_execute != null)
                            getActivity().runOnUiThread(pos_execute);
                    }

                    @Override
                    public void error(Exception ex) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Por el momento no se ha podido salvar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private final void saveAndUpload() {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        if (listView.getAdapter() == null || !(listView.getAdapter() instanceof PXFAdapter)) {
            return;
        }

        final PXFAdapter adapter = (PXFAdapter) listView.getAdapter();

        adapter.save(
                savedState.getLong(DATABASE_FORM_ID)
                , savedState.getInt(DATABASE_LEVEL)
                , savedState.getString(DATABASE_KEY_PARENT)
                , new PXFAdapter.AdapterSaveHandler() {
                    @Override
                    public void saved() {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Ready to upload", Toast.LENGTH_SHORT).show();
                        getSavedResponse();
                    }

                    @Override
                    public void error(Exception ex) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Por el momento no se ha podido salvar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void getSavedResponse() {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        if (listView.getAdapter() == null || !(listView.getAdapter() instanceof PXFAdapter)) {
            return;
        }

        final PXFAdapter adapter = (PXFAdapter) listView.getAdapter();

        adapter.getJsonForm(
                savedState.getLong(DATABASE_FORM_ID)
                , savedState.getInt(DATABASE_LEVEL)
                , savedState.getString(DATABASE_KEY_PARENT)
                , new PXFAdapter.AdapterJSONHandler() {
                    @Override
                    public void success(JsonElement jsonElement) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Formulario cargado", Toast.LENGTH_SHORT).show();
                        createFormService(jsonElement);
                    }

                    @Override
                    public void error(Exception ex) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error al cargar formulario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createFormService(JsonElement jsonElement) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        //loading.show();
        //Ready to upload


        String uuid = AppConfig.getUUID(getActivity());
        String at = AppPreferences.getAdminToken(getActivity());
        JsonObject json = new JsonObject();
        json.addProperty("inquest_id", getFormId());
        json.addProperty("iac_id", getIacId());
        //Intentar arreglar el json response
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().remove("barcodeReader");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("newCompany", "");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("newJob", "");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("newSalary", "");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("otherReason", "");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("reasonToLeave", "Matrimonio ");
        jsonElement.getAsJsonObject().get("response").getAsJsonObject().addProperty("unionizedWhy", "¿¿ funcionas,no tengo idea tu si ?????");
        json.add("user_response", jsonElement);

        UserService userService = new UserService(uuid, at);

        userService.create(json.getAsJsonObject().toString(), new UserService.ServiceHandler() {
            @Override
            public void onSuccess(UserService.UserResponse response) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Formulario enviado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UserService.UserResponse response) {
                loading.dismiss();
                Toast.makeText(getActivity(), "Error: " + response.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                loading.dismiss();
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFormId() {
        return "9";
    }

    private String getIacId() {
        return "32000011";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null || !TextUtils.isEmpty(result.getContents())) {
            Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_SHORT).show();
            if (buttonBarCode != null) {
                try {
                    buttonBarCode.setValue(result.getContents());
                    buttonBarCode.getEventHandler().notifyDataSetChanges();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Por el momento no se puede terminar la operacion", Toast.LENGTH_LONG).show();
        }
    }
}
