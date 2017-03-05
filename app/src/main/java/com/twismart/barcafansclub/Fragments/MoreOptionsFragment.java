package com.twismart.barcafansclub.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.twismart.barcafansclub.Activities.ReportProblemActivity;
import com.twismart.barcafansclub.Activities.UserProfileActivity;
import com.twismart.barcafansclub.Activities.WelcomeActivity;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;

public class MoreOptionsFragment extends Fragment {

    private static final String TAG = "MoreOptionsFragment";

    public MoreOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more_options, container, false);

        final PreferencesLogin preferencesLogin = new PreferencesLogin(getContext());

        //
        ListView listOptions = (ListView) v.findViewById(R.id.listOptions);
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.more_options_list));
        listOptions.setAdapter(adapter);
        listOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra(Constants.User.USER_ID.value, preferencesLogin.getMyId());
                        intent.putExtra(Constants.User.NAME.value, preferencesLogin.getMyName());
                        intent.putExtra(Constants.User.IMG_PROFILE.value, preferencesLogin.getMyImgProfile());
                        intent.putExtra(Constants.User.IMG_COVER.value, preferencesLogin.getMyImgCover());
                        getActivity().startActivity(intent);
                        break;
                    case 1:
                        openBrowser(Constants.Urls.PRIVACY_POLICY.link);
                        break;
                    case 2:
                        openBrowser(Constants.Urls.TERMS_OF_USE.link);
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), ReportProblemActivity.class));
                        break;
                    case 4:
                        preferencesLogin.logOut();
                        startActivity(new Intent(getActivity(), WelcomeActivity.class));
                        getActivity().finish();
                        break;
                }
            }
        });
        //
        return v;
    }

    private void openBrowser(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(Color.rgb(5, 10, 73));
        builder.setStartAnimations(getContext(), R.anim.derecha_izquierda, R.anim.alpha);
        builder.setExitAnimations(getContext(), R.anim.appear, R.anim.alpha);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }
}
