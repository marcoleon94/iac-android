package com.ievolutioned.iac.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.service.ProfileService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MyProfileFragment extends Fragment {

    public final static String TAG = MyProfileFragment.class.getName();
    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;
    private List<Fragment> mFragments = new ArrayList<>(2);

    protected ProfileFragment profileFragment;
    protected PasswordFragment passwordFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_myprofile, container, false);
        //setHasOptionsMenu(true);
        bindUI(root);
        bindData(getArguments());
        return root;
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

    private void setMyProfileInfo(ProfileEntity profile) {
        if(profileFragment == null)
            return;
        //profileFragment.setArguments();
    }

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
