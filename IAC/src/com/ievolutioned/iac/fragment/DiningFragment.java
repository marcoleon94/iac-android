package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.Site;
import com.ievolutioned.iac.net.service.ProfileService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.util.ViewUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.util.ArrayList;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Attendees for dining fragment class. Allows to add and modify attendees for any plant dining room
 * <p>
 * Created by Daniel on 21/03/2017.
 */

public class DiningFragment extends BaseFragmentClass {

    private static final String TAG = DiningFragment.class.getName();
    private static final String ARGS_PLANT = "ARGS_PLANT";
    private static final String ARGS_ATTENDEES = "ARGS_ATTENDEES";

    public interface SupportCategory {
        String NORMAL = "NORMAL";
        String NO_SUPPORT = "SIN SUBSIDIO";
        String EXTRA_TIME = "TIEMPO EXTRA";
    }

    public interface SupportType {
        String FOOD = "COMIDA";
        String BEVERAGE = "REFRESCO";
        String WATER = "AGUA";
    }

    private TextView mPlant;
    private Site mSite;

    private SegmentedGroup mSegmentedSupportType;

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

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mSavedInstanceState = new Bundle(savedInstanceState);
        super.onCreate(savedInstanceState);
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
        if (mSite != null) {
            String siteOut = new Gson().toJson(mSite, Site.class);
            outState.putString(ARGS_PLANT, siteOut);
        }
        if (mAttendees != null) {
            ArrayList<String> attendees = new ArrayList<>(mAttendees.size());
            for (JsonElement j : mAttendees)
                attendees.add(j.getAsJsonObject().toString());
            outState.putStringArrayList(ARGS_ATTENDEES, attendees);
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

        setToolbarNavigationDisplayHomeAsUpEnabled(false);

        mPlant = (TextView) root.findViewById(R.id.fragment_dining_plant);

        mSegmentedSupportType = (SegmentedGroup) root.findViewById(R.id.fragment_dining_support_type_segmented);

        mAttendeeListView = (ListView) root.findViewById(R.id.fragment_dining_list);
        if (mAttendeeListView != null) {
            mAttendeeAdapter = new AttendeeAdapter(getActivity());
            mAttendeeListView.setAdapter(mAttendeeAdapter);
        }

        root.findViewById(R.id.fragment_dining_iac_id_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_normal_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_extra_time_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_no_support_button).setOnClickListener(button_click);
    }

