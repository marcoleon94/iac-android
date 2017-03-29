package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Guests for dining fragment class. Allows to add and modify attendees for any plant dining room
 * <p>
 * Created by Daniel on 23/03/2017.
 */

public class DiningGuestsFragment extends BaseFragmentClass {

    private static final String TAG = DiningGuestsFragment.class.getName();
    public static final String ARGS_HOST = "ARGS_HOST";
    public static final String ARGS_GUESTS_SAVED = "ARGS_GUESTS_SAVED";

    private static final int EXTRA_HOST = 1;
    private static final int EXTRA_GUEST = 2;

    private ListView mGuestsListView;
    private GuestsAdapter mGuestsAdapter;
    private JsonArray mGuests = new JsonArray();

    private View mHostDetailsView;
    private TextView mHostIacId;
    private TextView mHostName;
    private JsonObject mCurrentHost;

    private Bundle mSavedInstanceState = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getActivity() != null && getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enable static back arrow
        View root = inflater.inflate(R.layout.fragment_dining_guests, container, false);
        bindUI(root);
        setTitle(getString(R.string.string_fragment_dining_guests_title));
        if (savedInstanceState != null)
            mSavedInstanceState = new Bundle(savedInstanceState);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindData(getArguments());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO: Save state
        if (mCurrentHost != null)
            outState.putString(ARGS_HOST, mCurrentHost.getAsJsonObject().toString());
        if (mGuests != null) {
            ArrayList<String> guests = new ArrayList<>(mGuests.size());
            for (int i = 0; i < mGuests.size(); i++)
                guests.add(mGuests.get(i).toString());
            outState.putStringArrayList(ARGS_GUESTS_SAVED, guests);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Binds the UI elements
     *
     * @param root - {@link View} inflated view
     */
    private void bindUI(View root) {
        if (root == null)
            return;

        mHostDetailsView = root.findViewById(R.id.fragment_dining_guests_host_details);
        mHostIacId = (TextView) root.findViewById(R.id.fragment_dining_guests_host_iac_id);
        mHostName = (TextView) root.findViewById(R.id.fragment_dining_guests_host_name);

        mGuestsListView = (ListView) root.findViewById(R.id.fragment_dining_guests_list);
        if (mGuestsListView != null) {
            mGuestsAdapter = new GuestsAdapter(getActivity());
            mGuestsListView.setAdapter(mGuestsAdapter);
        }

        root.findViewById(R.id.fragment_dining_guests_host_iac_id_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_host_barcode_button).setOnClickListener(button_click);

        root.findViewById(R.id.fragment_dining_guests_manual_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_barcode_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_iac_id_button).setOnClickListener(button_click);
    }

    /**
     * Binds any data if necessary
     *
     * @param args - {@link Bundle} arguments
     */
    private void bindData(Bundle args) {
        //TODO: restore state
        restoreState(args != null && args.containsKey(ARGS_HOST) ? args : mSavedInstanceState);
    }

    /**
     * Restores the state of courses loaded
     *
     * @param args - previous state
     */
    private void restoreState(Bundle args) {
        //TODO: restore state

        try {
            if (args != null) {
                //Get the host
                if (args.containsKey(ARGS_HOST)) {
                    JsonElement json = new JsonParser().parse(args.getString(ARGS_HOST));
                    mCurrentHost = json.getAsJsonObject();
                    if (mCurrentHost != null && !mCurrentHost.isJsonNull())
                        showHost();
                }

                //Get the guests
                if (args.containsKey(ARGS_GUESTS_SAVED)) {
                    ArrayList<String> jsonGuests = args.getStringArrayList(ARGS_GUESTS_SAVED);
                    mGuests = new JsonArray();
                    if (jsonGuests != null)
                        for (String g : jsonGuests) {
                            mGuests.add(new JsonParser().parse(g));
                        }
                    if (mGuestsAdapter != null)
                        mGuestsAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }

    }

    /**
     * Set the title
     *
     * @param title
     */
    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            activity.setTitle(title);
    }

    /**
     * Remove attendee from list of attendees
     *
     * @param guest - the attendee
     */
    private void removeGuest(final JsonObject guest) {
        //TODO: Remove it
        try {
            mGuests.remove(guest);
            mGuestsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, "Error");
        }
    }

    /**
     * Add new attendee by iac id
     *
     * @param iacId
     */
    private void addHost(final String iacId) {
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);

        //TODO: Add new host


        mCurrentHost = new JsonObject();
        mCurrentHost.addProperty("id", 1);
        mCurrentHost.addProperty("iac_id", iacId);
        mCurrentHost.addProperty("name", "Demo host");
        showHost();
    }

