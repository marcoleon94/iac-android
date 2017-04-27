package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.Site;
import com.ievolutioned.iac.entity.Support;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.net.service.DiningService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.FormatUtil;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Guests for dining fragment class. Allows to add and modify attendees for any plant dining room
 * <p>
 * Created by Daniel on 23/03/2017.
 */

public class DiningGuestsFragment extends BaseFragmentClass {

    public static final String TAG = DiningGuestsFragment.class.getName();
    public static final String ARGS_HOST = "ARGS_HOST";
    public static final String ARGS_GUESTS_SAVED = "ARGS_GUESTS_SAVED";

    private static final boolean HAS_OPTION_MENU = false;

    private static final int EXTRA_HOST = 1;
    private static final int EXTRA_GUEST = 2;

    private SegmentedGroup mSegmentedSupportType;

    private ListView mGuestsListView;
    private DiningGuestsAttendeeAdapter mGuestsAdapter;
    private JsonArray mGuests = new JsonArray();

    private View mHostDetailsView;
    private View mHostInputView;
    private TextView mHostIacId;
    private TextView mHostName;
    private ImageView mHostCategory;
    private ImageView mHostType;
    private JsonObject mCurrentHost;

    private Bundle mSavedInstanceState = null;
    private Site mSite;

    /**
     * Creates anew instrance with {@link Bundle } arguments
     *
     * @param args
     * @return
     */
    public static Fragment newInstance(Bundle args) {
        DiningGuestsFragment fragment = new DiningGuestsFragment();
        if (args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_dining_guests, container, false);
        setHasOptionsMenu(HAS_OPTION_MENU);
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
        inflater.inflate(R.menu.fragment_dining_guests_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_dining_guests:
                if (!NetUtil.hasNetworkConnection(getActivity())) {
                    ViewUtility.displayNetworkPreferences(getActivity());
                    break;
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

        //Toolbar
        setToolbarNavigationDisplayHomeAsUpEnabled(true);

        mHostDetailsView = root.findViewById(R.id.fragment_dining_guests_host_details);
        mHostInputView = root.findViewById(R.id.fragment_dining_guests_host_input_layout);

        mGuestsListView = (ListView) root.findViewById(R.id.fragment_dining_guests_list);
        if (mGuestsListView != null) {
            mGuestsAdapter = new DiningGuestsAttendeeAdapter(getActivity());
            mGuestsListView.setAdapter(mGuestsAdapter);
        }

        mSegmentedSupportType = (SegmentedGroup) root.findViewById(R.id.fragment_dining_guests_host_support_type_segmented);

        mHostIacId = (TextView) root.findViewById(R.id.list_item_attendee_id);
        mHostName = (TextView) root.findViewById(R.id.list_item_attendee_name);
        mHostCategory = (ImageView) root.findViewById(R.id.list_item_attendee_support_category);
        mHostType = (ImageView) root.findViewById(R.id.list_item_attendee_support_type);

        root.findViewById(R.id.list_item_attendee_delete_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_iac_id_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_normal_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_extra_time_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_barcode_no_support_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_manual_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_iac_id_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_dining_guests_barcode_button).setOnClickListener(button_click);

        setDrawablesRadioButtons(root);
    }

    /**
     * Sets drawables for {@link RadioButton}
     *
     * @param root find view
     */
    private void setDrawablesRadioButtons(View root) {
        Rect bounds = new Rect(0, 0, 40, 50);

        Drawable food = ContextCompat.getDrawable(getActivity(), R.drawable.ic_food);
        food.setBounds(bounds);
        ((RadioButton) root.findViewById(R.id.fragment_dining_support_type_food)).setCompoundDrawables(null, null, food, null);

        Drawable beverage = ContextCompat.getDrawable(getActivity(), R.drawable.ic_soda);
        beverage.setBounds(bounds);
        ((RadioButton) root.findViewById(R.id.fragment_dining_support_type_beverage)).setCompoundDrawables(null, null, beverage, null);

        Drawable water = ContextCompat.getDrawable(getActivity(), R.drawable.ic_water);
        water.setBounds(bounds);
        ((RadioButton) root.findViewById(R.id.fragment_dining_support_type_water)).setCompoundDrawables(null, null, water, null);

    }

    /**
     * Binds any data if necessary
     *
     * @param args - {@link Bundle} arguments
     */
    private void bindData(Bundle args) {
        restoreState(mSavedInstanceState != null ? mSavedInstanceState : args);
    }

    /**
     * Restores the state of courses loaded
     *
     * @param args - previous state
     */
    private void restoreState(Bundle args) {
        try {
            if (args != null) {
                //Get site
                if (args.containsKey(DiningFragment.ARGS_PLANT)) {
                    mSite = new Gson().fromJson(args.getString(DiningFragment.ARGS_PLANT),
                            Site.class);
                }

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
     * Gets the support type
     *
     * @return
     */
    private String getSupportType() {
        if (mSegmentedSupportType == null)
            return Support.Type.FOOD;
        switch (mSegmentedSupportType.getCheckedRadioButtonId()) {
            case R.id.fragment_dining_support_type_food:
                return Support.Type.FOOD;
            case R.id.fragment_dining_support_type_beverage:
                return Support.Type.BEVERAGE;
            case R.id.fragment_dining_support_type_water:
                return Support.Type.WATER;
            default:
                return Support.Type.FOOD;

        }
    }

    /**
     * Gets the support category
     *
     * @return
     */
    private String getSupportCategory() {
        if (getActivity() == null)
            return Support.Category.NORMAL;
        String support = AppPreferences.getDiningArgsType(getActivity());
        return support == null ? Support.Category.NORMAL : support;
    }


    /**
     * Remove attendee from list of attendees
     *
     * @param guest - the attendee
     */
    private void removeGuest(final JsonObject guest) {
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
     * @param id
     * @param iacId
     * @param name
     * @param category
     */
    private void addHost(long id, final String iacId, final String name, final String category,
                         final String type) {

        mCurrentHost = new JsonObject();
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_ID, id);
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_IAC_ID, iacId);
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_NAME, name);
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_CATEGORY, category);
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_TYPE, type);
        mCurrentHost.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_DATE,
                FormatUtil.parseDate(new Date()));
        showHost();
    }

