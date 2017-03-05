package com.twismart.barcafansclub.Activities;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.PreferencesLogin;

import java.util.HashMap;
import java.util.Map;

public class ReportProblemActivity extends AppCompatActivity {

    private static final String TAG = "ReportProblemActivity";
    private Spinner spinnerTypeProblem;
    private EditText descriptionProblem;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final PreferencesLogin preferencesLogin = new PreferencesLogin(this);

        //
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_problems, R.layout.spinner_item);
        spinnerTypeProblem = (Spinner) findViewById(R.id.spinnerTypeProblem);
        spinnerTypeProblem.setAdapter(adapter);

        //
        descriptionProblem = (EditText) findViewById(R.id.descriptionProblem);

        //
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = descriptionProblem.getText().toString().trim();
                final short typeProblemSelected = (short) spinnerTypeProblem.getSelectedItemPosition();

                if (!description.isEmpty()) {
                    if (typeProblemSelected != 0) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Constants.Problem.DESCRIPTION.value, description);
                        params.put(Constants.Problem.TYPE_PROBLEM.value, String.valueOf(typeProblemSelected));
                        params.put(Constants.User.USER_ID.value, preferencesLogin.getMyId());
                        buttonSubmit.setEnabled(false);
                        reportProblem(Constants.Urls.REPORT_PROBLEM.link, params);
                    } else {
                        Snackbar.make(buttonSubmit, R.string.register_missing_language, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    descriptionProblem.setError(getString(R.string.report_problem_missing_description));
                }
            }
        });
    }


    private void reportProblem(String url, final Map<String, String> params) {//Send the user profile data to the server
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.report_problem_progressbar_reporting_bug));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("false")){//Problem reported correctly
                    progress.cancel();
                    Toast.makeText(getBaseContext(), R.string.report_problem_successfully, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: reportProblem " + error.getMessage());
                buttonSubmit.setEnabled(true);
                progress.cancel();

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