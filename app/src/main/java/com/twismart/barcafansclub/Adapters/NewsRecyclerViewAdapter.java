package com.twismart.barcafansclub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Pojos.NewFCB;
import com.twismart.barcafansclub.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 12/22/2016.
 **/

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.MyItemNewViewHolder> {

    private static final String TAG = "NewsRecyclerViewAdapter";
    private List<NewFCB> listNews = new ArrayList<>();
    private Context context;

    public NewsRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setListNews(List<NewFCB> listNews) {
        this.listNews = listNews;
        notifyDataSetChanged();
    }

    @Override
    public MyItemNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyItemNewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyItemNewViewHolder holder, int position) {
        holder.bindData(listNews.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(!listNews.isEmpty()){
            return listNews.size();
        }
        else{
            return 0;
        }
    }

    class MyItemNewViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final ImageView imgNew;
        private final TextView title, description;

        MyItemNewViewHolder(View view){
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.titleNew);
            imgNew = (ImageView) view.findViewById(R.id.imgNew);
            description = (TextView) view.findViewById(R.id.descriptionNew);
        }
        private void bindData(final NewFCB newFCB, final int position){
            title.setText(newFCB.getTitle());
            Log.d(TAG, "urlI " + newFCB.getImgUrl());
            if(newFCB.getImgUrl().equals("null")){
                try {
                    newFCB.setImgUrl(newFCB.getDescription().substring(newFCB.getDescription().indexOf("src=")+5, newFCB.getDescription().indexOf("\"/>")));
                    Log.d(TAG, "urlImgGenerated " + newFCB.getImgUrl());
                    listNews.set(position, newFCB);
                } catch (Exception e){
                    Log.e(TAG, "Exception bindData setImgUrl " + e.getMessage());
                    e.printStackTrace();
                }
            }
            String contentDescription = newFCB.getDescription().replaceAll("<p>", "");
            contentDescription = contentDescription.replaceAll("</p>", "");
            contentDescription = contentDescription.replaceAll("&#160;", " ");

            if(newFCB.getDescription().length()>124){
                description.setText(contentDescription.substring(0, 120));
                description.append("...");
            }
            else{
                description.setText(contentDescription);
            }
            if(newFCB.getDrawableImgNew()==null){
                try {
                    Picasso.with(context).load(newFCB.getImgUrl()).into(imgNew, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess getDrawableImgNew");
                            newFCB.setDrawableImgNew(imgNew.getDrawable());
                            listNews.set(position, newFCB);//update this new in the list with the new drawable
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, "onError getDrawableImgNew");
                        }
                    });
                }
                catch (Exception e){
                    Log.e(TAG, "Exception getDrawableImgNew " + e.getMessage());
                }
            }
            else{
                imgNew.setImageDrawable(newFCB.getDrawableImgNew());
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(true){//save in preferences settings that the user can choose between chromecustomtabs o normal navigator
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(Color.rgb(5, 10, 73));
                        builder.setStartAnimations(context, R.anim.derecha_izquierda, R.anim.alpha);
                        builder.setExitAnimations(context, R.anim.appear, R.anim.alpha);
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(context, Uri.parse(newFCB.getLink()));
                    }
                    else{
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(newFCB.getLink()));
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}
