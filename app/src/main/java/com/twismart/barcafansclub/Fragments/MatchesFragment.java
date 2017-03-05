package com.twismart.barcafansclub.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.twismart.barcafansclub.Adapters.MatchesRecyclerViewAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Match;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.Util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MatchesFragment extends Fragment {

    private static final String TAG = "MatchesFragment";

    public MatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        PreferencesLogin preferencesLogin = new PreferencesLogin(getContext());
        final ArrayList<Match> listMatches = new ArrayList<>();

        //
        final RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.listMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //
        final MatchesRecyclerViewAdapter matchesRecyclerViewAdapter = new MatchesRecyclerViewAdapter(getContext());
        recyclerView.setAdapter(matchesRecyclerViewAdapter);

        //
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constants.Urls.GET_MATCHES.link + "?language="+preferencesLogin.getMyLanguage(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int currentPositionMatch = 0;
                Log.d(TAG, "onResponse " + response.toString());
                for (int n = 0 ; n < response.length() ; n++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(n);
                        if(jsonObject.getString(Constants.Match.VS.value).contains("v") && currentPositionMatch==0 && n!= 0){
                            currentPositionMatch = n;
                        }
                        listMatches.add(new Match(jsonObject.getString(Constants.Match.DATE.value), jsonObject.getString(Constants.Match.TIME.value), jsonObject.getString(Constants.Match.HOME_TEAM.value), jsonObject.getString(Constants.Match.AWAY_TEAM.value),
                                jsonObject.getString(Constants.Match.VS.value), jsonObject.getString(Constants.Match.HOME_ICON.value), jsonObject.getString(Constants.Match.AWAY_ICON.value), jsonObject.getString(Constants.Match.LINK.value)));
                    } catch (Exception e){
                        Log.e(TAG, "Bug onResponse " + e.getMessage());
                    }
                }
                matchesRecyclerViewAdapter.setListMatches(listMatches);
                recyclerView.scrollToPosition(currentPositionMatch);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bug: onErrorResponse " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return v;
    }
}
