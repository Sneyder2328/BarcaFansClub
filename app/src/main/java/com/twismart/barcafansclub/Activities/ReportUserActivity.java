package com.twismart.barcafansclub.Activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;

public class ReportUserActivity extends AppCompatActivity {

    private static final String TAG = "ReportUserActivity";
    private String userName, userShowingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarca3));
        }

        userName = getIntent().getStringExtra(Constants.User.NAME.value);
        userShowingId = getIntent().getStringExtra(Constants.User.USER_ID.value);

        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(String.format(getString(R.string.report_user_textview), userName));

        //
        final ListView listOptions = (ListView) findViewById(R.id.listOptionsReportUser);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.list_options_report_user));
        listOptions.setAdapter(adapter);
        listOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                listOptions.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                Fragment fragment = null;
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                }
                try {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                }
                catch (Exception e){
                    Log.e(TAG, "Exception in FragmentTransaction " + e.getMessage());
                    e.printStackTrace();
                }
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