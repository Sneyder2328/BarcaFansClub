package com.twismart.barcafansclub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import com.twismart.barcafansclub.Activities.UserProfileActivity;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Comment;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sneyd on 12/16/2016.
 **/

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final private String TAG = "CommentsRecViewAdapter";
    static final private int ITEM_TYPE_HEADER = 0, ITEM_TYPE_NORMAL = 1;
    private List<Comment> listComments;
    private Post post;
    private PreferencesLogin preferencesLogin;
    private Context context;
    private Activity activity;

    public CommentsRecyclerViewAdapter(Context context, Activity activity, Post post){
        this.context = context;
        this.activity = activity;
        preferencesLogin = new PreferencesLogin(context);
        this.post = post;
    }

    public void setListComments(List<Comment> listComments) {
        this.listComments = listComments;
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
        if(viewType == ITEM_TYPE_NORMAL){
            return new MyItemCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_post_comment, parent, false));
        }
        else{
            return new MyHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_post_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position != 0){
            ((MyItemCommentViewHolder)holder).bindData(listComments.get(position), position);
        }
        else{
            ((MyHeaderViewHolder)holder).bindData();
        }
    }

    @Override
    public int getItemCount() {
        try{
            return listComments.size();
        }
        catch (Exception e){
            return 0;
        }
    }

    private class MyItemCommentViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final LinearLayout linearImgs;
        private final CircleImageView userImg;
        private final ImageView thumbUp, thumbDown;
        private final TextView userName, textComment, dateTime, likes, dislikes;


        private MyItemCommentViewHolder(View view) {
            super(view);
            mView = view;
            dateTime = (TextView) view.findViewById(R.id.dateTime);
            likes = (TextView) view.findViewById(R.id.likes);
            dislikes = (TextView) view.findViewById(R.id.dislikes);
            textComment = (TextView) view.findViewById(R.id.textComment);
            userName = (TextView) view.findViewById(R.id.userName);
            linearImgs = (LinearLayout) view.findViewById(R.id.linearImgs);

            thumbUp = (ImageView) view.findViewById(R.id.thumbUp);
            thumbDown = (ImageView) view.findViewById(R.id.thumbDown);
            userImg = (CircleImageView) view.findViewById(R.id.userImg);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open the profile of the user that did the post
                }
            });
        }

        private void bindData(final Comment comment, final int position){
            textComment.setText(comment.getText());
            userImg.setImageResource(R.drawable.profile);
            userName.setText(comment.getNameUser());
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra(Constants.User.USER_ID.value, post.getUserId());
                    intent.putExtra(Constants.User.NAME.value, post.getNameUser());
                    intent.putExtra(Constants.User.IMG_PROFILE.value, post.getImgUser());
                    intent.putExtra(Constants.User.IMG_COVER.value, post.getImgUserCover());
                    activity.startActivity(intent);
                }
            });
            dateTime.setText(String.format(context.getResources().getString(R.string.format_date), comment.getDate()));
            likes.setText(String.valueOf(comment.getLikes()));
            dislikes.setText(String.valueOf(comment.getDislikes()));

            if(comment.getDrawableImgUser()!=null) {
                userImg.setImageDrawable(comment.getDrawableImgUser());
            }

            if(!comment.getImgUrl().equals("null")){
                linearImgs.setVisibility(View.VISIBLE);

                List<String> listImgs = new ArrayList<>(Arrays.asList(comment.getImgUrl().substring(1, comment.getImgUrl().length() - 1).split("\\s*,\\s*")));
                for (final String img : listImgs){
                    Log.d(TAG, "img " + img);
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    width -= width/40;

                    final ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));

                    new Handler().post(new Runnable() {
                        public void run() {
                            try {
                                final String url = Util.generateURL(MainActivity.s3, img);
                                Picasso.with(context).load(url).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess img");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "onError img");
                                    }
                                });
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    linearImgs.addView(imageView);
                }
            }
            else {
                linearImgs.removeAllViews();
                linearImgs.setVisibility(View.INVISIBLE);
            }

            if(comment.getMyVote().equals(Constants.Thumb.LIKE.value)){
                thumbUp.setColorFilter(Color.BLUE);
                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
            } else if(comment.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                thumbDown.setColorFilter(Color.BLUE);
            }
            else {
                thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
            }

            if(!comment.hasBasicInfo()) {
                final Map<String, String> params = new HashMap<>();
                params.put(Constants.User.USER_ID.value, comment.getUserId());
                params.put(Constants.Comment.COMMENT_ID.value, comment.getCommentId());
                params.put(Constants.MY_USER_ID, preferencesLogin.getMyId());

                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_INFO_COMMENT.link, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonCommentInfo) {
                        Log.d(TAG, "onResponse " + jsonCommentInfo);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonCommentInfo);

                            comment.setLikes(jsonObject.getInt(Constants.Comment.LIKES.value));
                            comment.setDislikes(jsonObject.getInt(Constants.Comment.DISLIKES.value));
                            comment.setBasicInfo(jsonObject.getString(Constants.User.NAME.value), jsonObject.getString(Constants.User.IMG_PROFILE.value));
                            comment.setMyVote(jsonObject.getString(Constants.Comment.MY_VOTE.value));

                            listComments.set(position, comment);//update this post in the list

                            userName.setText(jsonObject.getString(Constants.User.NAME.value));
                            likes.setText(String.valueOf(comment.getLikes()));
                            dislikes.setText(String.valueOf(comment.getDislikes()));

                            if(!comment.getImgUser().equals("null")){
                                final String url = Util.generateURL(MainActivity.s3, comment.getImgUser());
                                new Handler().post(new Runnable() {
                                    public void run() {
                                        try {
                                            final int large = Util.getWidthAndHeightForImgUser(activity);
                                            Picasso.with(context).load(url).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImg, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d(TAG, "onSuccess img");
                                                    comment.setDrawableImgUser(userImg.getDrawable());
                                                    listComments.set(position, comment);//update this post in the list
                                                }

                                                @Override
                                                public void onError() {
                                                    Log.e(TAG, "onError img");
                                                }
                                            });
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                            if(comment.getMyVote().equals(Constants.Thumb.LIKE.value)){
                                thumbUp.setColorFilter(Color.BLUE);
                                thumbDown.setColorFilter(Color.rgb(189, 189, 189));
                            } else if(comment.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
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
                    disableThumbs();

                    if(comment.getMyVote().equals(Constants.Thumb.LIKE.value)){
                        Log.d(TAG, "It will delete the thumbUp");
                        clickRemoveThumb(comment.getCommentId(), thumbUp, "0", position, comment);

                    } else {
                        clickThumb(comment.getCommentId(), comment.getPostId(), comment.getUserId(), "0", position, comment);
                    }
                }
            });

            thumbDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disableThumbs();

                    if(comment.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                        Log.d(TAG, "It will delete the thumbDown");
                        clickRemoveThumb(comment.getCommentId(), thumbDown, "1", position, comment);
                    } else {
                        clickThumb(comment.getCommentId(), comment.getPostId(), comment.getUserId(), "1", position, comment);
                    }
                }
            });
        }


        private void clickThumb(final String commentId, final String postId, final String creatorId, final String typeThumb, final int position, final Comment comment){
            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Thumb.LIKE_ID.value, Util.generateUUID());
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
            params.put(Constants.Comment.COMMENT_ID.value, commentId);
            params.put(Constants.Post.POST_ID.value, postId);
            params.put(Constants.Comment.CREATOR_ID.value, creatorId);
            params.put(Constants.Thumb.TYPE_THUMB.value, typeThumb);

            Log.d(TAG, "clickThumb params comment: " + params.toString());

            StringRequest likeRequest = new StringRequest(Request.Method.POST, Constants.Urls.PUT_THUMB_COMMENT.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "clickThumb onResponse: " + response);
                    if(typeThumb.equals("0")){
                        thumbUp.setColorFilter(Color.BLUE);
                        if(comment.getMyVote().equals(Constants.Thumb.DISLIKE.value)){
                            thumbDown.setColorFilter(Color.rgb(189, 189, 189));
                            dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())-1));
                            comment.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                        }
                        comment.setMyVote(Constants.Thumb.LIKE.value);
                        likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())+1));
                        comment.setLikes(Integer.parseInt(likes.getText().toString()));
                    }
                    else{
                        thumbDown.setColorFilter(Color.BLUE);
                        if(comment.getMyVote().equals(Constants.Thumb.LIKE.value)){
                            thumbUp.setColorFilter(Color.rgb(189, 189, 189));
                            likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())-1));
                            comment.setLikes(Integer.parseInt(likes.getText().toString()));
                        }
                        comment.setMyVote(Constants.Thumb.DISLIKE.value);
                        dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())+1));
                        comment.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                    }
                    listComments.set(position, comment);//update this post in the list with my vote
                    enableThumbs();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "clickThumb onErrorResponse: " + error.getClass());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(likeRequest);
        }


        private void clickRemoveThumb(String commentId, final ImageView thumb, final String typeThumb, final int position, final Comment comment){
            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Thumb.LIKE_ID.value, Util.generateUUID());
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
            params.put(Constants.Comment.COMMENT_ID.value, commentId);
            params.put(Constants.Thumb.TYPE_THUMB.value, typeThumb);

            StringRequest likeRequest = new StringRequest(Request.Method.POST, Constants.Urls.REMOVE_THUMB_COMMENT.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "clickRemoveThumb onResponse: " + response);
                    thumb.setColorFilter(Color.rgb(189, 189, 189));
                    comment.setMyVote(Constants.Thumb.NEITHER.value);
                    if(typeThumb.equals("0")){
                        likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString())-1));
                        comment.setLikes(Integer.parseInt(likes.getText().toString()));
                    }
                    else{
                        dislikes.setText(String.valueOf(Integer.parseInt(dislikes.getText().toString())-1));
                        comment.setDislikes(Integer.parseInt(dislikes.getText().toString()));
                    }
                    listComments.set(position, comment);//update this post in the list with myVote in neither later of delete my vote
                    enableThumbs();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "clickRemoveThumb onErrorResponse: " + error.getClass());
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
        private final TextView userName, text, dateTime, likes, dislikes;
        private final ImageView thumbUp, thumbDown;
        private final CircleImageView userImg;
        private final LinearLayout linearImgs;

        private MyHeaderViewHolder(View view) {
            super(view);
            mView = view;

            userName = (TextView) view.findViewById(R.id.userName);
            text = (TextView) view.findViewById(R.id.text);
            dateTime = (TextView) view.findViewById(R.id.dateTime);
            likes = (TextView) view.findViewById(R.id.likes);
            dislikes = (TextView) view.findViewById(R.id.dislikes);
            thumbUp = (ImageView) view.findViewById(R.id.thumbUp);
            thumbDown = (ImageView) view.findViewById(R.id.thumbDown);
            userImg = (CircleImageView) view.findViewById(R.id.userImg);
            userImg.setImageResource(R.drawable.profile);
            linearImgs = (LinearLayout) view.findViewById(R.id.linearImgs);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open the profile of the user that did the post
                }
            });
        }

        private void bindData(){
            userName.setText(post.getNameUser());
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra(Constants.User.USER_ID.value, post.getUserId());
                    intent.putExtra(Constants.User.NAME.value, post.getNameUser());
                    intent.putExtra(Constants.User.IMG_PROFILE.value, post.getImgUser());
                    intent.putExtra(Constants.User.IMG_COVER.value, post.getImgUserCover());
                    activity.startActivity(intent);
                }
            });
            text.setText(post.getText());
            dateTime.setText(String.format(context.getResources().getString(R.string.format_date), post.getDateText()));
            likes.setText(String.valueOf(post.getLikes()));
            dislikes.setText(String.valueOf(post.getDislikes()));


            //Manage user's profile img
            if(post.getDrawableImgUser()!=null) {
                userImg.setImageDrawable(post.getDrawableImgUser());
            }
            else if(!post.getImgUser().equals("null")){
                new Handler().post(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            int large = Util.getWidthAndHeightForImgUser(activity);
                            large += (large/20);//5% bigger than the imgUsers in the commments
                            Picasso.with(context).load(Util.generateURL(MainActivity.s3, post.getImgUser())).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImg, new Callback() {
                                @Override
                                public void onSuccess() {
                                    post.setDrawableImgUser(userImg.getDrawable());
                                }
                                @Override
                                public void onError() {
                                    Log.d(TAG, "onError getImgUrl Header comments");
                                }
                            });
                        }
                        catch (Exception e){
                            Log.e(TAG, "Exception getImgUser Header comments " + e.getMessage());
                        }
                    }
                });
            }

            //Manage thumbUp or thumbDown
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


            //Manage imgs of the post
            if(!post.getListDrawablesImgsPost().isEmpty()){
                linearImgs.removeAllViews();
                linearImgs.setVisibility(View.VISIBLE);

                for (Drawable drawableImg : post.getListDrawablesImgsPost()){
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    int width = display.getWidth()-20;

                    final ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setImageDrawable(drawableImg);
                    linearImgs.addView(imageView);
                    Log.d(TAG, "addView ");
                }
            }
            else if(!post.getImgUrl().equals("null")){
                linearImgs.removeAllViews();
                linearImgs.setVisibility(View.VISIBLE);

                final List<String> listImgs = new ArrayList<>(Arrays.asList(post.getImgUrl().substring(1, post.getImgUrl().length() - 1).split("\\s*,\\s*")));
                final List<Drawable> listDrawablesImgsPost = new ArrayList<>();
                for (final String img : listImgs){
                    Log.d(TAG, "img " + img);
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    final int width = display.getWidth()-25;

                    final ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String url = Util.generateURL(MainActivity.s3, img);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.with(context).load(url).into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "onSuccess getImgUrl");
                                            listDrawablesImgsPost.add(imageView.getDrawable());
                                            if(img.equals(listImgs.get(listImgs.size()-1))){
                                                Log.d(TAG, "equals");
                                                post.setListDrawablesImgsPost(listDrawablesImgsPost);
                                            }
                                        }
                                        @Override
                                        public void onError() {
                                            Log.d(TAG, "onError getImgUrl");
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                    linearImgs.addView(imageView);
                }
            }
            else {
                linearImgs.removeAllViews();
                linearImgs.setVisibility(View.INVISIBLE);
            }

            thumbUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, preferencesLogin.getMyId() + " clicked thumb up in " + post.getPostId() + " " + preferencesLogin.getMyEmail() + "  " + preferencesLogin.getMyPassword());
                    disableThumbs();

                    if(post.getMyVote().equals(Constants.Thumb.LIKE.value)){
                        Log.d(TAG, "It will delete the thumbUp");
                        clickRemoveThumb(String.valueOf(post.getPostId()), thumbUp, "0", post);

                    } else {
                        clickThumb(post.getPostId(), post.getUserId(), "0", post);
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
                        clickRemoveThumb(String.valueOf(post.getPostId()), thumbDown, "1", post);
                    } else {
                        clickThumb(post.getPostId(), post.getUserId(), "1", post);
                    }
                }
            });
        }


        private void clickThumb(final String postId, final String creatorId, final String typeThumb, final Post post){
            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Thumb.LIKE_ID.value, Util.generateUUID());
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
            params.put(Constants.Post.POST_ID.value, postId);
            params.put(Constants.Post.CREATOR_ID.value, creatorId);
            params.put(Constants.Thumb.TYPE_THUMB.value, typeThumb);

            Log.d(TAG, "clickThumb params: " + params.toString());

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


        private void clickRemoveThumb(String postId, final ImageView thumb, final String method, final Post post){
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
}
