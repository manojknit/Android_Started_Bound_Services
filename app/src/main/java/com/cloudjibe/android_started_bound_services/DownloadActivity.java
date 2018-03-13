package com.cloudjibe.android_started_bound_services;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity {
    private MyService serviceBinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.lilli);
    }


    public void onbtnStartDownloadClick(View view) {
        Intent intent = new Intent(getBaseContext(), MyService.class);
        EditText etpdf1=(EditText)findViewById(R.id.etpdf1);
        EditText etpdf2=(EditText)findViewById(R.id.etpdf2);
        EditText etpdf3=(EditText)findViewById(R.id.etpdf3);
        EditText etpdf4=(EditText)findViewById(R.id.etpdf4);
        EditText etpdf5=(EditText)findViewById(R.id.etpdf5);
        try {
            URL[] urls = new URL[] {
                    new URL(etpdf1.getText().toString()),
                    new URL(etpdf2.getText().toString()),
                    new URL(etpdf3.getText().toString()),
                    new URL(etpdf4.getText().toString()),
                    new URL(etpdf5.getText().toString())};
            intent.putExtra("URLs", urls);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            startService(intent);
        } else {
            // Permission is missing and must be requested.
            ActivityCompat.requestPermissions(DownloadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            startService(intent);
        }
    }



    public void onbtnCancelClick(View view) {
        finish();
    }
}