    private void showHost() {
        if (mCurrentHost != null && !mCurrentHost.isJsonNull() &&
                mHostDetailsView != null && mHostIacId != null && mHostName != null) {
            mHostDetailsView.setVisibility(View.VISIBLE);
            mHostIacId.setText(mCurrentHost.get("iac_id").toString());
            mHostName.setText(mCurrentHost.get("name").toString());
        } else if (mHostDetailsView != null)
            mHostDetailsView.setVisibility(View.GONE);
    }

    private void addGuest(final String iacId) {
        //Verify if it exists
        if (iacId == null || isAttendeeInList(iacId))
            return;
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);

        //TODO: Add new guest attendee
        JsonObject guest = new JsonObject();
        guest.addProperty(GuestsAdapter.GUEST_ID, mGuests.size() + 1);
        guest.addProperty(GuestsAdapter.GUEST_IAC_ID, iacId);
        guest.addProperty(GuestsAdapter.GUEST_NAME, "Demo Invitado");
        if (mCurrentHost.has("id"))
            guest.addProperty(GuestsAdapter.HOST_ID, mCurrentHost.get("id").getAsString());
        if (mCurrentHost.has("iac_id"))
            guest.addProperty(GuestsAdapter.HOST_IAC_ID, mCurrentHost.get("iac_id").getAsString());
        if (mCurrentHost.has("name"))
            guest.addProperty(GuestsAdapter.HOST_NAME, mCurrentHost.get("name").getAsString());

