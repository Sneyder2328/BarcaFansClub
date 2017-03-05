package com.twismart.barcafansclub.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Adapters.MainActivityTabsAdapter;
import com.twismart.barcafansclub.Adapters.MatchDetailedTabsAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MatchDetailedActivity extends AppCompatActivity {

    private static final String TAG = "MatchDetailedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setBackgroundResource(R.drawable.topbarca2);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        final String link = getIntent().getStringExtra(Constants.Match.LINK.value);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_FEATURES_OF_MATCH.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response " + response);
                viewPager.setAdapter(new MatchDetailedTabsAdapter(MatchDetailedActivity.this.getSupportFragmentManager(), MatchDetailedActivity.this, link, response));
                tabLayout.setupWithViewPager(viewPager);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put(Constants.Match.LINK.value, link);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}