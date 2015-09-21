package com.ievolutioned.iac.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.DownloadImageTask;

/**
 */
public class ProfileFragment extends Fragment {

    private ImageView mImageProfile;
    private TextView mTextEmployeeId;
    private TextView mTextName;
    private EditText mEditEmail;
    private TextView mTextDepartment;
    private TextView mTextDivp;
    private TextView mTextSite;
    private TextView mTextEmployeeType;
    private TextView mTextDateAdmission;
    private TextView mTextHolidays;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        bindUI(root);
        //setHasOptionsMenu(true);
        return root;
    }

    /**
     * Binds the User interface
     */
    private void bindUI(View root) {
        //Find views
        mImageProfile = (ImageView) root.findViewById(R.id.fragment_profile_picture);
        mTextEmployeeId = (TextView) root.findViewById(R.id.fragment_profile_employee_id);
        mTextName = (TextView) root.findViewById(R.id.fragment_profile_name);
        mEditEmail = (EditText) root.findViewById(R.id.fragment_profile_email);
        mTextDepartment = (TextView) root.findViewById(R.id.fragment_profile_department);
        mTextDivp = (TextView) root.findViewById(R.id.fragment_profile_divp);
        mTextSite = (TextView) root.findViewById(R.id.fragment_profile_site);
        mTextEmployeeType = (TextView) root.findViewById(R.id.fragment_profile_employee_type);
        mTextDateAdmission = (TextView) root.findViewById(R.id.fragment_profile_employee_date_admission);
        mTextHolidays = (TextView) root.findViewById(R.id.fragment_profile_employee_date_holidays);
    }

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

    private void loadImageFromURL(String url) {
        DownloadImageTask task = new DownloadImageTask();
        task.downloadImageFromURL(url, new DownloadImageTask.DownloadHandler() {
            @Override
            public void onDownloaded(DownloadImageTask.DownloadImageResponse response) {
                if (mImageProfile != null)
                    mImageProfile.setImageBitmap(response.image);
            }

            @Override
            public void onError(DownloadImageTask.DownloadImageResponse response) {
                Toast.makeText(getActivity(), "No se puede descargar la imagen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
