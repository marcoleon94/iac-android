package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.service.CoursesService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;

import java.util.Locale;

/**
 * Created by Daniel on 21/02/2017.
 */

public class AttendeesFragment extends BaseFragmentClass {

    private static final String TAG = AttendeesFragment.class.getName();
    private Spinner mCoursesSpinner;
    private CoursesAdapter mCoursesSpinnerAdapter;
    private JsonArray mCourses = new JsonArray();

    private ListView mAttendeeListView;
    private AttendeeAdapter mAttendeeAdapter;
    private JsonArray mAttendees = new JsonArray();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_attendees, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        setTitle(getString(R.string.string_fragment_attendees_title));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindData(getArguments());
    }

    private void bindUI(View root) {
        if (root == null)
            return;
        mCoursesSpinner = (Spinner) root.findViewById(R.id.fragment_attendees_courses_spinner);

        if (mCoursesSpinner != null) {
            mCoursesSpinnerAdapter = new CoursesAdapter(getActivity());
            mCoursesSpinner.setAdapter(mCoursesSpinnerAdapter);
            mCoursesSpinner.setGravity(Gravity.END);
            mCoursesSpinner.setOnItemSelectedListener(courses_selected);
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
                                //TODO: default value in resourses
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
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(c, "No se encontro registro", Toast.LENGTH_SHORT).show();
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

    }


    private AdapterView.OnItemSelectedListener courses_selected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long itemId) {
            if (itemId > 0) {
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
                                Toast.makeText(c, "No se puede cargar asistentes al curso", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                //Display hidden things

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

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
