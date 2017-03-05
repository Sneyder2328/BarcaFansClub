package com.twismart.barcafansclub.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.twismart.barcafansclub.Fragments.FCBFragment;
import com.twismart.barcafansclub.Fragments.MoreOptionsFragment;
import com.twismart.barcafansclub.Fragments.PreviewFragment;
import com.twismart.barcafansclub.Fragments.SocialFragment;
import com.twismart.barcafansclub.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 12/3/2016.
* */

public class MatchDetailedTabsAdapter extends FragmentPagerAdapter {

    private List<Integer> titleFragments = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
            //{R.string.match_detailed_tab_preview, R.string.match_detailed_tab_live, R.string.match_detailed_tab_comments, R.string.match_detailed_tab_lineups, R.string.match_detailed_tab_stats};
    private Context context;

    public MatchDetailedTabsAdapter(FragmentManager fragmentManager, Context context, String link, String featuresOfMatch){
        super(fragmentManager);
        this.context = context;
        if(featuresOfMatch.contains("PreviewAvailable")){
            titleFragments.add(R.string.match_detailed_tab_preview);
            fragments.add(PreviewFragment.newInstance(link));
        }
        if(featuresOfMatch.contains("LiveAvailable")){
            titleFragments.add(R.string.match_detailed_tab_live);
            fragments.add(PreviewFragment.newInstance(link));
        }
        titleFragments.add(R.string.match_detailed_tab_comments);
        fragments.add(PreviewFragment.newInstance(link));
        if(featuresOfMatch.contains("LineupsAvailable")){
            titleFragments.add(R.string.match_detailed_tab_lineups);
            fragments.add(PreviewFragment.newInstance(link));
        }
        if(featuresOfMatch.contains("StatsAvailable")){
            titleFragments.add(R.string.match_detailed_tab_stats);
            fragments.add(PreviewFragment.newInstance(link));
        }
    }

    @Override
    public int getCount() {
        return titleFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(titleFragments.get(position));
    }
}