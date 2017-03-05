package com.twismart.barcafansclub.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.twismart.barcafansclub.Fragments.FCBFragment;
import com.twismart.barcafansclub.Fragments.MoreOptionsFragment;
import com.twismart.barcafansclub.Fragments.NotificationsFragment;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Fragments.SocialFragment;

/**
 * Created by sneyd on 12/3/2016.
* */

public class MainActivityTabsAdapter extends FragmentPagerAdapter {

    private int[] titleFragments = {R.string.main_tab_social, R.string.main_tab_fcb, R.string.main_tab_notifications, R.string.fcb_tab_profile};

    public MainActivityTabsAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return titleFragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("getItem", "pos = " + position);
        switch (position) {
            case 0:
                return new SocialFragment();
            case 1:
                return new FCBFragment();
            case 2:
                return new NotificationsFragment();
            default:
                return new MoreOptionsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 1){
            return "FCB";
        }
        else{
            return null;
        }
    }
}