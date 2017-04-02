package com.twismart.barcafansclub.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;
import com.twismart.barcafansclub.R;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private PreferencesLogin preferencesLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        preferencesLogin = new PreferencesLogin(getBaseContext());

        if(preferencesLogin.isLogged()){
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    public void signUp(View v0){
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void logIn(View v1){
        LayoutInflater li = LayoutInflater.from(this);
        View v = li.inflate(R.layout.activity_welcome_login, null);

        final EditText editTextLoginEmail = (EditText) v.findViewById(R.id.loginEmail);

        final EditText editTextLoginPassword = (EditText) v.findViewById(R.id.loginPassword);

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setView(v);
        dialogo.setCancelable(false)
                .setPositiveButton(R.string.welcome_log_in, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String email = editTextLoginEmail.getText().toString().trim();
                        String password = editTextLoginPassword.getText().toString().trim();

                        if (!email.isEmpty()) {
                            if (!password.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put(Constants.User.EMAIL.value, email);
                                params.put(Constants.User.PASSWORD.value, password);

                                tryToLogin(params);
                            } else {
                                editTextLoginPassword.setError(getString(R.string.welcome_login_missing_password));
                            }
                        } else {
                            editTextLoginEmail.setError(getString(R.string.welcome_login_missing_email));
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        dialogo.show();
    }


    public void tryToLogin(final Map<String, String> params) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.welcome_login_progressbar_logging_in));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.Urls.LOGIN.link, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonUserData) {
                Log.d(TAG, "onResponse " + jsonUserData);
                if(!jsonUserData.equals("false")){
                    preferencesLogin.saveJsonUserData(jsonUserData);
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }else{
                    progress.cancel();
                    Toast.makeText(getBaseContext(), R.string.welcome_login_error, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: tryToLogin " + error.getMessage());
                progress.cancel();
                Toast.makeText(getBaseContext(), R.string.welcome_login_error, Toast.LENGTH_LONG).show();
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