package com.ievolutioned.iac.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.service.ProfileService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyProfileFragment class, represents the portion of UI for my profile and password controls
 */
public class MyProfileFragment extends Fragment {
    /**
     * TAG
     */
    public final static String TAG = MyProfileFragment.class.getName();
    /**
     * Main ViewPager pager
     */
    private ViewPager mViewPager;
    /**
     * PagerTabStrip tab strip
     */
    private PagerTabStrip mPagerTabStrip;
    /**
     * A set of Fragments
     */
    private List<Fragment> mFragments = new ArrayList<>(2);

    /**
     * ProfileFragment profile fragment
     */
    protected ProfileFragment profileFragment;
    /**
     * PasswordFragment password fragment
     */
    protected PasswordFragment passwordFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_myprofile, container, false);
        setHasOptionsMenu(true);
        bindUI(root);
        bindData(getArguments());
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.fragment_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_fragment_profile_upload)
            uploadProfile();
        return true;
    }

    /**
     * Binds the User interface
     */
    private void bindUI(View root) {
        mViewPager = (ViewPager) root.findViewById(R.id.fragment_profile_pager);
        mPagerTabStrip = (PagerTabStrip) root.findViewById(R.id.fragment_profile_pager_tab_strip);

        if (mViewPager == null) {

        }
    }

    /**
     * Binds the data to the current fragments
     *
     * @param arguments - Bundle of arguments
     */
    private void bindData(Bundle arguments) {
        if (mViewPager != null) {
            profileFragment = new ProfileFragment();
            passwordFragment = new PasswordFragment();
            mFragments.add(0, profileFragment);
            mFragments.add(1, passwordFragment);
            ProfilePageAdapter adapter = new ProfilePageAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);
        } else {
            profileFragment = (ProfileFragment) getChildFragmentManager().findFragmentByTag("Profile");
            passwordFragment = (PasswordFragment) getChildFragmentManager().findFragmentByTag("Password");
        }
        loadMyProfileInfo();
    }

    /**
     * Loads the profile information
     */
    private void loadMyProfileInfo() {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();
        new ProfileService(AppConfig.getUUID(getActivity()), AppPreferences.getAdminToken(getActivity())).getProfileInfo(
                new ProfileService.ProfileServiceHandler() {
                    @Override
                    public void onSuccess(ProfileService.ProfileResponse response) {
                        LogUtil.d(TAG, response.msg);
                        loading.dismiss();
                        setMyProfileInfo(response.profile);
                    }

                    @Override
                    public void onError(ProfileService.ProfileResponse response) {
                        LogUtil.e(TAG, response.msg, response.e);
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Error al cargar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        loading.dismiss();
                    }
                }
        );
    }

    /**
     * Set the info for profile fragment
     *
     * @param profile - ProfileEntity profile
     */
    private void setMyProfileInfo(ProfileEntity profile) {
        if (profileFragment == null)
            return;
        profileFragment.setProfileInfo(profile);
    }

    /**
     * Get prepared for upload profile info
     */
    private void uploadProfile() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map config = new HashMap();
                config.put("cloud_name", "iacgroup");
                config.put("api_key", "855275749257973");
                config.put("api_secret", "xcWDVYFPZ9eeigoVMgrr2AjE3go");
                Cloudinary cloudinary = new Cloudinary(config);
                String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                picturePath += "/Pictures/splash.jpg";
                File f = new File(picturePath);
                if (f.exists())
                    try {
                        Map result = cloudinary.uploader().upload(f, null);
                        if (result != null)
                            LogUtil.d(TAG, result.toString());
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage(), e);
                    }
                return null;
            }
        };
        task.execute();
    }

    public void setImageByIntent(Intent data) {
        try {
            Bitmap bitmap = null;
            if (data.getData() == null) {
                //Must try for thumbnails
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                //Must get input stream for image
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
            if (bitmap != null)
                profileFragment.setProfilePicture(bitmap);
        } catch (Exception ee) {
            LogUtil.e(TAG, ee.getMessage(), ee);
        }
    }

    /**
     * ProfilePageAdapter class that allows the control of the FragmentPagerAdapter adapter of
     * page viewer
     */
    public class ProfilePageAdapter extends FragmentPagerAdapter {

        public ProfilePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position) instanceof ProfileFragment ? "Perfil" : "Contrasena";
        }
    }

}
