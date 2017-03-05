package com.twismart.barcafansclub.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twismart.barcafansclub.R;

/**
 * Created by sneyd on 2/13/2017.
 **/

public class UninterestedAccountFragment extends Fragment {

    public UninterestedAccountFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uninteresting_account, container, false);

        return v;
    }
}
