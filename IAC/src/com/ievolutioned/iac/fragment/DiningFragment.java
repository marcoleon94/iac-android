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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.service.CoursesService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Attendees for courses fragment class. Allows to add and modify attendees for any course
 * <p>
 * Created by Daniel on 21/03/2017.
 */

public class DiningFragment extends BaseFragmentClass {

    private static final String TAG = DiningFragment.class.getName();

    private Spinner mPlantsSpinner;
    private PlantsAdapter mPlantsSpinnerAdapter;
    private boolean mTouchedSpinner = false;
    private JsonArray mPlants = new JsonArray();

    private ListView mAttendeeListView;
    private AttendeeAdapter mAttendeeAdapter;
    private JsonArray mAttendees = new JsonArray();

    private Bundle mSavedInstanceState = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_dining, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        setTitle(getString(R.string.string_fragment_dining_title));
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
        mPlantsSpinner = (Spinner) root.findViewById(R.id.fragment_dining_plants_spinner);

        if (mPlantsSpinner != null) {
            mPlantsSpinnerAdapter = new PlantsAdapter(getActivity());
            mPlantsSpinner.setAdapter(mPlantsSpinnerAdapter);
            mPlantsSpinner.setOnItemSelectedListener(plants_selected);
            mPlantsSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mTouchedSpinner = true;
                    return false;
                }
            });
        }

        mAttendeeListView = (ListView) root.findViewById(R.id.fragment_dining_list);
        if (mAttendeeListView != null) {
            mAttendeeAdapter = new AttendeeAdapter(getActivity());
            mAttendeeListView.setAdapter(mAttendeeAdapter);
        }

        root.findViewById(R.id.fragment_dining_barcode_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_iac_id_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_button).setOnClickListener(button_click);
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
        //Fill courses if necessary
        if ((mPlants == null || mPlants.size() <= 1) && c != null) {
            String adminToken = AppPreferences.getAdminToken(c);
            String iacId = AppPreferences.getIacId(c);

            //TODO: Call service for plants
            mPlants = new JsonArray();
            JsonObject plant = new JsonObject();
            plant.addProperty("id", 1);
            plant.addProperty("name", "Monterrey");
            mPlants.add(plant);
        }
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
                case R.id.fragment_dining_guests_button:
                    showGuests();
                    break;
                case R.id.fragment_attendees_iac_id_button:
                    showIacIdDialog();
                    break;
                case R.id.fragment_attendees_barcode_button:
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
            editTextIacId.setHint(R.string.string_fragment_attendees_new_input_hint);
            editTextIacId.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editTextIacId.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            editTextIacId.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.string_fragment_attendees_new_title);
            dialog.setView(editTextIacId);

            //Add
            dialog.setPositiveButton(R.string.string_fragment_attendees_new_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addNewAttendee(editTextIacId.getText().toString());
                            dialogInterface.dismiss();
                        }
                    });
            //Cancel
            dialog.setNegativeButton(R.string.string_fragment_attendees_new_cancel,
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
        IntentIntegrator.forSupportFragment(DiningFragment.this).initiateScan();
    }

    /**
     * Validates the form before to being sent
     *
     * @return true if is valid
     */
    private boolean validateForm() {
        return mPlantsSpinner.getSelectedItemId() > 0;
    }

    /**
     * Saves the attendees for the group
     */
    private void saveAndUpload() {
        final Context c = getActivity();
        if (c != null) {
            String adminToken = AppPreferences.getAdminToken(c);
            String iacId = AppPreferences.getIacId(c);
            int courseId = (int) mPlantsSpinner.getSelectedItemId();
            ArrayList<Integer> attendees = getAttendeeList();
            if (iacId != null && courseId > 0 && attendees != null) {
                //TODO: Save
            }

        }
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
            dialog.setTitle(R.string.string_fragment_attendees_barcode_add_title);
            dialog.setMessage(R.string.string_fragment_attendees_barcode_add_body);
            dialog.setPositiveButton(R.string.string_fragment_attendees_barcode_add_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showBarcodeReader();
                    dialogInterface.dismiss();
                }
            });
            dialog.setNegativeButton(R.string.string_fragment_attendees_barcode_add_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.create().show();
        }
    }

    /**
     * Courses item selected listener. Each courses brings you a set of attendees
     */
    private AdapterView.OnItemSelectedListener plants_selected = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long itemId) {
            if (itemId > 0 && view != null && mTouchedSpinner) {
                //Call attendees for the course
                final Context c = getActivity();
                String adminToken = AppPreferences.getAdminToken(c);
                String iacId = AppPreferences.getIacId(c);
                new CoursesService(AppConfig.getUUID(c), adminToken)
                        .getAttendees(adminToken, iacId, (int) itemId, new CoursesService.ServiceHandler() {
                            @Override
                            public void onSuccess(CoursesService.CoursesResponse response) {
                                LogUtil.d(TAG, response.json.toString());
                                if (response.json != null || !response.json.isJsonNull()) {
                                    mAttendees = response.json.getAsJsonArray();
                                    mAttendeeAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(CoursesService.CoursesResponse response) {
                                ViewUtility.showMessage(c, ViewUtility.MSG_ERROR, "Error");
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
            showAttendeeElements(itemId);
            mTouchedSpinner = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            showAttendeeElements(0);
        }
    };

    /**
     * Shows or hides the attendee layouts
     *
     * @param itemId if any course is selected, that means <code>itemId > 0</code>
     */
    private void showAttendeeElements(long itemId) {
        int visibility = itemId > 0 ? View.VISIBLE : View.GONE;
        try {
            getView().findViewById(R.id.fragment_dining_title).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_add_layout).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_attendee_subtitle).setVisibility(visibility);
            getView().findViewById(R.id.fragment_dining_list).setVisibility(visibility);
        } catch (NullPointerException npe) {
            LogUtil.e(TAG, npe.getMessage(), npe);
        }
    }

    /**
     * Courses spinner adapter. Shows a list of courses for {@link Spinner} view.
     * Displays an empty item by default
     */
    class PlantsAdapter extends BaseAdapter {
        private static final String PLANT_ID = "id";
        private static final String PLANT_NAME = "name";
        private LayoutInflater mInflater;
        private JsonElement mDefaultValue = new JsonObject();

        /**
         * Initializes the courses adapter
         *
         * @param context
         */
        public PlantsAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDefaultValue.getAsJsonObject().addProperty(PLANT_ID, "0");
            mDefaultValue.getAsJsonObject().addProperty(PLANT_NAME, "Seleccione");
            if (mPlants == null)
                mPlants = new JsonArray();
            if (mPlants.size() == 0)
                mPlants.add(mDefaultValue);
        }

        @Override
        public int getCount() {
            return mPlants == null ? 0 : mPlants.size();
        }

        @Override
        public Object getItem(int i) {
            return mPlants == null ? null : mPlants.get(i);
        }

        @Override
        public long getItemId(int i) {
            try {
                return mPlants == null ? 0L :
                        Long.parseLong(mPlants.get(i).getAsJsonObject().get(PLANT_ID).getAsString());
            } catch (Exception e) {
                return 0L;
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            if (view == null)
                v = mInflater.inflate(R.layout.list_item_right, viewGroup, false);
            else
                v = view;

            JsonElement jsonElement = (JsonElement) getItem(i);
            String name = jsonElement.getAsJsonObject().get(PLANT_NAME).getAsString();
            TextView textView = (TextView) v.findViewById(R.id.list_item_right_text);
            textView.setText(name);
            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null)
                v = mInflater.inflate(R.layout.item_dropdown_right, parent, false);
            else
                v = convertView;
            JsonElement jsonElement = (JsonElement) getItem(position);
            String name = jsonElement.getAsJsonObject().get(PLANT_NAME).getAsString();
            CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.list_item_right_text);
            textView.setText(getItemId(position) != 0 ? name : "");
            return v;
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
                alertDialog.setTitle(R.string.string_fragment_attendees_delete_title);
                String body = String.format(Locale.getDefault(),
                        getString(R.string.string_fragment_attendees_delete_body),
                        attendee.get(ATTENDEE_NAME).getAsString());
                alertDialog.setMessage(body);
                //Yes delete
                alertDialog.setPositiveButton(R.string.string_fragment_attendees_delete_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeAttendee(attendee);
                                dialogInterface.dismiss();
                            }
                        });
                //No delete
                alertDialog.setNegativeButton(R.string.string_fragment_attendees_delete_cancel,
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
