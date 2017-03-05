package com.twismart.barcafansclub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Activities.MatchDetailedActivity;
import com.twismart.barcafansclub.Pojos.Match;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 12/25/2016.
 **/

public class MatchesRecyclerViewAdapter extends RecyclerView.Adapter<MatchesRecyclerViewAdapter.MyMatchViewHolder> {

    private List<Match> listMatches = new ArrayList<>();
    private Context context;

    public MatchesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setListMatches(List<Match> listMatches) {
        this.listMatches = listMatches;
        notifyDataSetChanged();
    }

    @Override
    public MyMatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyMatchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_matches_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyMatchViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return listMatches.size();
    }

    class MyMatchViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final TextView date, time, homeTeam, result, awayTeam;
        private final ImageView homeIcon, awayIcon;

        MyMatchViewHolder(View view){
            super(view);
            mView = view;
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            homeTeam = (TextView) view.findViewById(R.id.homeTeam);
            result = (TextView) view.findViewById(R.id.result);
            awayTeam = (TextView) view.findViewById(R.id.awayTeam);
            homeIcon = (ImageView) view.findViewById(R.id.icHomeTeam);
            awayIcon = (ImageView) view.findViewById(R.id.icAwayTeam);
        }

        private void bindData(final int position){
            final Match match = listMatches.get(position);

            date.setText(match.getDate());
            time.setText(match.getTime());
            homeTeam.setText(match.getHomeTeam());
            awayTeam.setText(match.getAwayTeam());
            result.setText(match.getVs());

            Picasso.with(context).load(match.getHomeIcon()).into(homeIcon);
            Picasso.with(context).load(match.getAwayIcon()).into(awayIcon);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MatchDetailedActivity.class);
                    intent.putExtra(Constants.Match.LINK.value, match.getLink());
                    context.startActivity(intent);
                }
            });
        }
    }
}
