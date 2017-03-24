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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    private ListView mAttendeeListView;
    private AttendeeAdapter mAttendeeAdapter;
    private JsonArray mAttendees = new JsonArray();

    private View mHostDetailsView;

    private Bundle mSavedInstanceState = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_dining_guests, container, false);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_forms_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_form_upload:
                if (validateForm())
                    saveAndUpload();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO: Save state
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

        mAttendeeListView = (ListView) root.findViewById(R.id.fragment_dining_guests_list);
        if (mAttendeeListView != null) {
            mAttendeeAdapter = new AttendeeAdapter(getActivity());
            mAttendeeListView.setAdapter(mAttendeeAdapter);
        }

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
        /*if (mSavedInstanceState != null && mSavedInstanceState.containsKey(ARGS_SAVED_COURSES))
            restoreState(mSavedInstanceState);
        else {*/
        Context c = getActivity();
        //}
    }

    /**
     * Restores the state of courses loaded
     *
     * @param args - previous state
     */
    private void restoreState(Bundle args) {
        //TODO: restore state
        /*
        try {
            JsonElement json = new JsonParser().parse(args.getString(ARGS_SAVED_COURSES));
            mPlants = new JsonArray();
            mPlants.addAll(json.getAsJsonArray());
            if (mPlantsSpinner != null) {
                mPlantsSpinner.setOnItemSelectedListener(null);
                if (mPlantsSpinnerAdapter != null) {
                    mPlantsSpinnerAdapter.notifyDataSetChanged();
                    mPlantsSpinner.setSelection(args.getInt(ARGS_SAVED_COURSE_ITEM_POS));
                }
            }
            JsonElement jsonAttendees = new JsonParser().parse(args.getString(ARGS_SAVED_ATTENDEES));
            mAttendees = jsonAttendees.getAsJsonArray();
            if (mAttendeeAdapter != null)
                mAttendeeAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
        */
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
     * @param attendee - the attendee
     */
    private void removeAttendee(final JsonObject attendee) {
        //TODO: Remove it
        try {
            mAttendees.remove(attendee);
            mAttendeeAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, "Error");
        }
    }

    /**
     * Add new attendee by iac id
     *
     * @param iacId
     */
    private void addNewAttendee(final String iacId) {
        //Verify if it exists
        if (iacId == null || isAttendeeInList(iacId))
            return;
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);

        //TODO: Add new attendee
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
                    break;
                case R.id.fragment_dining_guests_host_barcode_button:
                    break;
                //Guests
                case R.id.fragment_dining_guests_manual_button:
                case R.id.fragment_dining_guests_iac_id_button:
                    showIacIdDialog();
                    break;
                case R.id.fragment_dining_guests_barcode_button:
                    showBarcodeReader();
                    break;
                default:
                    break;
            }
        }
    };

    private void showGuests() {
        //TODO: Save instance
        //TODO: Create the guests fragment and replace it
    }

    /**
     * Shows iac id prompt dialog
     */
    private void showIacIdDialog() {
        try {
            final EditText editTextIacId = new EditText(getActivity());
            editTextIacId.setHint(R.string.string_fragment_dining_guests_new_input_hint);
            editTextIacId.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editTextIacId.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            editTextIacId.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.string_fragment_dining_guests_new_title);
            dialog.setView(editTextIacId);

            //Add
            dialog.setPositiveButton(R.string.string_fragment_dining_guests_new_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addNewAttendee(editTextIacId.getText().toString());
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

    /**
     * Shows barcode reader for id card
     */
    private void showBarcodeReader() {
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
        if (mAttendees == null)
            return null;
        ArrayList<Integer> attendees = new ArrayList<>(mAttendees.size());
        for (int i = 0; i < mAttendees.size(); i++)
            try {
                attendees.add(Integer.parseInt(mAttendees.get(i).getAsJsonObject()
                        .get(AttendeeAdapter.ATTENDEE_ID).getAsString()));
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
            addNewAttendee(result.getContents());
            showBarCodeDecision();
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
                    showBarcodeReader();
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
    class AttendeeAdapter extends BaseAdapter implements View.OnClickListener {

        protected final static String ATTENDEE_ID = "id";
        protected final static String ATTENDEE_NAME = "name";
        protected final static String ATTENDEE_IAC_ID = "iac_id";
        private LayoutInflater mInflater;

        /**
         * {@link AttendeeAdapter} initializer
         *
         * @param c
         */
        public AttendeeAdapter(Context c) {
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mAttendees == null ? 0 : mAttendees.size();
        }

        @Override
        public Object getItem(int i) {
            try {
                return mAttendees == null ? null : mAttendees.get(i).getAsJsonObject();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            try {
                return mAttendees == null ? 0L :
                        Long.parseLong(mAttendees.get(i).getAsJsonObject().get(ATTENDEE_ID).getAsString());
            } catch (Exception e) {
                return 0L;
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_attendee, viewGroup, false);
                view.findViewById(R.id.list_item_attendee_delete_button).setOnClickListener(this);
            }

            JsonObject attendee = (JsonObject) getItem(i);
            TextView textViewName = (TextView) view.findViewById(R.id.list_item_attendee_name);
            TextView textViewId = (TextView) view.findViewById(R.id.list_item_attendee_id);
            textViewName.setText(attendee.get(ATTENDEE_NAME).getAsString());
            textViewId.setText(attendee.get(ATTENDEE_IAC_ID).getAsString());
            view.findViewById(R.id.list_item_attendee_delete_button).setTag(getItem(i));
            view.setTag(getItem(i));
            return view;
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
                        attendee.get(ATTENDEE_NAME).getAsString());
                alertDialog.setMessage(body);
                //Yes delete
                alertDialog.setPositiveButton(R.string.string_fragment_dining_guests_delete_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeAttendee(attendee);
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
