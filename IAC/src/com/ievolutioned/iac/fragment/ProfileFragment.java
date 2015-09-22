package com.ievolutioned.iac.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.DownloadImageTask;
import com.ievolutioned.iac.view.ViewUtility;

/**
 * Profile fragment class. Shows the main attributes of my profile
 * and allows the user to change some of them
 */
public class ProfileFragment extends Fragment {

    /**
     * ImageView image of profile
     */
    private ImageView mImageProfile;
    /**
     * ImageButton take picture
     */
    private ImageButton mImageButtonTake;
    /**
     * ImageButton select picture
     */
    private ImageButton mImageButtonSelect;
    /**
     * TextView text control for employee ID
     */
    private TextView mTextEmployeeId;
    /**
     * TextView text control for name
     */
    private TextView mTextName;
    /**
     * TextView text control for email
     */
    private EditText mEditEmail;
    /**
     * TextView text control for department
     */
    private TextView mTextDepartment;
    /**
     * TextView text control for divp
     */
    private TextView mTextDivp;
    /**
     * TextView text control for site
     */
    private TextView mTextSite;
    /**
     * TextView text control for type
     */
    private TextView mTextEmployeeType;
    /**
     * TextView text control for admission date
     */
    private TextView mTextDateAdmission;
    /**
     * TextView text control for holidays
     */
    private TextView mTextHolidays;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        bindUI(root);
        return root;
    }

    /**
     * Binds the User interface
     */
    private void bindUI(View root) {
        //Find views
        mImageProfile = (ImageView) root.findViewById(R.id.fragment_profile_picture);
        mImageButtonTake = (ImageButton) root.findViewById(R.id.fragment_profile_picture_take);
        mImageButtonSelect = (ImageButton) root.findViewById(R.id.fragment_profile_picture_select);
        mTextEmployeeId = (TextView) root.findViewById(R.id.fragment_profile_employee_id);
        mTextName = (TextView) root.findViewById(R.id.fragment_profile_name);
        mEditEmail = (EditText) root.findViewById(R.id.fragment_profile_email);
        mTextDepartment = (TextView) root.findViewById(R.id.fragment_profile_department);
        mTextDivp = (TextView) root.findViewById(R.id.fragment_profile_divp);
        mTextSite = (TextView) root.findViewById(R.id.fragment_profile_site);
        mTextEmployeeType = (TextView) root.findViewById(R.id.fragment_profile_employee_type);
        mTextDateAdmission = (TextView) root.findViewById(R.id.fragment_profile_employee_date_admission);
        mTextHolidays = (TextView) root.findViewById(R.id.fragment_profile_employee_date_holidays);

        //Set on click listeners
        mImageButtonTake.setOnClickListener(button_click);
        mImageButtonSelect.setOnClickListener(button_click);
    }

    private View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_profile_picture_take:
                    break;
                case R.id.fragment_profile_picture_select:
                    selectPicture();
                    break;
                default:
                    break;
            }
        }
    };

    private void selectPicture() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        getActivity().startActivityForResult(i, MainActivity.ACTION_PICK_PHOTO);
    }

    /**
     * Sets the profile information to the controls
     *
     * @param profile
     */
    public void setProfileInfo(ProfileEntity profile) {
        //Load image
        if (profile.getAvatar() != null && profile.getAvatar().getUrl() != null)
            loadImageFromURL(profile.getAvatar().getUrl());
        //Looad info
        if (mTextEmployeeId != null && profile.getIacId() != null)
            mTextEmployeeId.setText(profile.getIacId());
        if (mTextName != null && profile.getName() != null)
            mTextName.setText(profile.getName());
        if (mEditEmail != null && profile.getEmail() != null)
            mEditEmail.setText(profile.getEmail());
        if (mTextDepartment != null && profile.getDepartment() != null)
            mTextDepartment.setText(profile.getDepartment());
        if (mTextDivp != null && profile.getDivp() != null)
            mTextDivp.setText(profile.getDivp());
        if (mTextSite != null && profile.getSiteId() > 0)
            mTextSite.setText(String.valueOf(profile.getSiteId()));
        if (mTextEmployeeType != null && profile.getType() != null)
            mTextEmployeeType.setText(profile.getType());
        if (mTextDateAdmission != null && profile.getDateAdmission() != null)
            mTextDateAdmission.setText(profile.getDateAdmission());
        if (mTextHolidays != null && profile.getHolidays() != null)
            mTextHolidays.setText(profile.getHolidays());
    }

    /**
     * Load an image from the current URL
     *
     * @param url - URL of the web image to be downloaded
     */
    private void loadImageFromURL(String url) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();
        DownloadImageTask task = new DownloadImageTask();
        task.downloadImageFromURL(url, new DownloadImageTask.DownloadHandler() {
            @Override
            public void onDownloaded(DownloadImageTask.DownloadImageResponse response) {
                if (mImageProfile != null)
                    mImageProfile.setImageBitmap(response.image);
                loading.dismiss();
            }

            @Override
            public void onError(DownloadImageTask.DownloadImageResponse response) {
                loading.dismiss();
                Toast.makeText(getActivity(), "No se puede descargar la imagen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                loading.dismiss();
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProfilePicture(Bitmap bitmap) {
        mImageProfile.setImageBitmap(bitmap);
    }
}
