package com.twismart.barcafansclub.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Adapters.CircleImageView;
import com.twismart.barcafansclub.Adapters.PostsRecyclerViewAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Fragments.SocialFragment;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.CustomJsonRequest;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.Util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private final int STATUS_MY_USER = 0, STATUS_MUTED = 1, STATUS_BLOCKED = 2, STATUS_NORMAL = 3;
    private boolean isMuted = false, isBlocked = false;
    private int status = STATUS_NORMAL;
    private PostsRecyclerViewAdapter myPostRecyclerViewAdapter;
    private PreferencesLogin preferencesLogin;
    private Button buttonAction;
    private LinearLayout layoutTextMuted;
    private ArrayList<Post> listPosts;
    private String userName, userShowingId;
    private Menu menu = null;
    private boolean loadingMorePosts = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        preferencesLogin = new PreferencesLogin(getBaseContext());

        userName = getIntent().getStringExtra(Constants.User.NAME.value);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle(userName);
        collapsingToolbarLayout.setContentScrim(getResources().getDrawable(R.drawable.topbarca3));

        //

        final ImageView userImgCover = (ImageView) findViewById(R.id.userImgCover);
        final CircleImageView userImgSmall = (CircleImageView) findViewById(R.id.userImgSmall);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Picasso.with(getBaseContext()).load(Util.generateURL(MainActivity.s3, getIntent().getStringExtra(Constants.User.IMG_PROFILE.value))).resize(400, 400).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImgSmall);
                    Picasso.with(getBaseContext()).load(Util.generateURL(MainActivity.s3, getIntent().getStringExtra(Constants.User.IMG_COVER.value))).into(userImgCover);
                } catch (Exception e) {
                    Log.e(TAG, "Exception e " + e.getMessage());
                }
            }
        });

        //
        boolean hasHeader = false;
        buttonAction = (Button) findViewById(R.id.buttonAction);

        userShowingId = getIntent().getStringExtra(Constants.User.USER_ID.value);

        if (userShowingId.equals(preferencesLogin.getMyId())) {//If this is my profile
            status = STATUS_MY_USER;
            buttonAction.setText(R.string.user_profile_button_edit_profile);
            buttonAction.setTag(R.string.user_profile_button_edit_profile);
            buttonAction.setVisibility(View.VISIBLE);
            hasHeader = true;
        } else {
            status = STATUS_NORMAL;
            inflateMenu();
            manageFollowingUser();
            layoutTextMuted = (LinearLayout) findViewById(R.id.layoutTextMuted);
            layoutTextMuted.findViewById(R.id.textUnmute).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unmuteUser();
                }
            });
            manageMuteUser();
            manageBlockUser();
        }
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonAction.getTag().equals(R.string.user_profile_button_edit_profile)) {//Edit My Profile
                    startActivity(new Intent(UserProfileActivity.this, MyProfileActivity.class));
                    finish();
                } else if (buttonAction.getTag().equals(R.string.user_profile_button_follow)) {//Follow user
                    buttonAction.setEnabled(false);
                    followUser();
                } else if (buttonAction.getTag().equals(R.string.user_profile_button_following)) {//Ask for unfollow user
                    unfollowUser();
                }
            }
        });

        //

        listPosts = new ArrayList<>();
        listPosts.add(new Post("", "", "", "", "", ""));

        //
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listPosts);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
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
        myPostRecyclerViewAdapter = new PostsRecyclerViewAdapter(getBaseContext(), this, new SocialFragment.OnListFragmentSocialListener() {
            @Override
            public void onPostSelected(Post post, int action) {
                if (post.getPostId().equals("")) {
                    startActivity(new Intent(getBaseContext(), NewPostActivity.class));
                } else {
                    Intent intent = new Intent(getBaseContext(), ViewPostActivity.class);
                    intent.putExtra(Constants.POST_TO_VIEW_IN_DETAIL, post);
                    startActivity(intent);
                }
            }
        }, hasHeader);
        recyclerView.setAdapter(myPostRecyclerViewAdapter);

        //
        getPostFromUser();
    }

    private void getPostFromUser(){
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
        params.put(Constants.User.USER_ID.value, getIntent().getStringExtra(Constants.User.USER_ID.value));
        params.put(Constants.Post.FROM_DATE.value, "0");


        CustomJsonRequest getPostsOfThisUserRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_POSTS_BY_USER_ID.link, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                if (!response.toString().equals("false")) {
                    for (int n = 0; n < response.length(); n++) {
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
                Log.e(TAG, "onErrorResponse getPostsOfThisUserRequest " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(getPostsOfThisUserRequest);
    }


    private void loadMorePosts(){
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
        params.put(Constants.User.USER_ID.value, getIntent().getStringExtra(Constants.User.USER_ID.value));
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

        CustomJsonRequest customJsonRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_POSTS_BY_USER_ID.link, params, new Response.Listener<JSONArray>() {
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

    private void manageFollowingUser() {
        StringRequest amIFollowingThatUserRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_AM_I_FOLLOWING_THAT_USER.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse amIFollowingThatUserRequest " + response);
                if (response.equals("true")) {
                    buttonAction.setText(R.string.user_profile_button_following);
                    buttonAction.setTag(R.string.user_profile_button_following);
                    buttonAction.setVisibility(View.VISIBLE);
                } else if (response.equals("false")) {
                    buttonAction.setText(R.string.user_profile_button_follow);
                    buttonAction.setTag(R.string.user_profile_button_follow);
                    buttonAction.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: amIFollowingThatUserRequest " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Follow.FOLLOWER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Follow.FOLLOWED_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(amIFollowingThatUserRequest);
    }


    private void manageMuteUser() {
        StringRequest amIMutingThatUserRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_AM_I_MUTING_THAT_USER.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse amIMutingThatUserRequest " + response);
                if (response.equals("true") && !isBlocked) {
                    status = STATUS_MUTED;
                    isMuted = true;
                    inflateMenu();
                    layoutTextMuted.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: amIMutingThatUserRequest " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Mute.MUTER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Mute.USER_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(amIMutingThatUserRequest);
    }


    private void manageBlockUser() {
        StringRequest amIBlockingThatUserRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_AM_I_BLOCKING_THAT_USER.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse amIBlockingThatUserRequest " + response);
                if (response.equals("true")) {
                    status = STATUS_BLOCKED;
                    isBlocked = true;
                    inflateMenu();
                    layoutTextMuted.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: amIMutingThatUserRequest " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Block.BLOCKER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Mute.USER_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(amIBlockingThatUserRequest);
    }


    private void followUser() {
        StringRequest followRequest = new StringRequest(Request.Method.POST, Constants.Urls.FOLLOW.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse followRequest " + response);
                if (response.equals("true")) {
                    buttonAction.setEnabled(true);
                    buttonAction.setText(R.string.user_profile_button_following);
                    buttonAction.setTag(R.string.user_profile_button_following);
                } else {
                    buttonAction.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: followRequest " + error.getMessage());
                buttonAction.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Follow.FOLLOWER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Follow.FOLLOWED_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(followRequest);
    }

    private void unfollowUser() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setCancelable(false)
                .setTitle(R.string.user_profile_text_unfollow)
                .setMessage(String.format(getString(R.string.user_profile_dialog_stop_following), userName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StringRequest unfollowRequest = new StringRequest(Request.Method.POST, Constants.Urls.UNFOLLOW.link, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse unfollowRequest " + response);
                                if (response.equals("true")) {
                                    buttonAction.setText(R.string.user_profile_button_follow);
                                    buttonAction.setTag(R.string.user_profile_button_follow);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse: unfollowRequest " + error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put(Constants.Follow.FOLLOWER_ID.value, preferencesLogin.getMyId());
                                params.put(Constants.Follow.FOLLOWED_ID.value, userShowingId);
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(unfollowRequest);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialogo.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mute:
                muteUser();
                break;
            case R.id.block:
                blockUser();
                break;
            case R.id.report:
                Intent intent = new Intent();
                intent.setClass(this, ReportUserActivity.class);
                intent.putExtra(Constants.User.NAME.value, userName);
                intent.putExtra(Constants.User.USER_ID.value, userShowingId);
                startActivity(intent);
                break;
            case R.id.unmute:
                unmuteUser();
                break;
            case R.id.unblock:
                unblockUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void blockUser() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setCancelable(false)
                .setTitle(String.format(getString(R.string.user_profile_dialog_title_block_user), userName))
                .setMessage(String.format(getString(R.string.user_profile_dialog_block_user), userName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StringRequest blockRequest = new StringRequest(Request.Method.POST, Constants.Urls.BLOCK.link, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse blockRequest " + response);
                                if (response.equals("true")) {
                                    status = STATUS_BLOCKED;
                                    isBlocked = true;
                                    inflateMenu();
                                    layoutTextMuted.setVisibility(View.GONE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse: blockRequest " + error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put(Constants.Block.BLOCKER_ID.value, preferencesLogin.getMyId());
                                params.put(Constants.Block.USER_ID.value, userShowingId);
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(blockRequest);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialogo.show();
    }


    private void unblockUser() {
        StringRequest unblockRequest = new StringRequest(Request.Method.POST, Constants.Urls.UNBLOCK.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse unblockRequest " + response);
                if (response.equals("true")) {
                    isBlocked = false;
                    if(isMuted){
                        status = STATUS_MUTED;
                        layoutTextMuted.setVisibility(View.VISIBLE);
                    }
                    else{
                        status = STATUS_NORMAL;
                    }
                    inflateMenu();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: unblockRequest " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Block.BLOCKER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Block.USER_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(unblockRequest);
    }


    private void muteUser() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setCancelable(false)
                .setTitle(String.format(getString(R.string.user_profile_dialog_mute_user), userName))
                .setMessage(String.format(getString(R.string.user_profile_text_mute), userName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StringRequest muteRequest = new StringRequest(Request.Method.POST, Constants.Urls.MUTE.link, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse muteRequest " + response);
                                if (response.equals("true")) {
                                    layoutTextMuted.setVisibility(View.VISIBLE);
                                    status = STATUS_MUTED;
                                    isMuted = true;
                                    inflateMenu();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse: muteRequest " + error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put(Constants.Mute.MUTER_ID.value, preferencesLogin.getMyId());
                                params.put(Constants.Mute.USER_ID.value, userShowingId);
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(muteRequest);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialogo.show();
    }


    private void unmuteUser() {
        StringRequest unmuteRequest = new StringRequest(Request.Method.POST, Constants.Urls.UNMUTE.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse unmuteRequest " + response);
                if (response.equals("true")) {
                    layoutTextMuted.setVisibility(View.GONE);
                    status = STATUS_NORMAL;
                    inflateMenu();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: unmuteRequest " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.Mute.MUTER_ID.value, preferencesLogin.getMyId());
                params.put(Constants.Mute.USER_ID.value, userShowingId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(unmuteRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        inflateMenu();
        Log.e(TAG, "onCreateOptionsMenu");
        return true;
    }

    private void inflateMenu(){
        if(menu != null){
            menu.clear();
            switch (status){
                case STATUS_MUTED:
                    getMenuInflater().inflate(R.menu.menu_user_profile_with_unmute, menu);
                    break;
                case STATUS_BLOCKED:
                    getMenuInflater().inflate(R.menu.menu_user_profile_with_blocked, menu);
                    break;
                case STATUS_NORMAL:
                    getMenuInflater().inflate(R.menu.menu_user_profile, menu);
                    break;
            }
        }
        else{
            Log.e(TAG, "inflateMenu menu is null ");
        }
    }
}
