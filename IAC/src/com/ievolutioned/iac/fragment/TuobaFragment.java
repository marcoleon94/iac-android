package com.ievolutioned.iac.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ievolutioned.iac.MainActivity;
import com.ievolutioned.iac.R;

/**
 * Created by Daniel on 26/02/2016.
 */
public class TuobaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tuoba, container, false);
        setTitle("Acerca de");
        return root;
    }

    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            ((MainActivity) activity).setTitle(title);
    }
}
