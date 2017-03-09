package com.twismart.barcafansclub.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.twismart.barcafansclub.Adapters.CircleImageView;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.Util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class NewPostActivity extends AppCompatActivity {

    private static final String TAG = "NewPostActivity";
    private static final int PICK_PHOTO = 100, TAKE_PHOTO = 101;
    private EditText editTextTextNewPost;
    private Button addNewPost;
    private CircleImageView myImgProfile;
    private LinearLayout linearImgsToPost;
    private String myUserId;
    private ArrayList<String> listImages = new ArrayList<>();
    private PreferencesLogin preferencesLogin;
    private Post post = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        if(getSupportActionBar()!=null) {//show back arrow
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarca3));
        }

        preferencesLogin = new PreferencesLogin(this);

        myUserId = preferencesLogin.getMyId();

        myImgProfile = (CircleImageView) findViewById(R.id.myImgProfile);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    int large = Util.getWidthAndHeightForImgUser(NewPostActivity.this);
                    large += large/10;
                    Picasso.with(NewPostActivity.this).load(Util.generateURL(MainActivity.s3, preferencesLogin.getMyImgProfile())).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(myImgProfile);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //
        editTextTextNewPost = (EditText) findViewById(R.id.textNewPost);

        //
        linearImgsToPost = (LinearLayout) findViewById(R.id.linearImgsToPost);

        //
        TextView textViewUserName = (TextView) findViewById(R.id.myUserName);
        textViewUserName.setText(preferencesLogin.getMyName());

        //
        ImageButton btnAddImg = (ImageButton) findViewById(R.id.btnAddImg);
        btnAddImg.setOnClickListener(new View.OnClickListener() {//Button to add images to the post
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
                builder.setItems(R.array.new_post_options_add_img, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {//Option take photo clicked
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, TAKE_PHOTO);
                            }
                        } else {//Option pick photo clicked
                            if (ContextCompat.checkSelfPermission(NewPostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);//Ask permission to read external storage
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

        //
        addNewPost = (Button) findViewById(R.id.buttonNewPost);
        try{
            post = getIntent().getExtras().getParcelable(Constants.POST_TO_EDIT);
            if(post != null){
                getSupportActionBar().setTitle(R.string.new_post_label_edit);
                loadPostToEdit();
            }
        } catch (Exception e){
            post = null;
        }
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post==null){
                    newPost();
                }
                else{
                    addNewPost.setText(R.string.new_post_button_save_post_edited);
                    updatePost();
                }
            }
        });
    }


    private void loadPostToEdit(){
        editTextTextNewPost.setText(post.getText());

        listImages = new ArrayList<>(Arrays.asList(post.getImgUrl().substring(1, post.getImgUrl().length() - 1).split("\\s*,\\s*")));
        for (String img : listImages){
            Log.d(TAG, "listImages img " + img);
            final ImageView imageView = new ImageView(getBaseContext());
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0, 0, 5, 0);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.MATCH_PARENT));

            Picasso.with(getBaseContext()).load(Util.generateURL(MainActivity.s3, img)).into(imageView, new Callback() {
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
        }
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, final String permissions[], final int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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


    private void newPost(){//Button add new post clicked
        String text = editTextTextNewPost.getText().toString().trim();
        if(!text.isEmpty() || !listImages.isEmpty()){//Check if are there content to post
            editTextTextNewPost.setEnabled(false);
            addNewPost.setEnabled(false);

            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Post.POST_ID.value, Util.generateUUID());
            params.put(Constants.Post.TEXT.value, text);
            params.put(Constants.User.USER_ID.value, myUserId);
            params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());
            if(!listImages.isEmpty()){
                params.put(Constants.Post.IMG.value, listImages.toString());
            }

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.Urls.NEW_POST.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String jsonUserData) {
                    Log.d(TAG, "onResponse " + jsonUserData);
                    editTextTextNewPost.setEnabled(true);
                    finish();
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
    }


    private void updatePost(){//Button save post edited clicked
        String text = editTextTextNewPost.getText().toString().trim();
        if(!text.isEmpty() || !listImages.isEmpty()){//Check if are there content to post
            editTextTextNewPost.setEnabled(false);
            addNewPost.setEnabled(false);

            final Map<String, String> params = new HashMap<>();
            params.put(Constants.Post.POST_ID.value, post.getPostId());
            params.put(Constants.Post.TEXT.value, text);
            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
            if(!listImages.isEmpty()){
                params.put(Constants.Post.IMG.value, listImages.toString());
                Log.d(TAG, "params toString Update post  " + params.toString());
            }

            StringRequest updatePostRequest = new StringRequest(Request.Method.POST, Constants.Urls.UPDATE_POST.link, new Response.Listener<String>() {
                @Override
                public void onResponse(String jsonUserData) {
                    Log.d(TAG, "onResponse updatePostRequest " + jsonUserData);
                    editTextTextNewPost.setEnabled(true);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse updatePostRequest " + error.getClass());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(updatePostRequest);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}