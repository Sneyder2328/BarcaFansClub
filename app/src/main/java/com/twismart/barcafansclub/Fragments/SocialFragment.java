package com.twismart.barcafansclub.Fragments;

import android.content.Context;
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
import com.twismart.barcafansclub.Adapters.PostsRecyclerViewAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.CustomJsonRequest;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocialFragment extends Fragment {

    private static final String TAG = "SocialFragment";
    private OnListFragmentSocialListener mListener;
    private PreferencesLogin preferencesLogin;
    private ArrayList<Post> listPosts;
    private PostsRecyclerViewAdapter myPostRecyclerViewAdapter;
    private boolean loadingMorePosts = true;

    public SocialFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        listPosts = new ArrayList<>();
        listPosts.add(new Post("", "", "", "", "", ""));

        //
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listPosts);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Log.d(TAG, "onScrolled getItemCount " + linearLayoutManager.getItemCount() + " findLastVisibleItemPosition " + linearLayoutManager.findLastVisibleItemPosition());
                if (!loadingMorePosts && linearLayoutManager.getItemCount() <= (linearLayoutManager.findLastVisibleItemPosition() + 3)) {
                    Log.d(TAG, "Load More");
                    loadingMorePosts = true;
                    loadMorePosts();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //
        myPostRecyclerViewAdapter = new PostsRecyclerViewAdapter(getContext(), getActivity(), mListener, true);
        recyclerView.setAdapter(myPostRecyclerViewAdapter);

        //
        preferencesLogin = new PreferencesLogin(getContext());

        //

        loadPosts();
        return view;
    }

    private void loadPosts(){
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
        params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
        params.put(Constants.Post.FROM_DATE.value, "0");

        CustomJsonRequest customJsonRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_POSTS.link, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "response getPosts" + response.toString());
                if(!response.toString().equals("false")){
                    for (int n = 0 ; n < response.length() ; n++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(n);
                            listPosts.add(new Post(jsonObject.getString(Constants.Post.POST_ID.value), jsonObject.getString(Constants.User.USER_ID.value), jsonObject.getString(Constants.Post.DATE.value),
                                    jsonObject.getString(Constants.Post.DATE_TEXT.value), jsonObject.getString(Constants.Post.TEXT.value), jsonObject.getString(Constants.Post.IMG.value)));
                        } catch (Exception e){
                            Log.e(TAG, "Bug onResponse " + e.getMessage());
                        }
                    }
                    myPostRecyclerViewAdapter.setListPosts(listPosts);
                    loadingMorePosts = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: post " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(customJsonRequest);
    }


    private void loadMorePosts(){
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
        params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
        long fromDate = 0;
        for(Post post : myPostRecyclerViewAdapter.getListPosts()){
            try{
                long dateThisPost = Long.parseLong(post.getDate());
                if(fromDate > dateThisPost || fromDate==0){
                    fromDate = dateThisPost;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        params.put(Constants.Post.FROM_DATE.value, String.valueOf(fromDate));
        Log.d(TAG, "loadMorePosts fromDate" + String.valueOf(fromDate));

        CustomJsonRequest customJsonRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_POSTS.link, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "response getPosts" + response.toString());
                if(!response.toString().equals("false")){
                    ArrayList<Post> listNewPosts = new ArrayList<>();
                    for (int n = 0 ; n < response.length() ; n++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(n);
                            listNewPosts.add(new Post(jsonObject.getString(Constants.Post.POST_ID.value), jsonObject.getString(Constants.User.USER_ID.value), jsonObject.getString(Constants.Post.DATE.value),
                                    jsonObject.getString(Constants.Post.DATE_TEXT.value), jsonObject.getString(Constants.Post.TEXT.value), jsonObject.getString(Constants.Post.IMG.value)));
                        } catch (Exception e){
                            Log.e(TAG, "Bug onResponse " + e.getMessage());
                        }
                    }
                    Log.d(TAG, "response listNewPosts size " + listNewPosts.size());
                    myPostRecyclerViewAdapter.addListPosts(listNewPosts);
                    loadingMorePosts = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: post " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(customJsonRequest);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentSocialListener) {
            mListener = (OnListFragmentSocialListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentSocialListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentSocialListener {
        void onPostSelected(Post post, int action);
    }
}
