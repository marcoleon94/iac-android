package com.ievolutioned.iac.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ievolutioned.iac.R;

/**
 * Created by Daniel on 11/04/2017.
 */

public class DiningAttendeeDialogFragment extends DialogFragment {

    public static final String TAG = DiningAttendeeDialogFragment.class.getName();

    public static final String ARGS_INPUT = "ARGS_INPUT";
    public static final String ARGS_TYPE = "ARGS_TYPE";
    public static final String ARGS_SUPPORT = "ARGS_SUPPORT";
    public static final String ARGS_EMPLOYEE = "ARGS_EMPLOYEE";
    public static final String ARGS_IS_FROM_ERROR = "ARGS_IS_FROM_ERROR";


    private EditText mInputText;

    //Radio buttons
    private RadioButton mNormal;
    private RadioButton mNoSupport;
    private RadioButton mExtraTime;
    private RadioButton mFood;
    private RadioButton mBeverage;
    private RadioButton mWater;


    static DiningAttendeeDialogFragment newInstance(final Bundle args) {
        DiningAttendeeDialogFragment fragment = new DiningAttendeeDialogFragment();
        if (args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return bindUI(inflater.inflate(R.layout.dialog_fragment_dining_attendee, container, false));
    }

    private View bindUI(View view) {
        mInputText = (EditText) view.findViewById(R.id.dialog_fragment_dining_attendee_input);
        mNormal = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_normal);
        mNoSupport = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_no_support);
        mExtraTime = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_radio_extra_time);

        mFood = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_food);
        mBeverage = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_beverage);
        mWater = (RadioButton) view.findViewById(R.id.dialog_fragment_dining_attendee_support_type_water);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindData();
    }

    private void bindData() {
        Bundle args = getArguments();
        if (args != null) {
            //Input text
            if (args.containsKey(ARGS_INPUT) && args.getString(ARGS_INPUT, null) != null) {
                String input = args.getString(ARGS_INPUT, null);
                mInputText.setText(input);
            }
            //Type
            if (args.containsKey(ARGS_TYPE) && args.getString(ARGS_TYPE, null) != null) {
                String type = args.getString(ARGS_TYPE, null);
                switch (type) {
                    case DiningFragment.SupportCategory.NORMAL:
                        if (mNormal.isShown()) {
                            mNormal.setChecked(true);
                        }
                        break;
                    case DiningFragment.SupportCategory.NO_SUPPORT:
                        if (mNoSupport.isShown()) {
                            mNoSupport.setChecked(true);
                        }
                        break;
                    case DiningFragment.SupportCategory.EXTRA_TIME:
                        if (mExtraTime.isShown()) {
                            mExtraTime.setChecked(true);
                        }
                        break;
                    default:
                        break;

                }
            }
            //Support
            if (args.containsKey(ARGS_SUPPORT) && args.getString(ARGS_SUPPORT, null) != null) {
                String support = args.getString(ARGS_SUPPORT, null);
                switch (support) {
                    case DiningFragment.SupportType.FOOD:
                        if (mFood.isShown()) {
                            mFood.setChecked(true);
                        }
                        break;
                    case DiningFragment.SupportType.BEVERAGE:
                        if (mBeverage.isShown()) {
                            mBeverage.setChecked(true);
                        }
                        break;
                    case DiningFragment.SupportType.WATER:
                        if (mWater.isShown()) {
                            mWater.setChecked(true);
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }
}
