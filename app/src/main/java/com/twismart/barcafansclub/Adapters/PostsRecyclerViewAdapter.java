package com.twismart.barcafansclub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Activities.MainActivity;
import com.twismart.barcafansclub.Activities.NewPostActivity;
import com.twismart.barcafansclub.Activities.UserProfileActivity;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Fragments.SocialFragment;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final private String TAG = "PostRecViewAdapter";
    static final private int ITEM_TYPE_HEADER = 0, ITEM_TYPE_NORMAL = 1;

    private List<Post> listPosts = new ArrayList<>();
    private final SocialFragment.OnListFragmentSocialListener mListener;
    private PreferencesLogin preferencesLogin;
    private Context context;
    private Activity activity;
    private boolean hasHeader;

    public PostsRecyclerViewAdapter(Context context, Activity activity, SocialFragment.OnListFragmentSocialListener listener, boolean hasHeader) {
        this.context = context;
        this.activity = activity;
        this.hasHeader = hasHeader;
        preferencesLogin = new PreferencesLogin(context);
        mListener = listener;
        listPosts.add(new Post("", "", "", "", "", ""));
    }

    public List<Post> getListPosts() {
        return listPosts;
    }

    public void setListPosts(List<Post> listPosts) {
        this.listPosts = listPosts;
        if(!hasHeader){
            listPosts.remove(0);
        }
        notifyDataSetChanged();
    }

    public void addListPosts(List<Post> listNewPosts){
        Log.d(TAG, "listNewPosts size" + listNewPosts.size());
        Log.d(TAG, "listNewPosts listNewPosts toString " + listNewPosts.toString());
        for(int n = 0 ; n < listNewPosts.size() ; n++){
            this.listPosts.add(listNewPosts.get(n));
            Log.d(TAG, "listNewPosts add " + n + " / " + listNewPosts.size());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return ITEM_TYPE_HEADER;
        } else{
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "view type " + viewType);
        if(viewType == ITEM_TYPE_NORMAL || !hasHeader){
            return new MyItemPostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_post, parent, false));
        }
        else{
            return new MyHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(position != 0 || !hasHeader){
            ((MyItemPostViewHolder)holder).bindData(listPosts.get(position), position);
        }
        else{
            ((MyHeaderViewHolder)holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        try{
            return listPosts.size();
        }
        catch (Exception e){
            return 0;
        }
    }

    private class MyItemPostViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView userName, dateTime, text, likes, dislikes;
        private final ImageView thumbUp, thumbDown, moreOptionsItem;
        private final CircleImageView userImg;
        private final LinearLayout linearImgs;

        MyItemPostViewHolder(View view) {
            super(view);
            mView = view;
            userName = (TextView) view.findViewById(R.id.userName);
            dateTime = (TextView) view.findViewById(R.id.dateTime);
            text = (TextView) view.findViewById(R.id.text);
            likes = (TextView) view.findViewById(R.id.likes);
            dislikes = (TextView) view.findViewById(R.id.dislikes);
            userImg = (CircleImageView) view.findViewById(R.id.userImg);
            linearImgs = (LinearLayout) view.findViewById(R.id.linearImgs);
            thumbUp = (ImageView) view.findViewById(R.id.thumbUp);
            thumbDown = (ImageView) view.findViewById(R.id.thumbDown);
            moreOptionsItem = (ImageView) view.findViewById(R.id.moreOptionsItem);
        }

        private void bindData(final Post post, final int position){
            dateTime.setText(String.format(context.getResources().getString(R.string.format_date), post.getDateText()));
            text.setText(post.getText());
            userImg.setImageResource(R.drawable.profile);
            userName.setText(post.getNameUser());
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "CLICK NOW1 " + post.toString());

                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra(Constants.User.USER_ID.value, post.getUserId());
                    intent.putExtra(Constants.User.NAME.value, post.getNameUser());
                    intent.putExtra(Constants.User.IMG_PROFILE.value, post.getImgUser());
                    intent.putExtra(Constants.User.IMG_COVER.value, post.getImgUserCover());
                    activity.startActivity(intent);
                }
            });
            likes.setText(String.valueOf(post.getLikes()));
            dislikes.setText(String.valueOf(post.getDislikes()));

            //Manage thumbUp and thumbDown
            if(post.getMyVote().equals(Constants.Thumb.LIKE.value)){
                thumbUp.setColorFilter(Color.BLUE);
                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
            } else if(post.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                thumbDown.setColorFilter(Color.BLUE);
            } else {
                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
            }

            //Manage imgUser saved in a drawable
            if(post.getDrawableImgUser() != null) {//If already there is an drawableUserImg ready, show it
                userImg.setImageDrawable(post.getDrawableImgUser());
                userImg.setVisibility(View.VISIBLE);
            }
            else if(!post.getImgUser().equals("") && !post.getImgUser().equals("null")){
                loadMyImgUser(post, position);
            }

            //Manage posibles imgs in the post
            if(!post.getListDrawablesImgsPost().isEmpty()){//If already there are a list with drawables of imgs of this post, show the list one to one
                linearImgs.removeAllViews();//remove old imgs
                linearImgs.setVisibility(View.VISIBLE);//do it visible

                for (Drawable drawableImg : post.getListDrawablesImgsPost()){
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    width -= width/40;

                    final ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setImageDrawable(drawableImg);
                    linearImgs.addView(imageView);
                    Log.d(TAG, "addView ");
                }
            }
            else if(!post.getImgUrl().equals("null") && !post.getImgUrl().isEmpty()){ //If this post has urls of images
                linearImgs.removeAllViews();//remove old imgs
                linearImgs.setVisibility(View.VISIBLE);//do it visible

                final List<String> listImgs = new ArrayList<>(Arrays.asList(post.getImgUrl().substring(1, post.getImgUrl().length() - 1).split("\\s*,\\s*")));
                final List<Drawable> listDrawablesImgsPost = new ArrayList<>();
                final int width = activity.getWindowManager().getDefaultDisplay().getWidth()-20;

                for (final String img : listImgs){
                    final ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));

                    final String url = Util.generateURL(MainActivity.s3, img);
                    new Handler().post(new Runnable() {
                        public void run() {
                            try{
                                Picasso.with(context).load(url).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess getImgUrl");
                                        listDrawablesImgsPost.add(imageView.getDrawable());
                                        if(img.equals(listImgs.get(listImgs.size()-1))){
                                            Log.d(TAG, "equals");
                                            post.setListDrawablesImgsPost(listDrawablesImgsPost);
                                            listPosts.set(position, post);//update this post in the list with the new list of Images
                                        }
                                    }
                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "onError getImgUrl");
                                    }
                                });
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    linearImgs.addView(imageView);
                }
            }
            else {//If this post does not have any img
                linearImgs.removeAllViews();//clean linear with imgs
                linearImgs.setVisibility(View.INVISIBLE);//do it invisible
            }

            //If there is no basic info of the post
            if(!post.hasBasicInfo()) {
                final Map<String, String> params = new HashMap<>();
                params.put(Constants.User.USER_ID.value, post.getUserId());
                params.put(Constants.Post.POST_ID.value, post.getPostId());
                params.put(Constants.MY_USER_ID, preferencesLogin.getMyId());

                //Ask postInfo through this new StringRequest
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_INFO_POST.link, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonPostBasicInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonPostBasicInfo);//Access to the info through this jsonObject

                            //Set info in the post
                            post.setLikes(jsonObject.getInt(Constants.Post.LIKES.value));
                            post.setDislikes(jsonObject.getInt(Constants.Post.DISLIKES.value));
                            post.setBasicInfo(jsonObject.getString(Constants.User.NAME.value), jsonObject.getString(Constants.User.IMG_PROFILE.value), jsonObject.getString(Constants.User.IMG_COVER.value));
                            post.setMyVote(jsonObject.getString(Constants.Post.MY_VOTE.value));

                            listPosts.set(position, post);//Update the updated post in the list

                            //Reflect the info in the UI
                            userName.setText(post.getNameUser());
                            likes.setText(String.valueOf(post.getLikes()));
                            dislikes.setText(String.valueOf(post.getDislikes()));

                            if(!post.getImgUser().equals("null")){
                                loadMyImgUser(post, position);
                            }

                            if(post.getMyVote().equals(Constants.Thumb.LIKE.value)){
                                thumbUp.setColorFilter(Color.BLUE);
                                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
                            } else if(post.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                                thumbDown.setColorFilter(Color.BLUE);
                            }
                            else {
                                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Bug in onResponse " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: post " + error.getClass());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            }

            thumbUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, preferencesLogin.getMyId() + " clicked thumb up in " + post.getPostId() + " " + preferencesLogin.getMyEmail() + "  " + preferencesLogin.getMyPassword());
                    disableThumbs();

                    if(post.getMyVote().equals(Constants.Thumb.LIKE.value)){
                        Log.d(TAG, "It will delete the thumbUp");
                        clickRemoveThumb(String.valueOf(post.getPostId()), thumbUp, "0", position, post);

                    } else {
                        clickThumb(post.getPostId(), post.getUserId(), "0", position, post);
                    }
                }
            });

            thumbDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, preferencesLogin.getMyId() + " clicked thumb down in " + post.getPostId() + " " + preferencesLogin.getMyEmail() + "  " + preferencesLogin.getMyPassword());
                    disableThumbs();

                    if(post.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                        Log.d(TAG, "It will delete the thumbDown");
                        clickRemoveThumb(String.valueOf(post.getPostId()), thumbDown, "1", position, post);
                    } else {
                        clickThumb(post.getPostId(), post.getUserId(), "1", position, post);
                    }
                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        Log.d(TAG, "click in " + post.getText());
                        mListener.onPostSelected(post, 0);
                    }
                }
            });

            moreOptionsItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);

                    if(post.getUserId().equals(preferencesLogin.getMyId())){
                        dialogo.setItems(R.array.my_post_more_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0:
                                        Intent intent = new Intent();
                                        intent.setClass(context, NewPostActivity.class);
                                        intent.putExtra(Constants.POST_TO_EDIT, post);
                                        context.startActivity(intent);
                                        break;
                                    case 1:
                                        StringRequest deletePostRequest = new StringRequest(Request.Method.POST, Constants.Urls.DELETE_POST.link, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.d(TAG, "deletePostRequest onResponse: " + response);
                                                if(response.equals("true")){
                                                    listPosts.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d(TAG, "deletePostRequest onErrorResponse: " + error.getClass());
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
                                                params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
                                                params.put(Constants.Post.POST_ID.value, post.getPostId());
                                                return params;
                                            }
                                        };
                                        AppController.getInstance().addToRequestQueue(deletePostRequest);
                                        break;
                                }
                            }
                        });
                        dialogo.show();
                    }
                    else{
                        dialogo.setItems(R.array.post_more_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if(item==0){
                                    //Set up report post and more options
                                }
                            }
                        });
                        dialogo.show();
                    }
                }
            });
        }

        private void loadMyImgUser(final Post post, final int position){
            new Handler().post(new Runnable() {
                public void run() {
                    try {
                        final int large = Util.getWidthAndHeightForImgUser(activity);
                        Picasso.with(context).load(Util.generateURL(MainActivity.s3, post.getImgUser())).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImg, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess img");
                                post.setDrawableImgUser(userImg.getDrawable());
                                userImg.setVisibility(View.VISIBLE);
                                listPosts.set(position, post);//update this post in the list
                            }
                            @Override
                            public void onError() {
                                Log.e(TAG, "onError img");
                            }
                        });
                    }
                    catch (Exception e){
                        Log.e(TAG, "Exception post.getImgUser()" + e.getMessage());
                    }
                }
            });
        }

        private void clickThumb(final String postId, final String creatorId, final String typeThumb, final int position, final Post post){
            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Thumb.LIKE_ID.value, Util.generateUUID());
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
            params.put(Constants.Post.POST_ID.value, postId);
            params.put(Constants.Post.CREATOR_ID.value, creatorId);
            params.put(Constants.Thumb.TYPE_THUMB.value, typeThumb);

            StringRequest likeRequest = new StringRequest(Request.Method.POST, Constants.Urls.PUT_THUMB_POST.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "clickThumb onResponsee: " + response);
                    if(typeThumb.equals("0")){
                        thumbUp.setColorFilter(Color.BLUE);
                        if(post.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                            thumbDown.setColorFilter(Color.rgb(189, 189, 189));
                            dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())-1));
                            post.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                        }
                        post.setMyVote(Constants.Thumb.LIKE.value);
                        likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())+1));
                        post.setLikes(Integer.parseInt(likes.getText().toString()));
                    }
                    else{
                        thumbDown.setColorFilter(Color.BLUE);
                        if(post.getMyVote().equals(Constants.Thumb.LIKE.value)){
                            thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                            likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())-1));
                            post.setLikes(Integer.parseInt(likes.getText().toString()));
                        }
                        post.setMyVote(Constants.Thumb.DISLIKE.value);
                        dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())+1));
                        post.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                    }
                    listPosts.set(position, post);//update this post in the list with my vote
                    enableThumbs();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "clickThumb onErrorResponse: " + error.getClass());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(likeRequest);
        }

        private void clickRemoveThumb(String postId, final ImageView thumb, final String method, final int position, final Post post){
            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Thumb.LIKE_ID.value, Util.generateUUID());
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
            params.put(Constants.Post.POST_ID.value, postId);
            params.put("method", method);

            StringRequest likeRequest = new StringRequest(Request.Method.POST, Constants.Urls.REMOVE_THUMB_POST.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "clickRemoveThumb onResponse: " + response);
                    thumb.setColorFilter(Color.rgb(189, 189, 189));
                    post.setMyVote(Constants.Thumb.NEITHER.value);
                    if(method.equals("0")){
                        likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())-1));
                        post.setLikes(Integer.parseInt(likes.getText().toString()));
                    }
                    else{
                        dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())-1));
                        post.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                    }
                    listPosts.set(position, post);//update this post in the list with myVote in neither later of delete my vote
                    enableThumbs();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "clickRemoveThumb onErrorResponse: " + error.getClass());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(likeRequest);
        }

        private void enableThumbs(){
            thumbUp.setEnabled(true);
            thumbDown.setEnabled(true);
        }

        private void disableThumbs(){
            thumbUp.setEnabled(false);
            thumbDown.setEnabled(false);
        }
    }


    private class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final CircleImageView userImg;
        private Drawable drawableImgMyUser = null;

        private MyHeaderViewHolder(View view) {
            super(view);
            mView = view;
            userImg = (CircleImageView) view.findViewById(R.id.userImg);
            userImg.setImageResource(R.drawable.profile);

            loadMyImgUser();

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onPostSelected(new Post("", "", "", "", "", ""), 0);
                    }
                }
            });
        }

        private void loadMyImgUser(){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try{
                        final int large = Util.getWidthAndHeightForImgUser(activity);
                        Picasso.with(context).load(Util.generateURL(MainActivity.s3, preferencesLogin.getMyImgProfile())).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImg, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "MyHeaderViewHolder onSuccess img");
                                drawableImgMyUser = userImg.getDrawable();
                            }
                            @Override
                            public void onError() {
                                Log.e(TAG, "MyHeaderViewHolder onError img");
                            }
                        });
                    }
                    catch (Exception e){
                        Log.e(TAG, "Exception in loadMyImgUser header " + e.getMessage());
                    }
                }
            });
        }

        private void bind(){
            if(drawableImgMyUser!=null){
                userImg.setImageDrawable(drawableImgMyUser);
            }
            else{
                loadMyImgUser();
            }
        }
    }
}