    /**
     * Binds any data if necessary
     *
     * @param args - {@link Bundle} arguments
     */
    private void bindData(Bundle args) {
        //TODO: restore state, add more things to load at first
        if (!restoreState(mSavedInstanceState) && mSite == null) {
            //Initial bind
            Context c = getActivity();
            String adminToken = AppPreferences.getAdminToken(c);
            String deviceId = AppConfig.getUUID(c);
            final android.app.AlertDialog loadingScreen = ViewUtility.getLoadingScreen(c);
            loadingScreen.show();
            //Call profile service
            new ProfileService(deviceId, adminToken).getProfileInfo(new ProfileService.ProfileServiceHandler() {
                @Override
                public void onSuccess(ProfileService.ProfileResponse response) {
                    mSite = response.profile.getSite();
                    if (mSite != null && mSite.getName() != null && mSite.getName().length() > 0)
                        mPlant.setText(mSite.getName());
                    else
                        ViewUtility.showMessage(getContext(), ViewUtility.MSG_ERROR,
                                R.string.string_fragment_dining_plant_fetch_error);
                    try {
                        loadingScreen.dismiss();
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onError(ProfileService.ProfileResponse response) {
                    try {
                        loadingScreen.dismiss();
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancel() {
                    try {
                        loadingScreen.dismiss();
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    /**
     * Restores the state of courses loaded
     *
     * @param args - previous state
     * @return if state was restored
     */
    private boolean restoreState(Bundle args) {
        if (mSavedInstanceState == null || !mSavedInstanceState.containsKey(ARGS_ATTENDEES))
            return false;
        try {
            mSite = new Gson().fromJson(mSavedInstanceState.getString(ARGS_PLANT), Site.class);
            if (mSite != null && mSite.getName() != null && mSite.getName().length() > 0)
                mPlant.setText(mSite.getName());
            else
                ViewUtility.showMessage(getContext(), ViewUtility.MSG_ERROR,
                        R.string.string_fragment_dining_plant_fetch_error);
            return true;
        } catch (Exception e) {
            return false;
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
    private void addNewAttendee(final String iacId, final String category, final String type) {
        //Verify if it exists
        if (iacId == null || isAttendeeInList(iacId))
            return;
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);

        //TODO: Add new attendee
        JsonObject attendee = new JsonObject();
        attendee.addProperty(AttendeeAdapter.ATTENDEE_ID, mAttendees.size());
        attendee.addProperty(AttendeeAdapter.ATTENDEE_IAC_ID, iacId);
        attendee.addProperty(AttendeeAdapter.ATTENDEE_NAME, "Demo");
        attendee.addProperty(AttendeeAdapter.ATTENDEE_SUPPORT_CATEGORY, category);
        attendee.addProperty(AttendeeAdapter.ATTENDEE_SUPPORT_TYPE, type);
        mAttendees.add(attendee);
        mAttendeeAdapter.notifyDataSetChanged();
        LogUtil.d(TAG, mAttendees.toString());
        ViewUtil.setListViewHeightBasedOnChildren(mAttendeeListView);
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
                case R.id.fragment_dining_iac_id_button:
                    showIacIdDialog();
                    break;
                case R.id.fragment_dining_barcode_normal_button:
                    showBarcodeReader(SupportCategory.NORMAL);
                    break;
                case R.id.fragment_dining_barcode_no_support_button:
                    showBarcodeReader(SupportCategory.NO_SUPPORT);
                    break;
                case R.id.fragment_dining_barcode_extra_time_button:
                    showBarcodeReader(SupportCategory.EXTRA_TIME);
                    break;
                default:
                    break;
            }
        }
    };


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
                            //addNewAttendee(editTextIacId.getText().toString());
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
     *
     * @param args
     */
    private void showBarcodeReader(String args) {
        AppPreferences.setDiningArgsType(getContext(), args);
        IntentIntegrator.forSupportFragment(DiningFragment.this).initiateScan();
    }

    /**
     * Validates the form before to being sent
     *
     * @return true if is valid
     */
    private boolean validateForm() {
        return mSite != null;
    }

    /**
     * Saves the attendees for the group
     */
    private void saveAndUpload() {
        final Context c = getActivity();
        if (c != null) {
            String adminToken = AppPreferences.getAdminToken(c);
            String iacId = AppPreferences.getIacId(c);
            long courseId = mSite.getId();
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
        if (result != null && !TextUtils.isEmpty(result.getContents()))
            handleScanResult(result.getContents());
        else
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, "Error");


    }

    private void handleScanResult(final String content) {
        //TODO: CALL
        callServiceFor(content);
    }

    private void callServiceFor(String iacId) {
        //TODO: MAKE the call
        //addNewAttendee();
        if (mAttendees.size() < 2)
            addNewAttendee(iacId, getSupportCategory(), getSupportType());
        else
            showAttendeeDialog(iacId);
    }

    private void showAttendeeDialog(String input) {
        String support = getSupportType();
        String desiredType = getSupportCategory();
        Bundle args = new Bundle();
        args.putString(DiningAttendeeDialogFragment.ARGS_INPUT, input);
        args.putString(DiningAttendeeDialogFragment.ARGS_TYPE, desiredType);
        args.putString(DiningAttendeeDialogFragment.ARGS_SUPPORT, support);

        final DiningAttendeeDialogFragment attendeeDialogFragment = DiningAttendeeDialogFragment.newInstance(args);
        attendeeDialogFragment.show(getFragmentManager(), DiningAttendeeDialogFragment.TAG);
    }

    private String getSupportCategory() {
        if (getActivity() == null)
            return SupportCategory.NORMAL;
        String support = AppPreferences.getDiningArgsType(getActivity());
        return support == null ? SupportCategory.NORMAL : support;
    }

    private String getSupportType() {
        if (mSegmentedSupportType == null)
            return SupportType.FOOD;
        switch (mSegmentedSupportType.getCheckedRadioButtonId()) {
            case R.id.fragment_dining_support_type_food:
                return SupportType.FOOD;
            case R.id.fragment_dining_support_type_beverage:
                return SupportType.BEVERAGE;
            case R.id.fragment_dining_support_type_water:
                return SupportType.WATER;
            default:
                return SupportType.FOOD;

        }
    }


    /**
     * Attendees list adapter. A list of attendees in the {@link ListView} element
     */
    class AttendeeAdapter extends BaseAdapter implements View.OnClickListener {

        protected final static String ATTENDEE_ID = "id";
        protected final static String ATTENDEE_NAME = "name";
        protected final static String ATTENDEE_IAC_ID = "iac_id";
        protected final static String ATTENDEE_SUPPORT_CATEGORY = "category";
        protected final static String ATTENDEE_SUPPORT_TYPE = "type";
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
            ImageView viewCategory = (ImageView) view.findViewById(R.id.list_item_attendee_support_category);
            ImageView viewType = (ImageView) view.findViewById(R.id.list_item_attendee_support_type);

            textViewName.setText(attendee.get(ATTENDEE_NAME).getAsString());
            textViewId.setText(attendee.get(ATTENDEE_IAC_ID).getAsString());
            viewCategory.setImageResource(getImageResource(attendee.get(ATTENDEE_SUPPORT_CATEGORY).getAsString()));
            viewType.setImageResource(getImageResource(attendee.get(ATTENDEE_SUPPORT_TYPE).getAsString()));
            view.findViewById(R.id.list_item_attendee_delete_button).setTag(getItem(i));
            view.setTag(getItem(i));
            return view;
        }

        private int getImageResource(String s) {
            if (s == null)
                return R.drawable.ic_mapcross_dummy;
            switch (s) {
                case SupportCategory.NORMAL:
                    return R.drawable.ic_normal;
                case SupportCategory.NO_SUPPORT:
                    return R.drawable.ic_no_support;
                case SupportCategory.EXTRA_TIME:
                    return R.drawable.ic_extra_time;
                case SupportType.FOOD:
                    return R.drawable.ic_food;
                case SupportType.BEVERAGE:
                    return R.drawable.ic_soda;
                case SupportType.WATER:
                    return R.drawable.ic_water;
                default:
                    return R.drawable.ic_mapcross_dummy;
            }
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
