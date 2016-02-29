package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.LastVersionMobile;
import com.ievolutioned.iac.net.service.UtilService;
import com.ievolutioned.iac.util.AppConfig;

import java.util.Calendar;
import java.util.Locale;

/**
 * About fragment class, displays the about of this app and the option to download a new update if it is necessary
 * Created by Daniel on 26/02/2016.
 */
public class TuobaFragment extends Fragment implements View.OnClickListener {

    /**
     * TextView version
     */
    private TextView mTextVersion;
    /**
     * TextView year information
     */
    private TextView mTextYear;
    /**
     * TextView description
     */
    private TextView mTextDescription;
    /**
     * Button download
     */
    private Button mButtonDownload;
    /**
     * Activity main activity
     */
    private Activity mActivity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tuoba, container, false);
        setTitle("Acerca de");
        bindUI(root);
        bindData();
        return root;
    }

    /**
     * Binds the data to the current controls
     */
    private void bindData() {
        Locale localeDefault = Locale.getDefault();
        String version = String.format(localeDefault, "%s %s",
                getString(R.string.string_fragment_tuoba_version_prefix),
                getString(R.string.app_version));
        mTextVersion.setText(version);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String yearString = String.format(localeDefault, "%s %s",
                String.valueOf(year), getString(R.string.string_fragment_tuoba_iac));

        mTextYear.setText(yearString);

        showDescriptionDownloadVersion();
    }

    /**
     * Show description download version when it is a new version available
     */
    private void showDescriptionDownloadVersion() {
        new UtilService(AppConfig.getUUID(mActivity)).getUpdate(updatable);
    }

    /**
     * IUpdateVersion call back, shows if a new version available
     */
    private UtilService.IUpdateVersion updatable = new UtilService.IUpdateVersion() {
        @Override
        public void onUpdateVersionResult(final LastVersionMobile lastVersionMobile) {
            if (lastVersionMobile.getVersioAndroid() == null ||
                    lastVersionMobile.getVersioAndroid().isEmpty())
                return;

            if (lastVersionMobile.getVersioAndroid().contentEquals(getString(R.string.app_version)))
                return;

            mTextDescription.setVisibility(View.VISIBLE);
            mButtonDownload.setVisibility(View.VISIBLE);
            mTextDescription.setText(lastVersionMobile.getDescriptionAndroid());
            mButtonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Look for url on response
                    if (lastVersionMobile.getUrlAndroid() != null &&
                            !lastVersionMobile.getUrlAndroid().isEmpty())
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(lastVersionMobile.getUrlAndroid())));
                    else {
                        //Get plays and app package name
                        final String appPackageName = mActivity.getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=" +
                                            appPackageName)));
                        }
                    }
                }
            });

        }
    };

    /**
     * Binds the UI
     *
     * @param root - View layout
     */
    private void bindUI(View root) {
        //Find views
        mTextVersion = (TextView) root.findViewById(R.id.fragment_tuoba_version);
        mTextYear = (TextView) root.findViewById(R.id.fragment_tuoba_year);
        mTextDescription = (TextView) root.findViewById(R.id.fragment_tuoba_description);
        mButtonDownload = (Button) root.findViewById(R.id.fragment_tuoba_b_download);

        //OnClick listeners
        mButtonDownload.setOnClickListener(this);
    }

    /**
     * Sets the title
     *
     * @param title
     */
    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            ((MainActivity) activity).setTitle(title);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
