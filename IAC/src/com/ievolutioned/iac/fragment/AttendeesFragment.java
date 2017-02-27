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
import com.google.gson.JsonParser;
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
 * Created by Daniel on 21/02/2017.
 */

public class AttendeesFragment extends BaseFragmentClass {

    private static final String TAG = AttendeesFragment.class.getName();
    private static final String ARGS_SAVED_COURSE_ITEM_POS = "ARGS_COURSE_ID";
    private static final String ARGS_SAVED_ATTENDEES = "ARGS_SAVED_ATTENDEES";
    private static final String ARGS_SAVED_COURSES = "ARGS_SAVED_COURSES";
    private Spinner mCoursesSpinner;
    private CoursesAdapter mCoursesSpinnerAdapter;
    private boolean mTouchedSpinner = false;
    private JsonArray mCourses = new JsonArray();

    private ListView mAttendeeListView;
    private AttendeeAdapter mAttendeeAdapter;
    private JsonArray mAttendees = new JsonArray();

    private Bundle mSavedInstanceState = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_attendees, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        setTitle(getString(R.string.string_fragment_attendees_title));
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
                if (validateForm()) {
                    saveAndUpload();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARGS_SAVED_COURSE_ITEM_POS, mCoursesSpinner.getSelectedItemPosition());
        outState.putString(ARGS_SAVED_COURSES, mCourses.toString());
        outState.putString(ARGS_SAVED_ATTENDEES, mAttendees.toString());
        super.onSaveInstanceState(outState);
    }

    private void bindUI(View root) {
        if (root == null)
            return;
        mCoursesSpinner = (Spinner) root.findViewById(R.id.fragment_attendees_courses_spinner);

        if (mCoursesSpinner != null) {
            mCoursesSpinnerAdapter = new CoursesAdapter(getActivity());
            mCoursesSpinner.setAdapter(mCoursesSpinnerAdapter);
            mCoursesSpinner.setOnItemSelectedListener(courses_selected);
            mCoursesSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mTouchedSpinner = true;
                    return false;
                }
            });
        }

        mAttendeeListView = (ListView) root.findViewById(R.id.fragment_attendees_list);
        if (mAttendeeListView != null) {
            mAttendeeAdapter = new AttendeeAdapter(getActivity());
            mAttendeeListView.setAdapter(mAttendeeAdapter);
        }

        root.findViewById(R.id.fragment_attendees_barcode_button).setOnClickListener(button_click);
        root.findViewById(R.id.fragment_attendees_iac_id_button).setOnClickListener(button_click);
    }

    private void bindData(Bundle args) {
        if (mSavedInstanceState != null && mSavedInstanceState.containsKey(ARGS_SAVED_COURSES))
            restoreState(mSavedInstanceState);
        else {
            Context c = getActivity();
            //Fill courses if necessary
            if ((mCourses == null || mCourses.size() <= 1) && c != null) {
                String adminToken = AppPreferences.getAdminToken(c);
                String iacId = AppPreferences.getIacId(c);
                new CoursesService(AppConfig.getUUID(c), adminToken)
                        .getActiveCourses(adminToken, iacId, new CoursesService.ServiceHandler() {
                            @Override
                            public void onSuccess(CoursesService.CoursesResponse response) {
                                LogUtil.d(TAG, response.json.toString());
                                try {
                                    //TODO: default value in resources
                                    if (mCourses == null)
                                        mCourses = new JsonArray();
                                    mCourses.addAll(response.json.getAsJsonArray());
                                    if (mCoursesSpinnerAdapter != null)
                                        mCoursesSpinnerAdapter.notifyDataSetChanged();
                                } catch (IllegalStateException ise) {
                                    LogUtil.e(TAG, ise.getMessage(), ise);
                                }
                            }

                            @Override
                            public void onError(CoursesService.CoursesResponse response) {
                                if (mCoursesSpinnerAdapter != null)
                                    mCoursesSpinnerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancel() {
                                LogUtil.d(TAG, "binData: canceled courses");
                            }
                        });
            }
        }
    }

    private void restoreState(Bundle args) {
        try {
            JsonElement json = new JsonParser().parse(args.getString(ARGS_SAVED_COURSES));
            mCourses = new JsonArray();
            mCourses.addAll(json.getAsJsonArray());
            if (mCoursesSpinner != null) {
                mCoursesSpinner.setOnItemSelectedListener(null);
                if (mCoursesSpinnerAdapter != null) {
                    mCoursesSpinnerAdapter.notifyDataSetChanged();
                    mCoursesSpinner.setSelection(args.getInt(ARGS_SAVED_COURSE_ITEM_POS));
                }
            }
            JsonElement jsonAttendees = new JsonParser().parse(args.getString(ARGS_SAVED_ATTENDEES));
            mAttendees = jsonAttendees.getAsJsonArray();
            if (mAttendeeAdapter != null)
                mAttendeeAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

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
        //Search attendee by id
        final Context c = getActivity();
        String adminToken = AppPreferences.getAdminToken(c);
        //String iacId = AppPreferences.getIacId(c);
        new CoursesService(AppConfig.getUUID(c), adminToken)
                .getNewAttendeeInfo(adminToken, iacId,
                        new CoursesService.ServiceHandler() {
                            @Override
                            public void onSuccess(CoursesService.CoursesResponse response) {
                                if (mAttendees == null)
                                    mAttendees = new JsonArray();
                                JsonElement attendeeInfo = response.json.getAsJsonObject()
                                        .get("info_attendee");
                                if (attendeeInfo != null && !attendeeInfo.isJsonNull()) {
                                    mAttendees.add(attendeeInfo.getAsJsonObject());
                                    if (mAttendeeAdapter != null) {
                                        mAttendeeAdapter.notifyDataSetChanged();
                                        mAttendeeListView.smoothScrollByOffset(mAttendeeAdapter.getCount() - 1);
                                    }
                                } else
                                    ViewUtility.showMessage(c, ViewUtility.MSG_ERROR, "No se encontro registro");
                            }

                            @Override
                            public void onError(CoursesService.CoursesResponse response) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
        //Add attendee to the local list
    }

    /**
     * For Barcode and iac manual inputs
     */
    private View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
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
            /*editTextIacId.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);*/
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Shows barcode reader for id card
     */
    private void showBarcodeReader() {
        IntentIntegrator.forSupportFragment(AttendeesFragment.this).initiateScan();
    }

    private boolean validateForm() {
        return mCoursesSpinner.getSelectedItemId() > 0;
    }

    private void saveAndUpload() {
        final Context c = getActivity();
        if (c != null) {
            String adminToken = AppPreferences.getAdminToken(c);
            String iacId = AppPreferences.getIacId(c);
            int courseId = (int) mCoursesSpinner.getSelectedItemId();
            ArrayList<Integer> attendees = getAttendeeList();
            if (iacId != null && courseId > 0 && attendees != null)
                new CoursesService(AppConfig.getUUID(getActivity()), adminToken)
                        .modifyAttendees(adminToken, iacId, courseId, attendees,
                                new CoursesService.ServiceHandler() {
                                    @Override
                                    public void onSuccess(CoursesService.CoursesResponse response) {
                                        //LogUtil.d(TAG, response.json.toString());
                                        ViewUtility.showMessage(c, ViewUtility.MSG_SUCCESS,
                                                R.string.string_fragment_attendees_modify_save_success);
                                    }

                                    @Override
                                    public void onError(CoursesService.CoursesResponse response) {
                                        //LogUtil.e(TAG, response.msg, null);
                                        ViewUtility.showMessage(c, ViewUtility.MSG_SUCCESS,
                                                R.string.string_fragment_attendees_modify_save_success);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
        }
    }

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
        if (result != null || !TextUtils.isEmpty(result.getContents())) {
            ViewUtility.showMessage(getActivity(), ViewUtility.MSG_SUCCESS, result.getContents());
            addNewAttendee(result.getContents());
        } else {
            if (getActivity() != null)
                ViewUtility.showMessage(getActivity(), ViewUtility.MSG_ERROR,
                        R.string.fragment_forms_error_barcode);
        }
    }

    /**
     * Courses item selected listener. Each courses brings you a set of attendees
     */
    private AdapterView.OnItemSelectedListener courses_selected = new AdapterView.OnItemSelectedListener() {

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
                                mAttendees = response.json.getAsJsonArray();
                                mAttendeeAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(CoursesService.CoursesResponse response) {
                                ViewUtility.showMessage(c, ViewUtility.MSG_ERROR, "Error");
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                //Display hidden things

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
            getView().findViewById(R.id.fragment_attendees_title).setVisibility(visibility);
            getView().findViewById(R.id.fragment_attendees_add_layout).setVisibility(visibility);
            getView().findViewById(R.id.fragment_attendees_attendee_subtitle).setVisibility(visibility);
            getView().findViewById(R.id.fragment_attendees_list).setVisibility(visibility);
        } catch (NullPointerException npe) {
            LogUtil.e(TAG, npe.getMessage(), npe);
        }
    }

    class CoursesAdapter extends BaseAdapter {
        private static final String COURSE_ID = "id";
        private static final String COURSE_NAME = "name";
        private LayoutInflater mInflater;
        private JsonElement mDefaultValue = new JsonObject();

        public CoursesAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDefaultValue.getAsJsonObject().addProperty(COURSE_ID, "0");
            mDefaultValue.getAsJsonObject().addProperty(COURSE_NAME, "Seleccione");
            if (mCourses == null)
                mCourses = new JsonArray();
            if (mCourses.size() == 0)
                mCourses.add(mDefaultValue);
        }

        @Override
        public int getCount() {
            return mCourses == null ? 0 : mCourses.size();
        }

        @Override
        public Object getItem(int i) {
            return mCourses == null ? null : mCourses.get(i);
        }

        @Override
        public long getItemId(int i) {
            try {
                return mCourses == null ? 0L :
                        Long.parseLong(mCourses.get(i).getAsJsonObject().get(COURSE_ID).getAsString());
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
            String name = jsonElement.getAsJsonObject().get(COURSE_NAME).getAsString();
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
            String name = jsonElement.getAsJsonObject().get(COURSE_NAME).getAsString();
            CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.list_item_right_text);
            textView.setText(getItemId(position) != 0 ? name : "");
            return v;
        }
    }

    class AttendeeAdapter extends BaseAdapter implements View.OnClickListener {

        protected final static String ATTENDEE_ID = "id";
        protected final static String ATTENDEE_NAME = "name";
        private LayoutInflater mInflater;

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
            textViewName.setText(attendee.get(ATTENDEE_NAME).getAsString());
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
