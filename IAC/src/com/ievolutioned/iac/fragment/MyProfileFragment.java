package com.ievolutioned.iac.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.gson.JsonObject;
import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;
import com.ievolutioned.iac.entity.ProfileEntity;
import com.ievolutioned.iac.net.CloudImageTask;
import com.ievolutioned.iac.net.service.ProfileService;
import com.ievolutioned.iac.net.service.ResponseBase;
import com.ievolutioned.iac.util.AppConfig;
import com.ievolutioned.iac.util.AppPreferences;
import com.ievolutioned.iac.util.ImageFilePath;
import com.ievolutioned.iac.util.LogUtil;
import com.ievolutioned.iac.view.ViewUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        setTitle(getString(R.string.string_fragment_myprofile_title));
        return root;
    }

    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            ((MainActivity) activity).setTitle(title);
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
            //TODO: when it is a tablet
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
        String picturePath = profileFragment.getImagePath();
        //Verify picture edit
        if (picturePath != null) {
            uploadProfilePicture(picturePath);

        } else
            uploadProfile(null);

    }

    /**
     * Upload the data of the profile
     *
     * @param picture
     */
    private void uploadProfile(final String picture) {
        //Get info
        String email = profileFragment.getEmail();
        String password = passwordFragment.getPassword();
        String repassword = passwordFragment.getRepassword();

        JsonObject response = new JsonObject();
        response.addProperty("email", email);
        response.addProperty("password", password);
        response.addProperty("re-password", repassword);
        response.addProperty("picture", picture);
        //TODO: Call service
    }

    /**
     * Uploads a profile picture
     *
     * @param path - Picture path
     */
    private void uploadProfilePicture(final String path) {
        final AlertDialog loading = ViewUtility.getLoadingScreen(getActivity());
        loading.show();
        CloudImageTask cloudImageTask = new CloudImageTask();
        File file = new File(path);
        cloudImageTask.uploadImageFile(file, new CloudImageTask.CloudImageHandler() {
            @Override
            public void onSuccess(ResponseBase response) {
                String urlCloudinary = ((CloudImageTask.UploadImageResponse) response).url;
                if (urlCloudinary != null) {
                    LogUtil.d(TAG, "URL:" + urlCloudinary);
                    loading.dismiss();
                    uploadProfile(urlCloudinary);
                }
            }

            @Override
            public void onError(ResponseBase response) {
                LogUtil.e(TAG, response.e.getMessage(), response.e);
                loading.dismiss();
                Toast.makeText(getActivity(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                loading.dismiss();
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets the image according to the request code and Intent data
     *
     * @param data
     * @param requestCode
     */
    public void setImageByIntent(Intent data, int requestCode) {
        try {
            Bitmap bitmap = null;
            String path = null;
            if (requestCode == MainActivity.ACTION_PICK_PHOTO) {
                //Must get input stream for image
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                path = ImageFilePath.getPath(getActivity(), data.getData());
            } else if (requestCode == MainActivity.ACTION_TAKE_PHOTO) {
                //Must try for thumbnails
                bitmap = (Bitmap) data.getExtras().get("data");
                // get the temporal uri
                Uri tempUri = getImageUri(bitmap);
                // Gets the real path
                path = getRealPathFromURI(tempUri);
            }

            if (bitmap != null)
                profileFragment.setProfilePicture(bitmap);
            if (path != null)
                profileFragment.setImagePath(path);
        } catch (Exception ee) {
            LogUtil.e(TAG, ee.getMessage(), ee);
        }
    }

    /**
     * Gets the image Uri
     *
     * @param image
     * @return
     */
    public Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), image,
                "ProfileTemp", null);
        return Uri.parse(path);
    }

    /**
     * Gets the real path
     *
     * @param uri - Uri uri
     * @return the real path of a uri file
     */
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
            return getItem(position) instanceof ProfileFragment ?
                    getString(R.string.string_fragment_myprofile_profile) :
                    getString(R.string.string_fragment_myprofile_password);
        }
    }

}
