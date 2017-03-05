package com.twismart.barcafansclub.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class TableFragment extends Fragment {

    private static final String TAG = "TableFragment";

    public TableFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_table, container, false);

        final LinearLayout linearClubs = (LinearLayout) v.findViewById(R.id.linearClubs);
        linearClubs.removeAllViews();


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constants.Urls.GET_TABLE.link, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse " + response.toString());
                for (int n = 0 ; n < response.length() ; n++){
                    LinearLayout layoutClub = (LinearLayout) inflater.inflate(R.layout.fragment_table_club, null);
                    if(n%2==0){
                        layoutClub.setBackgroundColor(Color.rgb(223, 245, 255));
                    }
                    try {
                        JSONObject jsonObject = response.getJSONObject(n);

                        TextView ranking = (TextView) layoutClub.findViewById(R.id.ranking);
                        ranking.setText(String.valueOf(n+1));

                        ImageView icon = (ImageView) layoutClub.findViewById(R.id.icon);
                        Picasso.with(getContext()).load(jsonObject.getString(Constants.Rank.ICON.value)).into(icon);

                        TextView team = (TextView) layoutClub.findViewById(R.id.team);
                        team.setText(jsonObject.getString(Constants.Rank.TEAM.value));

                        TextView matches = (TextView) layoutClub.findViewById(R.id.matches);
                        matches.setText(jsonObject.getString(Constants.Rank.MATCHES.value));

                        TextView won = (TextView) layoutClub.findViewById(R.id.won);
                        won.setText(jsonObject.getString(Constants.Rank.WON.value));

                        TextView drawn = (TextView) layoutClub.findViewById(R.id.drawn);
                        drawn.setText(jsonObject.getString(Constants.Rank.DRAWN.value));

                        TextView lost = (TextView) layoutClub.findViewById(R.id.lost);
                        lost.setText(jsonObject.getString(Constants.Rank.LOST.value));

                        TextView gd = (TextView) layoutClub.findViewById(R.id.gd);
                        gd.setText(jsonObject.getString(Constants.Rank.GD.value));

                        TextView pts = (TextView) layoutClub.findViewById(R.id.pts);
                        pts.setText(jsonObject.getString(Constants.Rank.PTS.value));

                        linearClubs.addView(layoutClub);
                    } catch (Exception e){
                        Log.e(TAG, "Bug onResponse " + e.getMessage());
                    }
                }
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
