package com.twismart.barcafansclub.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PreviewFragment extends Fragment {

    private static final String TAG = "PreviewFragment";
    private static final String ARG_LINK = "link";

    private String link;


    public PreviewFragment() {
        // Required empty public constructor
    }


    public static PreviewFragment newInstance(String param1) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(ARG_LINK);
        }
    }

    private TextView date, time, homeTeam, awayTeam, homeScore, awayScore, separator, competition;
    private ImageView homeIcon, awayIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preview, container, false);

        homeTeam = (TextView) v.findViewById(R.id.homeTeam);
        awayTeam = (TextView) v.findViewById(R.id.awayTeam);
        separator = (TextView) v.findViewById(R.id.separator);
        homeScore = (TextView) v.findViewById(R.id.homeScore);
        awayScore = (TextView) v.findViewById(R.id.awayScore);
        competition = (TextView) v.findViewById(R.id.competition);
        homeIcon = (ImageView) v.findViewById(R.id.icHomeTeam);
        awayIcon = (ImageView) v.findViewById(R.id.icAwayTeam);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_PREVIEW_MATCH.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String getInfoMatchReponse) {
                Log.d(TAG, "getInfoMatchReponse " + getInfoMatchReponse);
                if(!getInfoMatchReponse.equals("false")){
                    try {
                        JSONObject jsonObject = new JSONObject(getInfoMatchReponse);
                        Picasso.with(getContext()).load(jsonObject.getString(Constants.MatchPreview.HOME_ICON.value)).into(homeIcon);
                        Picasso.with(getContext()).load(jsonObject.getString(Constants.MatchPreview.AWAY_ICON.value)).into(awayIcon);
                        homeTeam.setText(jsonObject.getString(Constants.MatchPreview.HOME_TEAM.value));
                        awayTeam.setText(jsonObject.getString(Constants.MatchPreview.AWAY_TEAM.value));
                        homeScore.setText(jsonObject.getString(Constants.MatchPreview.HOME_SCORE.value));
                        awayScore.setText(jsonObject.getString(Constants.MatchPreview.AWAY_SCORE.value));
                        separator.setText(jsonObject.getString(Constants.MatchPreview.SEPARATOR.value));
                        competition.setText(jsonObject.getString(Constants.MatchPreview.COMPETITION.value));
                    } catch (Exception e){
                        Log.e(TAG, "Bug onResponse " + e.getMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: getInfoMatchReponse " + error.getMessage());
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

        return v;
    }
}
