package com.ievolutioned.iac.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Form fragment class, provides a simple control to insert a form into a
 * local database and upload data to web services
 */
public class FormsFragment extends BaseFragmentClass {
    public static final String TAG_SUBFORM = "SUBFORM";
    public static final String ARGS_FORM_ID = "ARGS_FORM_ID";
    public static final String ARG_FORM_NAME = "ARG_FORM_NAME";
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
        setToolbarNavigationDisplayHomeAsUpEnabled(getTag() != null &&
                getTag().contentEquals(TAG_SUBFORM));
        setTitle(getArguments());
        return root;
    }

    private void setTitle(Bundle args) {
        Bundle b = args.getBundle(FormsFragment.class.getName());
        if (b != null && b.containsKey(ARG_FORM_NAME))
            getActivity().setTitle(b.getString(ARG_FORM_NAME));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_forms_menu, menu);
        if (savedState.containsKey(DATABASE_KEY_PARENT) &&
                !savedState.getString(DATABASE_KEY_PARENT, "").isEmpty()) {
            menu.clear();
        }
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
                if (validateForm()) {
                    saveAndUpload();
                }
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
                IntentIntegrator.forSupportFragment(FormsFragment.this).initiateScan();

            }
        }

        @Override
        public void openSubForm(final String parentKey, final String json, PXFAdapter adapter) {
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

                    Fragment fragment = new FormsFragment();
                    fragment.setArguments(args);
                    setMainActivityReplaceFragment(fragment, TAG_SUBFORM);
                }
            };

            save(saveRunnable);
        }
    };

    /**
     * Validates the form
     *
     * @return true if its valid, false otherwise
     */
    private boolean validateForm() {
        PXFAdapter adapter = (PXFAdapter) listView.getAdapter();
        String msg = null;

        //Verify ID FORM
        if (getFormId() == null)
            msg = "Error de formulario";
        if (msg != null) {
            showValidationMessage(msg);
            return false;
        }

        //Verify IAC ID
        String iacID = getIacId();
        if (iacID == null || iacID.isEmpty())
            msg = "El IAC ID no es válido";
        if (msg != null) {
            showValidationMessage(msg);
            return false;
        }

        //Verify the form
        msg = adapter.validate(listView);
        if (msg != null) {
            showValidationMessage("Campo requerido: " + msg);
            return false;
        }

        return true;
    }

    /**
     * Displays a validation message
     *
     * @param msg - the message
     */
    private void showValidationMessage(String msg) {
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        View v = toast.getView();
        v.setBackgroundColor(Color.RED);
        toast.show();
    }

    /**
     * Save current state of the Form data
     *
     * @param pos_execute Runnable to be executed after save
     */
    public final void save(final Runnable pos_execute) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity(), "Guardando...");
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

    /**
     * Saves the form before upload
     */
    private final void saveAndUpload() {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity(), "Guardando...");
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
                        Toast.makeText(getActivity(), "Guardado", Toast.LENGTH_SHORT).show();
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

    /**
     * Calls an gets the response of submit form
     */
    private void getSavedResponse() {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();

        if (listView.getAdapter() == null || !(listView.getAdapter() instanceof PXFAdapter)) {
            return;
        }

        final PXFAdapter adapter = (PXFAdapter) listView.getAdapter();

        adapter.getJsonForm(
                savedState.getLong(DATABASE_FORM_ID)
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

    /**
     * Creates a form form the service
     *
     * @param jsonElement - the form
     */
    private void createFormService(JsonElement jsonElement) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity(), "Subiendo respuestas...");
        loading.show();
        //Ready to upload
        String uuid = AppConfig.getUUID(getActivity());
        String at = AppPreferences.getAdminToken(getActivity());
        JsonObject json = new JsonObject();
        json.addProperty("inquest_id", getFormId());
        json.addProperty("iac_id", getIacId());
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
                Toast.makeText(getActivity(), "Error al enviar fomulario!" + response.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                loading.dismiss();
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets a form id
     *
     * @return - the id or null
     */
    public String getFormId() {
        long idForm = -1;
        if (savedState == null)
            return null;
        if (savedState.containsKey(ARGS_FORM_ID))
            idForm = savedState.getLong(ARGS_FORM_ID, -1);
        return idForm > -1 ? String.valueOf(idForm) : null;
    }

    /**
     * Gets the IAC id
     *
     * @return the IAC id
     */
    private String getIacId() {
        //TODO: This key is only temporal
        PXFAdapter adapter = (PXFAdapter) listView.getAdapter();
        String iacId = adapter.getItemValueForKey("employeeID");
        if (iacId == null)
            iacId = AppPreferences.getIacId(getActivity());
        return iacId;
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