        mGuests.add(guest);
        mGuestsAdapter.notifyDataSetChanged();
    }

    private void addManualGuest(final String input) {
        if (input == null)
            return;
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);

        //TODO: Add new guest manual
        JsonObject guest = new JsonObject();
        guest.addProperty(GuestsAdapter.GUEST_ID, 0);// How to set?
        guest.addProperty(GuestsAdapter.GUEST_IAC_ID, "N/A");
        guest.addProperty(GuestsAdapter.GUEST_NAME, input);
        if (mCurrentHost.has("id"))
            guest.addProperty(GuestsAdapter.HOST_ID, mCurrentHost.get("id").getAsString());
        if (mCurrentHost.has("iac_id"))
            guest.addProperty(GuestsAdapter.HOST_IAC_ID, mCurrentHost.get("iac_id").getAsString());
        if (mCurrentHost.has("name"))
            guest.addProperty(GuestsAdapter.HOST_NAME, mCurrentHost.get("name").getAsString());

        mGuests.add(guest);
        mGuestsAdapter.notifyDataSetChanged();
    }


    /**
     * Returns if an attendee is in a list
     *
     * @param iacId - String id
     * @return true if the attendee id is in the list, false otherwise
     */
    private boolean isAttendeeInList(String iacId) {
        ArrayList<Integer> attendeesIds = getAttendeeList();
        if (attendeesIds != null && attendeesIds.size() > 0)
            for (Integer i : attendeesIds)
                if (iacId.contentEquals(i.toString()))
                    return true;
        return false;
    }

    /**
     * For Barcode and iac manual inputs
     */
    private View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //Host
                case R.id.fragment_dining_guests_host_iac_id_button:
                    showIacIdDialog(EXTRA_HOST);
                    break;
                case R.id.fragment_dining_guests_host_barcode_button:
                    showBarcodeReader(EXTRA_HOST);
                    break;
                //Guests
                case R.id.fragment_dining_guests_manual_button:
                    showManualInput();
                    break;
                case R.id.fragment_dining_guests_iac_id_button:
                    showIacIdDialog(EXTRA_GUEST);
                    break;
                case R.id.fragment_dining_guests_barcode_button:
                    showBarcodeReader(EXTRA_GUEST);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Shows iac id prompt dialog
     */
    private void showIacIdDialog(final int extra) {
        try {
            final EditText editTextIacId = new EditText(getActivity());
            editTextIacId.setHint(R.string.string_fragment_dining_guests_new_input_hint);
            editTextIacId.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editTextIacId.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            editTextIacId.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(extra == 1 ? R.string.string_fragment_dining_guests_host_new_title :
                    R.string.string_fragment_dining_guests_new_title);
            dialog.setView(editTextIacId);

            //Add
            dialog.setPositiveButton(R.string.string_fragment_dining_guests_new_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (extra == 1)
                                addHost(editTextIacId.getText().toString());
                            else
                                addGuest(editTextIacId.getText().toString());
                            dialogInterface.dismiss();
                        }
                    });
            //Cancel
            dialog.setNegativeButton(R.string.string_fragment_dining_guests_new_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            dialog.create().show();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    private void showManualInput() {
        try {
            final EditText editText = new EditText(getActivity());
            editText.setHint(R.string.string_fragment_dining_guests_new_manual_hint);
            editText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.string_fragment_dining_guests_new_manual);
            dialog.setView(editText);

            //Add
            dialog.setPositiveButton(R.string.string_fragment_dining_guests_new_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addManualGuest(editText.getText().toString());
                        }
                    });
            //Cancel
            dialog.setNegativeButton(R.string.string_fragment_dining_guests_new_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            dialog.create().show();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Shows barcode reader for id card
     */
    private void showBarcodeReader(int extra) {
        AppPreferences.setDiningBarcodeTemporal(getActivity(), extra);
        IntentIntegrator.forSupportFragment(DiningGuestsFragment.this).initiateScan();
    }

    /**
     * Validates the form before to being sent
     *
     * @return true if is valid
     */
    private boolean validateForm() {
        //TODO: validate
        return true;
    }

    /**
     * Saves the attendees for the group
     */
    private void saveAndUpload() {
        //TODO: save
    }

    /**
     * Gets the attendee list to be submitted
     *
     * @return an integer array list
     */
    private ArrayList<Integer> getAttendeeList() {
        if (mGuests == null)
            return null;
        ArrayList<Integer> attendees = new ArrayList<>(mGuests.size());
        for (int i = 0; i < mGuests.size(); i++)
            try {
                attendees.add(Integer.parseInt(mGuests.get(i).getAsJsonObject()
                        .get(GuestsAdapter.GUEST_ID).getAsString()));
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        return attendees;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && !TextUtils.isEmpty(result.getContents())) {
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_SUCCESS, result.getContents());
            int from = AppPreferences.getDiningBarcodeTemporal(getContext());
            if (from == EXTRA_HOST) {
                //For Host
                addHost(result.getContents());
            } else if (from == EXTRA_GUEST) {
                //For Guests
                addGuest(result.getContents().toString());
                showBarCodeDecision();
            } else
                ViewUtility.showMessage(getContext(), ViewUtility.MSG_ERROR,
                        R.string.string_fragment_dining_guests_general_error);
        }
    }

    /**
     * Added a show bar code show question
     */
    private void showBarCodeDecision() {
        if (getActivity() != null) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.string_fragment_dining_guests_barcode_add_title);
            dialog.setMessage(R.string.string_fragment_dining_guests_barcode_add_body);
            dialog.setPositiveButton(R.string.string_fragment_dining_guests_barcode_add_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showBarcodeReader(EXTRA_GUEST);
                    dialogInterface.dismiss();
                }
            });
            dialog.setNegativeButton(R.string.string_fragment_dining_guests_barcode_add_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.create().show();
        }
    }


    /**
     * Shows or hides the attendee layouts
     *
     * @param itemId if any course is selected, that means <code>itemId > 0</code>
     */
    private void showAttendeeElements(long itemId) {
        int visibility = itemId > 0 ? View.VISIBLE : View.GONE;
        try {
            getView().findViewById(R.id.fragment_dining_guests_title).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_guests_add_layout).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_guests_attendee_subtitle).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_guests_list).setVisibility(visibility);
        } catch (NullPointerException npe) {
            LogUtil.e(TAG, npe.getMessage(), npe);
        }
    }


    /**
     * Attendees list adapter. A list of attendees in the {@link ListView} element
     */
    class GuestsAdapter extends BaseAdapter implements View.OnClickListener {

        protected final static String GUEST_ID = "id";
        protected final static String GUEST_NAME = "name";
        protected final static String GUEST_IAC_ID = "iac_id";

        protected final static String HOST_ID = "host_id";
        protected final static String HOST_NAME = "host_name";
        protected final static String HOST_IAC_ID = "host_iac_id";


        private LayoutInflater mInflater;

        /**
         * {@link GuestsAdapter} initializer
         *
         * @param c
         */
        public GuestsAdapter(Context c) {
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mGuests == null ? 0 : mGuests.size();
        }

        @Override
        public Object getItem(int i) {
            try {
                return mGuests == null ? null : mGuests.get(i).getAsJsonObject();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            try {
                return mGuests == null ? 0L :
                        Long.parseLong(mGuests.get(i).getAsJsonObject().get(GUEST_ID).getAsString());
            } catch (Exception e) {
                return 0L;
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_guest, viewGroup, false);
                view.findViewById(R.id.list_item_guest_delete_button).setOnClickListener(this);
            }

            JsonObject guest = (JsonObject) getItem(i);
            TextView textViewGuest = (TextView) view.findViewById(R.id.list_item_guest);
            TextView textViewHost = (TextView) view.findViewById(R.id.list_item_guest_host);
            textViewGuest.setText(getDetails(guest, EXTRA_GUEST));
            textViewHost.setText(getDetails(guest, EXTRA_HOST));
            view.findViewById(R.id.list_item_guest_delete_button).setTag(getItem(i));
            view.setTag(getItem(i));
            return view;
        }

        private String getDetails(JsonObject guest, final int extra) {
            if (guest == null || guest.isJsonNull())
                return "";
            StringBuilder sb = new StringBuilder("");
            if (extra == EXTRA_HOST) {
                if (guest.has(GUEST_IAC_ID)) {
                    sb.append(guest.get(GUEST_IAC_ID).getAsString());
                    sb.append(" - ");
                }
                if (guest.has(GUEST_NAME))
                    sb.append(guest.get(GUEST_NAME).getAsString());
            } else {
                if (guest.has(HOST_IAC_ID)) {
                    sb.append(guest.get(HOST_IAC_ID).getAsString());
                    sb.append(" - ");
                }
                if (guest.has(HOST_NAME))
                    sb.append(guest.get(HOST_NAME).getAsString());
            }
            return sb.toString();
        }

        @Override
        public void onClick(View view) {
            try {
                //What happen when the tag is null?
                //The tag is the id of the attendee
                final JsonObject attendee = (JsonObject) view.getTag();
                if (attendee == null)
                    return;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle(R.string.string_fragment_dining_guests_delete_title);
                String body = String.format(Locale.getDefault(),
                        getString(R.string.string_fragment_dining_guests_delete_body),
                        attendee.get(GUEST_NAME).getAsString());
                alertDialog.setMessage(body);
                //Yes delete
                alertDialog.setPositiveButton(R.string.string_fragment_dining_guests_delete_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeGuest(attendee);
                                dialogInterface.dismiss();
                            }
                        });
                //No delete
                alertDialog.setNegativeButton(R.string.string_fragment_dining_guests_delete_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                alertDialog.create().show();
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

}
