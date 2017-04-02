package com.twismart.barcafansclub.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int PICK_PHOTO = 100, TAKE_PHOTO = 101;
    private Spinner spinnerLanguages;
    private Button buttonSignUp;
    private EditText editTextName, editTextEmail, editTextPassword;
    private PreferencesLogin preferencesLogin;
    private CircleImageView myImgProfile;
    private ImageView myImgProfileIcCamera;
    private String filePathImgProfile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarca3));
        }

        preferencesLogin = new PreferencesLogin(getBaseContext());

        init();
    }

    public void init() {
        TextView text = (TextView) findViewById(R.id.textPolicy);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        //
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        //
        editTextName = (EditText) findViewById(R.id.registerName);

        //
        editTextEmail = (EditText) findViewById(R.id.registerEmail);

        //
        editTextPassword = (EditText) findViewById(R.id.registerPassword);

        //
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.register_languages, R.layout.spinner_item);
        spinnerLanguages = (Spinner) findViewById(R.id.registerLanguages);
        spinnerLanguages.setAdapter(adapter);

        //
        myImgProfileIcCamera = (ImageView) findViewById(R.id.myImgProfileCamara);

        //
        myImgProfile = (CircleImageView) findViewById(R.id.myImgProfile);
        myImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);//Ask permission to read external storage
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
                    final String path = ImageSelectorUtils.getFilePathFromUri(getBaseContext(), selectedImage);
                    fileImg = new File(path);
                    break;
                case TAKE_PHOTO:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    fileImg = Util.saveToInternalStorage(getBaseContext(), imageBitmap);
            }
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
                            Log.d(TAG, "onSuccess img uploaded correctly " + contentItem.getFilePath());
                            filePathImgProfile = contentItem.getFilePath();
                            Picasso.with(getBaseContext()).load(file).resize(400, 400).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(myImgProfile);
                            myImgProfileIcCamera.setVisibility(View.GONE);//Quit camera icon
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
                            Log.e(TAG, "onError " + ex.getMessage());
                        }
                    });
                }
            });
    }


    public void signUp() {//Listener of click button sign up
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        final short languageSelected = (short) spinnerLanguages.getSelectedItemPosition();

        if (!name.isEmpty()) {
            if (!email.isEmpty()) {
                if (!password.isEmpty()) {
                    if (languageSelected != 0) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Constants.User.USER_ID.value, UUID.randomUUID().toString().replaceAll("-", ""));
                        params.put(Constants.User.NAME.value, name);
                        params.put(Constants.User.EMAIL.value, email);
                        params.put(Constants.User.PASSWORD.value, password);
                        params.put(Constants.User.LANGUAGE.value, String.valueOf(languageSelected));
                        if(!filePathImgProfile.isEmpty()){
                            params.put(Constants.User.IMG_PROFILE.value, filePathImgProfile);
                        }
                        buttonSignUp.setEnabled(false);
                        registerUserInServer(Constants.Urls.SIGN_UP.link, params);
                    } else {
                        Snackbar.make(buttonSignUp, R.string.register_missing_language, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    editTextPassword.setError(getString(R.string.register_missing_password));
                }
            } else {
                editTextEmail.setError(getString(R.string.register_missing_email));
            }
        } else {
            editTextName.setError(getString(R.string.register_missing_name));
        }
    }


    private void registerUserInServer(String url, final Map<String, String> params) {//Send the user profile data to the server
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.register_progressbar_creating_profile));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonUserData) {
                Log.d(TAG, "onResponseSignUp " + jsonUserData);
                if(!jsonUserData.equals("false")){//Sign up correctly
                    preferencesLogin.saveJsonUserData(jsonUserData);
                    progress.cancel();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: post " + error.getMessage());
                progress.cancel();
                buttonSignUp.setEnabled(true);
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