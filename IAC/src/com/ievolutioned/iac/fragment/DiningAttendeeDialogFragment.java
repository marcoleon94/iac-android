package com.ievolutioned.iac.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.Support;
import com.ievolutioned.iac.net.NetUtil;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Dining attendee dialog fragment, allows to define any attendee for dining room
 * <p>
 * Created by Daniel on 11/04/2017.
 */

public class DiningAttendeeDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = DiningAttendeeDialogFragment.class.getName();

    public static final String ARGS_INPUT = "ARGS_INPUT";
    public static final String ARGS_CATEGORY = "ARGS_CATEGORY";
    public static final String ARGS_TYPE = "ARGS_TYPE";
    public static final String ARGS_EMPLOYEE = "ARGS_EMPLOYEE";
    public static final String ARGS_ERROR_CODE = "ARGS_ERROR_CODE";

    public static final int NO_ERROR = 0;
    public static final int ERROR_NOT_FOUND = 1;
    public static final int ERROR_RESTRICTED = 2;


    private EditText mInputText;
    private TextView mErrorMsg;

    private SegmentedGroup mSegmentedCategory;
    private SegmentedGroup mSegmentedType;

    //Radio buttons
    private RadioButton mNormal;
    private RadioButton mNoSupport;
    private RadioButton mExtraTime;
    private RadioButton mFood;
    private RadioButton mBeverage;
    private RadioButton mWater;

    private IDiningManual iDiningManualCallback;


    static DiningAttendeeDialogFragment newInstance(final Bundle args) {
        DiningAttendeeDialogFragment fragment = new DiningAttendeeDialogFragment();
        if (args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Window w = getDialog().getWindow();
        if (w != null) {
            w.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            WindowManager.LayoutParams attrs = w.getAttributes();
            attrs.width = ViewGroup.LayoutParams.MATCH_PARENT;
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            attrs.x = 0;
            w.setAttributes(attrs);
        }
        return bindUI(inflater.inflate(R.layout.dialog_fragment_dining_attendee, container, false));
    }

    private View bindUI(View view) {
        mInputText = (EditText) view.findViewById(R.id.dialog_fragment_dining_attendee_input);
        mErrorMsg = (TextView) view.findViewById(R.id.dialog_fragment_dining_attendee_error);
        mNormal = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_normal);
        mNoSupport = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_no_support);
        mExtraTime = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_extra_time);

        mFood = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_food);
        mBeverage = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_beverage);
        mWater = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_water);

        mSegmentedCategory = (SegmentedGroup) view.findViewById(R.id.dialog_fragment_dining_attendee_category_segmented);
        mSegmentedType = (SegmentedGroup) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_segmented);

        view.findViewById(R.id.dialog_fragment_dining_attendee_accept).setOnClickListener(this);
        view.findViewById(R.id.dialog_fragment_dining_attendee_cancel).setOnClickListener(this);

        return view;
    }

    private int getErrorResourceId(int errorCode) {
        switch (errorCode) {
            case ERROR_NOT_FOUND:
                return R.string.strings_dining_attende_dialog_fragment_error_not_found;
            case ERROR_RESTRICTED:
                return R.string.strings_dining_attende_dialog_fragment_error_restricted;
            default:
                return R.string.strings_dining_attende_dialog_fragment_no_error;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindData();
    }

    private void bindData() {
        //Enable and disable
        Bundle args = getArguments();
        if (args != null) {
            //mInputText.setEnabled(!args.containsKey(ARGS_INPUT));
            mInputText.setText(args.containsKey(ARGS_INPUT) ? args.getString(ARGS_INPUT) : "");

            boolean hasError = args.containsKey(ARGS_ERROR_CODE) && args.getInt(ARGS_ERROR_CODE) != NO_ERROR;
            mErrorMsg.setVisibility(hasError ? View.VISIBLE : View.GONE);
            mErrorMsg.setText(getErrorResourceId(args.getInt(ARGS_ERROR_CODE)));

            mNormal.setEnabled(args.containsKey(ARGS_ERROR_CODE) && args.getInt(ARGS_ERROR_CODE) != ERROR_RESTRICTED);

            final String category = args.containsKey(ARGS_CATEGORY) ? args.getString(ARGS_CATEGORY) : null;
            final String type = args.containsKey(ARGS_TYPE) ? args.getString(ARGS_TYPE) : null;

            if (category != null)
                switch (category) {
                    case Support.Category.NORMAL:
                        if (mNormal.isEnabled())
                            mNormal.setChecked(true);
                        else {
                            mNormal.setChecked(false);
                            mNoSupport.setChecked(true);
                        }
                        break;
                    case Support.Category.EXTRA_TIME:
                        mExtraTime.setChecked(true);
                        break;
                    case Support.Category.NO_SUPPORT:
                        mNoSupport.setChecked(true);
                        break;
                    default:
                        break;
                }
            if (type != null) {
                switch (type) {
                    case Support.Type.FOOD:
                        mFood.setChecked(true);
                        break;
                    case Support.Type.BEVERAGE:
                        mBeverage.setChecked(true);
                        break;
                    case Support.Type.WATER:
                        mWater.setChecked(true);
                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (!NetUtil.hasNetworkConnection(getActivity())) {
            ViewUtility.displayNetworkPreferences(getActivity());
            return;
        }
        switch (view.getId()) {
            case R.id.dialog_fragment_dining_attendee_accept:
                try {
                    if (validate() && this.iDiningManualCallback != null) {
                        String category = getCategory();
                        String type = getType();
                        String input = mInputText.getText().toString();
                        this.iDiningManualCallback.onAccept(input, category, type);
                        dismiss();
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                }
                break;
            case R.id.dialog_fragment_dining_attendee_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private boolean validate() {
        String category = getCategory();
        String type = getType();
        String input = mInputText.getText().toString();
        return category != null && type != null && input.length() > 0;
    }

    private String getCategory() {
        if (mNormal.isChecked())
            return Support.Category.NORMAL;
        if (mNoSupport.isChecked())
            return Support.Category.NO_SUPPORT;
        if (mExtraTime.isChecked())
            return Support.Category.EXTRA_TIME;
        return null;
    }

    private String getType() {
        if (mFood.isChecked())
            return Support.Type.FOOD;
        if (mBeverage.isChecked())
            return Support.Type.BEVERAGE;
        if (mWater.isChecked())
            return Support.Type.WATER;
        return null;
    }

    public void setDiningManualCallback(IDiningManual iDiningManualCallback) {
        this.iDiningManualCallback = iDiningManualCallback;
    }

    interface IDiningManual {
        void onAccept(final String input, final String category, final String type);
    }
}
