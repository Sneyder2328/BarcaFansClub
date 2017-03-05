package com.twismart.barcafansclub.Activities;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Adapters.MainActivityTabsAdapter;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Fragments.SocialFragment;
import com.twismart.barcafansclub.Util.Util;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SocialFragment.OnListFragmentSocialListener, AdListener {

    private static final String TAG = "MainActivity";
    public static AmazonS3 s3 = new AmazonS3Client(AWSMobileClient.defaultMobileClient().getIdentityManager().getCredentialsProvider());
    private com.amazon.device.ads.AdLayout amazonAdView;
    private PreferencesLogin preferencesLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.amazon.device.ads.AdRegistration.setAppKey(Constants.AMAZON_AD_KEY);

        setContentView(R.layout.activity_main);

        showBanners();

        //This do that S3 work correctly
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        preferencesLogin = new PreferencesLogin(getBaseContext());
        Util.setLanguageLocale(getResources(), preferencesLogin.getMyLanguage());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.topbarca3);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setBackgroundResource(R.drawable.topbarca2);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainActivityTabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_people_black);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_public_black);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_options_black);
        }
        catch (Exception e){
            Log.e(TAG, "Exception FUCKED in onCreate getTabAt.setIcon");
        }

        checkMySesion();

        FirebaseAnalytics.getInstance(this);
    }

    private void checkMySesion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Urls.CHECK_MY_SESION.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonMyUserData) {
                Log.d(TAG, "jsonMyUserData " + jsonMyUserData);
                if(!jsonMyUserData.equals("false")){
                    preferencesLogin.saveJsonUserData(jsonMyUserData);
                    uploadData();
                }
                else{
                    preferencesLogin.logOut();
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CHECK_MY_SESION " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
                params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void uploadData(){
        try{
            StringRequest setMyTokenIdRequest = new StringRequest(Request.Method.POST, Constants.Urls.SET_MY_TOKEN_ID.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "setMyTokenIdRequest " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: setMyTokenIdRequest " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<>();
                    params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
                    params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
                    params.put(Constants.User.TOKEN_ID.value, FirebaseInstanceId.getInstance().getToken());
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(setMyTokenIdRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showBanners() {
        try {
            amazonAdView = (com.amazon.device.ads.AdLayout) findViewById(R.id.amazonAd);
            amazonAdView.setListener(this);
            amazonAdView.loadAd();
        }
        catch (Exception e) {
            Log.e(TAG, "Exception showBanners " + e.getMessage());
        }
    }


    //listener of ads amazon
    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        Log.d(TAG, "onAdLoaded");
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        Log.e(TAG, "onAdFailedToLoad " + adError.getMessage());
        amazonAdView.destroy();
        amazonAdView.setVisibility(View.GONE);
    }
    @Override
    public void onAdExpanded(Ad ad) {
    }
    @Override
    public void onAdCollapsed(Ad ad) {
    }
    @Override
    public void onAdDismissed(Ad ad) {
    }


    @Override
    public void onPostSelected(Post post, int action) {
        Log.d(TAG, "onPostSelected " + post.getPostId() + " action " + action);
        if(post.getPostId().equals("")){
            startActivity(new Intent(getBaseContext(), NewPostActivity.class));
        }
        else{
            Intent intent = new Intent(getBaseContext(), ViewPostActivity.class);
            intent.putExtra(Constants.POST_TO_VIEW_IN_DETAIL, post);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        try{
            amazonAdView.destroy();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
