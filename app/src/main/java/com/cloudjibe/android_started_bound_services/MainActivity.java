package com.cloudjibe.android_started_bound_services;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.lilli);
    }

    public void onbtnCloseClick(View view) {
        finish();
    }

    public void onbtnDownloadClick(View view) {
        Intent intenty = new Intent(getApplicationContext(), DownloadActivity.class);
        startActivity(intenty);
    }
}
