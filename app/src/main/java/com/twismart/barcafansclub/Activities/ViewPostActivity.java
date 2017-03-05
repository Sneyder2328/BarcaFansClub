package com.twismart.barcafansclub.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.content.ContentItem;
import com.amazonaws.mobile.content.ContentProgressListener;
import com.amazonaws.mobile.content.UserFileManager;
import com.amazonaws.mobile.util.ImageSelectorUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Adapters.CommentsRecyclerViewAdapter;
import com.twismart.barcafansclub.Pojos.Comment;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.CustomJsonRequest;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPostActivity extends AppCompatActivity {

    static private final String TAG = "ViewPostActivity";
    private static final int PICK_PHOTO = 100, TAKE_PHOTO = 101;
    private EditText textNewComment;
    private PreferencesLogin preferencesLogin;
    private Post post;
    private LinearLayout linearImgsToPost;
    private ArrayList<String> listImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        if(getSupportActionBar()!=null) {//show back arrow
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarca3));
        }

        preferencesLogin = new PreferencesLogin(getBaseContext());

        //
        post = getIntent().getExtras().getParcelable(Constants.POST_TO_VIEW_IN_DETAIL);
        if(post==null){
            final StringRequest postDataFromIdRequest = new StringRequest(Request.Method.POST, Constants.Urls.GET_POST_DATA_FROM_ID.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "response postDataFromIdRequest " + response);
                    if(!response.equals("false")){
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            post = new Post();
                            try{
                                post.setUserId(jsonObject.getString(Constants.User.USER_ID.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setPostId(getIntent().getStringExtra(Constants.Post.POST_ID.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setDate(jsonObject.getString(Constants.Post.DATE.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setText(jsonObject.getString(Constants.Post.TEXT.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                post.setImgUrl(jsonObject.getString(Constants.Post.IMG.value));
                            } catch (Exception e){
                                e.printStackTrace();
                                post.setImgUrl("null");
                            }
                            try{
                                post.setMyVote(jsonObject.getString(Constants.Post.MY_VOTE.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setLikes(jsonObject.getInt(Constants.Post.LIKES.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setDislikes(jsonObject.getInt(Constants.Post.DISLIKES.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setNameUser(jsonObject.getString(Constants.Post.NAME_USER.value));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                post.setImgUser(jsonObject.getString(Constants.Post.IMG_USER.value));
                            } catch (Exception e){
                                e.printStackTrace();
                                post.setImgUser("null");
                            }
                            try{
                                post.setImgUserCover(jsonObject.getString(Constants.Post.IMG_USER_COVER.value));
                            } catch (Exception e){
                                e.printStackTrace();
                                post.setImgUserCover("null");
                            }
                            post.setText(jsonObject.getString(Constants.Post.TEXT.value));
                            Log.d(TAG, "POSTDATA " + post.toString());
                            initComponents();
                        }
                        catch (Exception e){
                            Log.e(TAG, "Exception postDataFromIdRequest " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: postDataFromIdRequest " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<>();
                    params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
                    params.put(Constants.Post.POST_ID.value, getIntent().getStringExtra(Constants.Post.POST_ID.value));
                    params.put("myUserId", preferencesLogin.getMyId());
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(postDataFromIdRequest);
        }
        else{
            initComponents();
        }
    }

    private void initComponents(){
        //
        final ArrayList<Comment> listComments = new ArrayList<>();
        listComments.add(new Comment("", "", "", "", "", "null"));

        //RecyclerView with the list of comments
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        //
        final CommentsRecyclerViewAdapter commentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(getBaseContext(), this,  post);
        commentsRecyclerViewAdapter.setListComments(listComments);
        recyclerView.setAdapter(commentsRecyclerViewAdapter);

        //EditText with content of the new comment
        textNewComment = (EditText) findViewById(R.id.textNewComment);

        //LinearLayout in HorizontalScrollView to add imgs tah will be uploaded
        linearImgsToPost = (LinearLayout) findViewById(R.id.linearImgsToPost);

        //
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.Post.POST_ID.value, post.getPostId());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());

        CustomJsonRequest getCommentsRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_COMMENTS.link, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "getCommentsRequest response " + response.toString());
                try {
                    for (int n = 0; n < response.length(); n++) {
                        JSONObject jsonObject = response.getJSONObject(n);
                        Log.d(TAG, "onResponse comments ");

                        listComments.add(new Comment(jsonObject.getString(Constants.User.USER_ID.value), jsonObject.getString(Constants.Comment.COMMENT_ID.value),
                                jsonObject.getString(Constants.Post.POST_ID.value), jsonObject.getString(Constants.Comment.DATE.value), jsonObject.getString(Constants.Comment.TEXT.value),
                                jsonObject.getString(Constants.Comment.IMG.value)));
                    }
                    commentsRecyclerViewAdapter.setListComments(listComments);
                } catch (Exception e) {
                    Log.e(TAG, "catch getComments " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getComments Bug: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(getCommentsRequest);
        //
        ImageButton btnAddImg = (ImageButton) findViewById(R.id.btnAddImg);
        btnAddImg.setOnClickListener(new View.OnClickListener() {//Button to add images to the post
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                builder.setItems(R.array.new_post_options_add_img, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {//Option take photo clicked
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, TAKE_PHOTO);
                            }
                        } else {//Option pick photo clicked
                            if (ContextCompat.checkSelfPermission(ViewPostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ViewPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);//Ask permission to read external storage
                                return;
                            }
                            // We have permission, so show the image selector.
                            final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
                            startActivityForResult(intent, PICK_PHOTO);
                        }
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String permissions[], final int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "well done onRequestpermission");
            final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
            startActivityForResult(intent, PICK_PHOTO);
        } else {
            // Inform the user they won't be able to upload without permission.
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getString(R.string.read_external_storage_permission_failure_title));
            dialogBuilder.setMessage(getString(R.string.read_external_storage_permission_failure));
            dialogBuilder.setNegativeButton(getString(android.R.string.ok), null);
            dialogBuilder.show();
        }
    }

    private File fileImg = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO:
                    Uri selectedImage = data.getData();
                    final String path = ImageSelectorUtils.getFilePathFromUri(this, selectedImage);
                    fileImg = new File(path);
                    break;
                case TAKE_PHOTO:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    fileImg = Util.saveToInternalStorage(this, imageBitmap);
            }
            Log.d(TAG, "path" + fileImg.getAbsolutePath());
            uploadImage(new File(fileImg.getAbsolutePath()));
        }
    }


    public void uploadImage(final File file) {
        AWSMobileClient.defaultMobileClient().createUserFileManager(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, "public/", AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION,
                new UserFileManager.BuilderResultHandler() {
                    @Override
                    public void onComplete(final UserFileManager userFileManager) {
                        userFileManager.uploadContent(file, "currentPath" + file.getName(), new ContentProgressListener() {
                            @Override
                            public void onSuccess(final ContentItem contentItem) {
                                Log.d(TAG, "onSuccess contentItem" + contentItem.getFilePath());

                                final ImageView imageView = new ImageView(getBaseContext());
                                imageView.setAdjustViewBounds(true);
                                imageView.setPadding(0, 0, 5, 0);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT));

                                Picasso.with(getBaseContext()).load(file).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess img");
                                        linearImgsToPost.addView(imageView);
                                    }
                                    @Override
                                    public void onError() {
                                        Log.e(TAG, "onError img");
                                    }
                                });
                                listImages.add(contentItem.getFilePath());
                            }

                            @Override
                            public void onProgressUpdate(final String fileName, final boolean isWaiting, final long bytesCurrent, final long bytesTotal) {
                                Log.d(TAG, "onProgressUpdate fileName" + fileName);
                                Log.d(TAG, "onProgressUpdate isWaiting" + isWaiting);
                                Log.d(TAG, "onProgressUpdate bytesCurrent" + bytesCurrent);
                                Log.d(TAG, "onProgressUpdate bytesTotal" + bytesTotal);
                            }

                            @Override
                            public void onError(final String fileName, final Exception ex) {
                                Log.d(TAG, "onError " + ex.getMessage());
                            }
                        });
                    }
                });
    }

    public void sendComment(View v){
        String textComment = textNewComment.getText().toString().trim();

        final Map<String, String> params = new HashMap<>();
        params.put(Constants.Comment.COMMENT_ID.value, Util.generateUUID());
        params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
        params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
        params.put(Constants.User.USER_ID.value,preferencesLogin.getMyId());
        params.put(Constants.Post.POST_ID.value, post.getPostId());
        params.put(Constants.Post.CREATOR_ID.value, post.getUserId());
        params.put(Constants.Comment.TEXT.value, textComment);
        if(!listImages.isEmpty()){
            params.put(Constants.Post.IMG.value, listImages.toString());
        }
        textNewComment.setEnabled(false);
        v.setEnabled(false);

        Log.d(TAG, "send comment " + params.toString());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.Urls.ADD_NEW_COMMENT.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "sendComment onResponse: " + response);
                if(response.equals("true")){
                    Intent intent = new Intent(getBaseContext(), ViewPostActivity.class);
                    intent.putExtra(Constants.POST_TO_VIEW_IN_DETAIL, post);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "sendComment onErrorResponse " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}