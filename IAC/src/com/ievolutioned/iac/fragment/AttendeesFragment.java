package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
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

/**
 * Created by Daniel on 21/02/2017.
 */

public class AttendeesFragment extends BaseFragmentClass {

    private static final String TAG = AttendeesFragment.class.getName();
    private Spinner mCoursesSpinner;
    private CoursesAdapter mCoursesSpinnerAdapter;
    private JsonArray mCourses = new JsonArray();

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

}
