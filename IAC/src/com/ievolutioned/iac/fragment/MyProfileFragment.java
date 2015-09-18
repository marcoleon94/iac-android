package com.ievolutioned.iac.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.net.service.ProfileService;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MyProfileFragment extends Fragment {


    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;
    private List<Fragment> mFragments = new ArrayList<>(2);

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
        ProfileFragment profileFragment;
        PasswordFragment passwordFragment;

        new ProfileService(AppConfig.getUUID(getActivity()), AppPreferences.getAdminToken(getActivity())).getProfileInfo(
                new ProfileService.ProfileServiceHandler() {
                    @Override
                    public void onSuccess(ProfileService.ProfileResponse response) {
                        LogUtil.d("MYPROFILE", response.msg);
                    }

                    @Override
                    public void onError(ProfileService.ProfileResponse response) {
                        LogUtil.e("MYPROFILE", response.msg, response.e);
                    }

                    @Override
                    public void onCancel() {

                    }
                }
        );

        if (mViewPager != null) {
            profileFragment = new ProfileFragment();
            passwordFragment = new PasswordFragment();
            mFragments.add(profileFragment);
            mFragments.add(passwordFragment);
            ProfilePageAdapter adapter = new ProfilePageAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);
        } else {
            profileFragment = (ProfileFragment) getChildFragmentManager().findFragmentByTag("Profile");
            passwordFragment = (PasswordFragment) getChildFragmentManager().findFragmentByTag("Password");

        }
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
