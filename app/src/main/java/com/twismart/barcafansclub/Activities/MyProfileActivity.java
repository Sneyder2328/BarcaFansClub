package com.twismart.barcafansclub.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Adapters.CircleImageView;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.Util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileFragment";
    private static final int PICK_PHOTO = 100, TAKE_PHOTO = 101;

    private CircleImageView myImgProfile;
    private ImageView myImgCover;
    private EditText myUserName;
    private Button saveMyProfile;
    private Spinner spinnerLanguages;
    private PreferencesLogin preferencesLogin;
    private String filePathImgCover,  filePathImgProfile;
    private boolean imgProfileClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        preferencesLogin = new PreferencesLogin(getBaseContext());
        filePathImgProfile = preferencesLogin.getMyImgProfile();
        filePathImgCover = preferencesLogin.getMyImgCover();

        if(getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarca3));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.register_languages, R.layout.spinner_item);
        spinnerLanguages = (Spinner) findViewById(R.id.registerLanguages);
        spinnerLanguages.setAdapter(adapter);

        //
        myUserName = (EditText) findViewById(R.id.myUserName);
        myUserName.setText(preferencesLogin.getMyName());

        //
        myImgCover = (ImageView) findViewById(R.id.myImgCover);
        if (!preferencesLogin.getMyImgCover().equals("null")) {
            try {
                Picasso.with(getBaseContext()).load(Util.generateURL(MainActivity.s3, preferencesLogin.getMyImgCover())).into(myImgCover);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        myImgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgProfileClicked = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setItems(R.array.new_post_options_add_img, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {//Option take photo clicked
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getBaseContext().getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, TAKE_PHOTO);
                            }
                        } else {//Option pick photo clicked
                            if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);//Ask permission to read external storage
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
        myImgProfile = (CircleImageView) findViewById(R.id.myImgProfile);
        if (!preferencesLogin.getMyImgProfile().equals("null")) {
            try {
                Picasso.with(getBaseContext()).load(Util.generateURL(MainActivity.s3, preferencesLogin.getMyImgProfile())).resize(150, 150).centerCrop().into(myImgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        myImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgProfileClicked = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setItems(R.array.new_post_options_add_img, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {//Option take photo clicked
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getBaseContext().getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, TAKE_PHOTO);
                            }
                        } else {//Option pick photo clicked
                            if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);//Ask permission to read external storage
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
        saveMyProfile = (Button) findViewById(R.id.saveMyProfile);
        saveMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (spinnerLanguages.getSelectedItemPosition() != 0) {
                    saveMyProfile.setEnabled(false);
                    myUserName.setEnabled(false);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Urls.UDATE_MY_PROFILE.link, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse UDATE_MY_PROFILE " + response);
                            if (response.equals("true")) {
                                Snackbar.make(view, R.string.my_profile_changes_saved, Snackbar.LENGTH_SHORT).show();

                                preferencesLogin.setMyLanguage(Constants.languages[spinnerLanguages.getSelectedItemPosition() - 1]);
                                preferencesLogin.setMyName(myUserName.getText().toString());

                                startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Snackbar.make(view, R.string.app_net_error, Snackbar.LENGTH_SHORT).show();
                            }
                            saveMyProfile.setEnabled(true);
                            myUserName.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error: post " + error.getMessage());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            final Map<String, String> params = new HashMap<>();
                            params.put(Constants.User.LANGUAGE.value, String.valueOf((short) spinnerLanguages.getSelectedItemPosition()));
                            params.put(Constants.User.EMAIL.value, preferencesLogin.getMyEmail());
                            params.put(Constants.User.PASSWORD.value, preferencesLogin.getMyPassword());
                            params.put(Constants.User.NAME.value, myUserName.getText().toString());
                            if (!filePathImgProfile.isEmpty()) {
                                params.put(Constants.User.IMG_PROFILE.value, filePathImgProfile);
                            }
                            if (!filePathImgCover.isEmpty()) {
                                params.put(Constants.User.IMG_COVER.value, filePathImgCover);
                            }
                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(stringRequest);
                }
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
                    final String path = ImageSelectorUtils.getFilePathFromUri(getBaseContext(), selectedImage);
                    fileImg = new File(path);
                    break;
                case TAKE_PHOTO:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    fileImg = Util.saveToInternalStorage(getBaseContext(), imageBitmap);
            }
            Log.d(TAG, "path" + fileImg.getAbsolutePath());
            uploadImage(new File(fileImg.getAbsolutePath()));
        }
    }

    public void uploadImage(final File file) {
        final boolean imgProfileClickedTemp = imgProfileClicked;
        AWSMobileClient.defaultMobileClient().createUserFileManager(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, "public/", AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET_REGION,
            new UserFileManager.BuilderResultHandler() {
                @Override
                public void onComplete(final UserFileManager userFileManager) {
                    userFileManager.uploadContent(file, "currentPath" + file.getName(), new ContentProgressListener() {

                        @Override
                        public void onSuccess(final ContentItem contentItem) {
                            Log.d(TAG, "onSuccess contentItem" + contentItem.getFilePath());
                            if(imgProfileClickedTemp){
                                filePathImgProfile = contentItem.getFilePath();
                                Picasso.with(getBaseContext()).load(file).resize(400, 400).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(myImgProfile);
                            } else{
                                filePathImgCover = contentItem.getFilePath();
                                Picasso.with(getBaseContext()).load(file).into(myImgCover);
                            }
                        }
                        @Override
                        public void onProgressUpdate(final String fileName, final boolean isWaiting, final long bytesCurrent, final long bytesTotal) {
                            Log.d(TAG, "onProgressUpdate fileName" + fileName + " from " + imgProfileClickedTemp);
                            Log.d(TAG, "onProgressUpdate isWaiting" + isWaiting);
                            Log.d(TAG, "onProgressUpdate bytesCurrent" + bytesCurrent);
                            Log.d(TAG, "onProgressUpdate bytesTotal" + bytesTotal);
                        }

                        @Override
                        public void onError(final String fileName, final Exception ex) {
                            Log.e(TAG, "onError " + ex.getMessage());
                        }
                    });
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
