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
import com.twismart.barcafansclub.Adapters.NewsRecyclerViewAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.NewFCB;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.HandleXML;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.Util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private PreferencesLogin preferencesLogin;
    private NewsRecyclerViewAdapter newsRecyclerViewAdapter;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        preferencesLogin = new PreferencesLogin(getContext());

        //
        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(getContext());

        //
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.listNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsRecyclerViewAdapter);

        //
        getNews();

        return v;
    }


    private int listsReady = 0, listsNecessary;
    private List<NewFCB> listAllNews;

    private void getNews(){
        listAllNews = new ArrayList<>();
        if(preferencesLogin.getMyLanguage().equals("es") || preferencesLogin.getMyLanguage().equals("ca")){
            listsNecessary = 3;
            HandleXML obj = new HandleXML(getActivity(), Constants.Urls.FEED_ES_SPORTS.link, new INewsFromRSSReadyListener() {
                @Override
                public void onNewsReady(List<NewFCB> listNews) {
                    moreNewsReady(listNews);
                }
                @Override
                public void onError() {
                    listsReady++;
                }
            });
            HandleXML obj2 = new HandleXML(getActivity(), Constants.Urls.FEED_ES_FCBARCELONANOTICIAS.link, new INewsFromRSSReadyListener() {
                @Override
                public void onNewsReady(List<NewFCB> listNews) {
                    moreNewsReady(listNews);
                }
                @Override
                public void onError() {
                    listsReady++;
                }
            });
            HandleXML obj3 = new HandleXML(getActivity(), Constants.Urls.FEED_ES_MUNDODEPORTIVO.link, new INewsFromRSSReadyListener() {
                @Override
                public void onNewsReady(List<NewFCB> listNews) {
                    moreNewsReady(listNews);
                }
                @Override
                public void onError() {
                    listsReady++;
                }
            });
        }
        else {
            listsNecessary = 1;
            HandleXML obj = new HandleXML(getActivity(), Constants.Urls.FEED_EN_SPORTS.link, new INewsFromRSSReadyListener() {
                @Override
                public void onNewsReady(List<NewFCB> listNews) {
                    moreNewsReady(listNews);
                }
                @Override
                public void onError() {
                    listsReady++;
                }
            });
        }
    }

    private void moreNewsReady(List<NewFCB> listNews){
        listsReady++;
        for (NewFCB newFCB : listNews) {
            if(!newFCB.getLink().isEmpty()){
                listAllNews.add(newFCB);
            }
        }
        if(listsReady == listsNecessary){//If are ready all the list of news necessary
            Collections.shuffle(listAllNews);
            Log.d(TAG, "ListNews " + listAllNews.toString());
            listsReady = 0;//Reset listsReady Don't delete this line
            newsRecyclerViewAdapter.setListNews(listAllNews);
        }
    }

    public interface INewsFromRSSReadyListener{
        void onNewsReady(List<NewFCB> listNews);
        void onError();
    }
}