    /**
     * Removes host
     */
    private void removeHost() {
        mCurrentHost = null;
        showHost();
    }

    /**
     * Show hosts if it's set
     */
    private void showHost() {
        if (mCurrentHost != null && !mCurrentHost.isJsonNull() &&
                mHostDetailsView != null && mHostIacId != null && mHostName != null) {
            mHostDetailsView.setVisibility(View.VISIBLE);
            mHostIacId.setText(mCurrentHost.get(DiningGuestsAttendeeAdapter.ATTENDEE_IAC_ID).getAsString());
            mHostName.setText(mCurrentHost.get(DiningGuestsAttendeeAdapter.ATTENDEE_NAME).getAsString());
            mHostCategory.setImageResource(getImageResource(mCurrentHost
                    .get(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_CATEGORY).getAsString()));
            mHostType.setImageResource(getImageResource(mCurrentHost
                    .get(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_TYPE).getAsString()));
            mHostInputView.setVisibility(View.GONE);
        } else if (mHostDetailsView != null) {
            mHostDetailsView.setVisibility(View.GONE);
            mHostInputView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Adds a new attendee with the current params
     *
     * @param id
     * @param iacId
     * @param name
     * @param category
     * @param type
     * @param date
     */
    private void addNewAttendee(final long id, final String iacId, final String name,
                                final String category, final String type, final Date date) {
        //Verify if it exists
        if (iacId == null || isAttendeeInList(iacId))
            return;

        JsonObject attendee = new JsonObject();
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_ID, id);
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_IAC_ID, iacId);
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_NAME, name);
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_CATEGORY, category);
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_SUPPORT_TYPE, type);
        attendee.addProperty(DiningGuestsAttendeeAdapter.ATTENDEE_DATE, FormatUtil.parseDate(date));

        JsonArray newAttendees = new JsonArray();
        newAttendees.add(attendee);
        newAttendees.addAll(mGuests);
        mGuests = newAttendees;

        mGuestsAdapter.notifyDataSetChanged();
        LogUtil.d(TAG, mGuests.toString());
        //ViewUtil.setListViewHeightBasedOnChildren(mGuestsListView);
    }


    /**
     * Returns if an attendee is in a list
     *
     * @param iacId - String id
     * @return true if the attendee id is in the list, false otherwise
     */
    private boolean isAttendeeInList(String iacId) {
        if (iacId.contentEquals(Support.Category.GUEST_DEFAULT_IAC_ID))
            return false;
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
            if (!NetUtil.hasNetworkConnection(getActivity())) {
                ViewUtility.displayNetworkPreferences(getActivity());
                return;
            }

            switch (view.getId()) {
                //HOST
                case R.id.list_item_attendee_delete_button:
                    removeHost();
                    break;
                case R.id.fragment_dining_iac_id_button:
                    showAttendeeDialog(null, Support.Category.NORMAL, Support.Type.FOOD,
                            DiningAttendeeDialogFragment.NO_ERROR);
                    break;
                case R.id.fragment_dining_barcode_normal_button:
                    showBarcodeReader(EXTRA_HOST, Support.Category.NORMAL);
                    break;
                case R.id.fragment_dining_barcode_no_support_button:
                    showBarcodeReader(EXTRA_HOST, Support.Category.NO_SUPPORT);
                    break;
                case R.id.fragment_dining_barcode_extra_time_button:
                    showBarcodeReader(EXTRA_HOST, Support.Category.EXTRA_TIME);
                    break;
                //GUESTS
                case R.id.fragment_dining_guests_manual_button:
                    showManualInput();
                    break;
                case R.id.fragment_dining_guests_iac_id_button:
                    showIacIdDialog(EXTRA_GUEST);
                    break;
                case R.id.fragment_dining_guests_barcode_button:
                    showBarcodeReader(EXTRA_GUEST, Support.Category.GUEST);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Shows the attendee dialog
     *
     * @param input
     * @param category
     * @param type
     * @param errorCode
     */
    private void showAttendeeDialog(final String input, final String category, final String type,
                                    final int errorCode) {
        Bundle args = new Bundle();
        if (input != null)
            args.putString(DiningAttendeeDialogFragment.ARGS_INPUT, input);
        args.putString(DiningAttendeeDialogFragment.ARGS_CATEGORY, category);
        args.putString(DiningAttendeeDialogFragment.ARGS_TYPE, type);
        args.putInt(DiningAttendeeDialogFragment.ARGS_ERROR_CODE, errorCode);

        final DiningAttendeeDialogFragment attendeeDialogFragment = DiningAttendeeDialogFragment.newInstance(args);
        attendeeDialogFragment.setDiningManualCallback(new DiningAttendeeDialogFragment.IDiningManual() {
            @Override
            public void onAccept(String input, String category, String type) {
                callServiceFor(EXTRA_HOST, input, category, type);
            }
        });
        attendeeDialogFragment.show(getFragmentManager(), DiningAttendeeDialogFragment.TAG);
    }

    /**
     * Validate a user for enter to diner room
     *
     * @param extra
     * @param iacId
     * @param category
     * @param type
     */
    private void callServiceFor(final int extra, final String iacId, final String category, final String type) {
        String adminToken = AppPreferences.getAdminToken(getActivity());
        String deviceId = AppConfig.getUUID(getActivity());
        boolean restricted = category != null && category.contentEquals(Support.Category.NORMAL);

        new DiningService(deviceId, adminToken).getValidateDiningRoom(iacId, restricted,
                new DiningService.ServiceHandler() {
                    @Override
                    public void onSuccess(DiningService.DiningResponse response) {
                        handleServiceCallResponse(extra, iacId, response.json, category, type);
                        LogUtil.d(TAG, response.msg);
                    }

                    @Override
                    public void onError(DiningService.DiningResponse response) {
                        ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR,
                                R.string.string_fragment_dining_guests_general_error);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    /**
     * Handles the validation user for dining room
     *
     * @param extra
     * @param iacId
     * @param response
     * @param category
     * @param type
     */
    private void handleServiceCallResponse(final int extra, final String iacId, JsonElement response,
                                           final String category, final String type) {
        try {
            JsonObject responseObject = response.getAsJsonObject();
            if (responseObject.has(DiningService.COMENSAL) && !responseObject.get(DiningService.COMENSAL).isJsonNull()) {
                switch (responseObject.get(DiningService.ERROR_CODE).getAsInt()) {
                    case DiningService.ErrorCodes.NO_ERROR:
                        JsonObject comensal = responseObject.get(DiningService.COMENSAL).getAsJsonObject();
                        if (extra == EXTRA_HOST)
                            addHost(comensal.get("id").getAsLong(), iacId,
                                    comensal.get("name").getAsString(), category, type);
                        else
                            addNewAttendee(comensal.get("id").getAsLong(), iacId,
                                    comensal.get("name").getAsString(), category, type, new Date());
                        break;
                    case DiningService.ErrorCodes.NO_USER:
                        ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR,
                                R.string.string_fragment_dining_new_no_record);
                        break;
                    case DiningService.ErrorCodes.NO_ENTER:
                        if (extra == EXTRA_HOST)
                            showAttendeeDialog(iacId, category, type,
                                    DiningAttendeeDialogFragment.ERROR_RESTRICTED);
                        else
                            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR,
                                    R.string.string_fragment_dining_new_no_record);
                        break;
                    default:
                        break;
                }
            } else {
                showAttendeeDialog(iacId, category, type, DiningAttendeeDialogFragment.ERROR_NOT_FOUND);
            }
        } catch (Exception e) {
            //Error
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

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
                            String iacId = editTextIacId.getText().toString().trim();
                            if (iacId.length() > 0) {
                                callServiceFor(EXTRA_GUEST, iacId, Support.Category.GUEST, Support.Type.FOOD);
                            }
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
     * Shows a manual input
     */
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
                            String name = editText.getText().toString().trim();
                            if (name.length() > 0) {
                                addNewAttendee(Support.Category.GUEST_ID_DEFAULT,
                                        Support.Category.GUEST_DEFAULT_IAC_ID,
                                        name, Support.Category.GUEST, Support.Type.FOOD, new Date());
                            } else
                                ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, R.string.string_fragment_dining_guests_general_error);
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
    private void showBarcodeReader(int extra, final String category) {
        AppPreferences.setDiningBarcodeTemporal(getActivity(), extra);
        IntentIntegrator.forSupportFragment(DiningGuestsFragment.this).initiateScan();
    }

    /**
     * Saves the dining attendees and host for the menu option
     */
    private void doSaveOption() {
        if (validateForm())
            saveAndUpload();
        else
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR,
                    R.string.string_fragment_dining_guests_save_error_validation);
    }

    /**
     * Saves the dining attendees on back pressed event
     */
    private void doSaveBackPressed() {
        if (validateForm())
            saveAndUpload();
        else
            showBackUnsavedDialog();
    }

    /**
     * Shows error on save decision, allows exit without saving
     */
    private void showBackUnsavedDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.mAttachedActivity);
        dialog.setTitle(R.string.string_fragment_dining_guests_dialog_title);
        dialog.setMessage(R.string.string_fragment_dining_guests_dialog_body);
        dialog.setPositiveButton(R.string.string_fragment_dining_guests_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closeGuests();
            }
        });

        dialog.setNegativeButton(R.string.string_fragment_dining_guests_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create().show();
    }

    /**
     * Validates the form before to being sent
     *
     * @return true if is valid
     */
    private boolean validateForm() {
        return mCurrentHost != null && mGuests != null && mGuests.size() > 0;
    }

    /**
     * Saves the attendees for the group
     */
    private void saveAndUpload() {
        final Context context = getActivity();
        String type = getSupportType();
        String category = getSupportCategory();

        if (context != null && mSite != null && type != null && category != null && mCurrentHost != null
                && mGuests != null && mGuests.size() > 0) {
            String register = DiningService.getDiningRegisterBody(mSite.getId(), type, category,
                    mCurrentHost, mGuests);
            LogUtil.d(TAG, register);

            new DiningService(AppConfig.getUUID(context), AppPreferences.getAdminToken(context))
                    .registerNewCommensal(register, new DiningService.ServiceHandler() {
                        @Override
                        public void onSuccess(DiningService.DiningResponse response) {
                            LogUtil.d(TAG, response.msg);
                            ViewUtility.showMessage(context, ViewUtility.MSG_SUCCESS,
                                    R.string.string_fragment_dining_modify_save_success);
                            closeGuests();
                        }

                        @Override
                        public void onError(DiningService.DiningResponse response) {
                            LogUtil.d(TAG, response.msg);
                            ViewUtility.showMessage(context, ViewUtility.MSG_ERROR,
                                    R.string.string_fragment_dining_guests_modify_save_error);
                            if (!HAS_OPTION_MENU)
                                showBackUnsavedDialog();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

        }
    }

    /**
     * back stack to the previous fragment
     */
    private void closeGuests() {
        if (this.mAttachedActivity != null)
            this.mAttachedActivity.onBackPressed();
    }

    /**
     * Saves and goes back to the previus fragment
     */
    public void onBackPressed() {
        if (HAS_OPTION_MENU)
            doSaveOption();
        else
            doSaveBackPressed();
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
                        .get(DiningGuestsAttendeeAdapter.ATTENDEE_ID).getAsString()));
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
            handleScanResult(from, result.getContents());
        }
    }

    /**
     * Scan result handler
     *
     * @param from
     * @param iacId
     */
    private void handleScanResult(int from, final String iacId) {
        if (from == EXTRA_HOST) {
            if (!isAttendeeInList(iacId)) {
                final String category = getSupportCategory();
                final String type = getSupportType();
                callServiceFor(EXTRA_HOST, iacId, category, type);
            } else
                ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, "Comensal previamente registrado");
        } else {
            //For guests
            if (!isAttendeeInList(iacId)) {
                final String type = getSupportType();
                callServiceFor(EXTRA_GUEST, iacId, Support.Category.GUEST, Support.Type.FOOD);
            } else
                ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR, "Comensal previamente registrado");
        }
    }

    /**
     * Get the Icons for guests and host support type and category
     *
     * @param s
     * @return
     */
    private int getImageResource(String s) {
        if (s == null)
            return R.drawable.ic_mapcross_dummy;
        switch (s) {
            case Support.Category.NORMAL:
                return R.drawable.ic_normal;
            case Support.Category.NO_SUPPORT:
                return R.drawable.ic_no_support;
            case Support.Category.EXTRA_TIME:
                return R.drawable.ic_extra_time;
            case Support.Category.GUEST:
                return android.R.drawable.ic_menu_myplaces;
            case Support.Type.FOOD:
                return R.drawable.ic_food;
            case Support.Type.BEVERAGE:
                return R.drawable.ic_soda;
            case Support.Type.WATER:
                return R.drawable.ic_water;
            default:
                return R.drawable.ic_mapcross_dummy;
        }
    }


    /**
     * {@link DiningGuestsAttendeeAdapter} list adapter for guests
     */
    public class DiningGuestsAttendeeAdapter extends BaseAdapter implements View.OnClickListener {

        protected final static String ATTENDEE_ID = "id";
        protected final static String ATTENDEE_NAME = "name";
        protected final static String ATTENDEE_IAC_ID = "iac_id";
        protected final static String ATTENDEE_SUPPORT_CATEGORY = "category";
        protected final static String ATTENDEE_SUPPORT_TYPE = "type";
        protected final static String ATTENDEE_DATE = "date";

        private LayoutInflater mInflater;

        /**
         * {@link DiningGuestsAttendeeAdapter} initializer
         *
         * @param c
         */
        public DiningGuestsAttendeeAdapter(Context c) {
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
            return i + 1;
            /*
            try {
                return mGuests == null ? 0L :
                        Long.parseLong(mGuests.get(i).getAsJsonObject().get(ATTENDEE_ID).getAsString());
            } catch (Exception e) {
                return 0L;
            }
            */
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_attendee_dinner, viewGroup, false);
                view.findViewById(R.id.list_item_attendee_delete_button).setOnClickListener(this);
            }

            JsonObject attendee = (JsonObject) getItem(i);
            TextView textViewName = (TextView) view.findViewById(R.id.list_item_attendee_name);
            TextView textViewId = (TextView) view.findViewById(R.id.list_item_attendee_id);
            TextView textViewDate = (TextView) view.findViewById(R.id.list_item_attendee_date);
            ImageView viewCategory = (ImageView) view.findViewById(R.id.list_item_attendee_support_category);
            ImageView viewType = (ImageView) view.findViewById(R.id.list_item_attendee_support_type);

            textViewName.setText(attendee.get(ATTENDEE_NAME).getAsString());
            textViewId.setText(attendee.get(ATTENDEE_IAC_ID).getAsString());
            textViewDate.setText(attendee.get(ATTENDEE_DATE).getAsString());
            viewCategory.setImageResource(getImageResource(attendee.get(ATTENDEE_SUPPORT_CATEGORY).getAsString()));
            viewType.setImageResource(getImageResource(attendee.get(ATTENDEE_SUPPORT_TYPE).getAsString()));
            view.findViewById(R.id.list_item_attendee_delete_button).setOnClickListener(this);
            view.findViewById(R.id.list_item_attendee_delete_button).setTag(getItem(i));
            view.setTag(getItem(i));
            return view;
        }

        private int getImageResource(String s) {
            if (s == null)
                return R.drawable.ic_mapcross_dummy;
            switch (s) {
                case Support.Category.NORMAL:
                    return R.drawable.ic_normal;
                case Support.Category.NO_SUPPORT:
                    return R.drawable.ic_no_support;
                case Support.Category.EXTRA_TIME:
                    return R.drawable.ic_extra_time;
                case Support.Category.GUEST:
                    return android.R.drawable.ic_menu_myplaces;
                case Support.Type.FOOD:
                    return R.drawable.ic_food;
                case Support.Type.BEVERAGE:
                    return R.drawable.ic_soda;
                case Support.Type.WATER:
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
                alertDialog.setTitle(R.string.string_fragment_dining_delete_title);
                String body = String.format(Locale.getDefault(),
                        getString(R.string.string_fragment_dining_delete_body),
                        attendee.get(ATTENDEE_NAME).getAsString());
                alertDialog.setMessage(body);
                //Yes delete
                alertDialog.setPositiveButton(R.string.string_fragment_dining_delete_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeGuest(attendee);
                                dialogInterface.dismiss();
                            }
                        });
                //No delete
                alertDialog.setNegativeButton(R.string.string_fragment_dining_delete_cancel,
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
