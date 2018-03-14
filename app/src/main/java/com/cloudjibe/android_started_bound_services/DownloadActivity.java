package com.cloudjibe.android_started_bound_services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity {
    private MyBoundService serviceBinder; //For Bound Service
    private boolean isBound = false;  //For Bound Service
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.lilli);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService

        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, boundconnection, Context.BIND_AUTO_CREATE);

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(isBound) {
            unbindService(boundconnection);
            isBound = false;
        }
    }

//Started service
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

    //Bounded service
    public void onbtnBoundDownloadClick(View view) {



        EditText etpdf1=(EditText)findViewById(R.id.etpdf1);
        EditText etpdf2=(EditText)findViewById(R.id.etpdf2);
        EditText etpdf3=(EditText)findViewById(R.id.etpdf3);
        EditText etpdf4=(EditText)findViewById(R.id.etpdf4);
        EditText etpdf5=(EditText)findViewById(R.id.etpdf5);
        try {

            if(!isBound) {
                Intent intent = new Intent(this, MyBoundService.class);
                bindService(intent, boundconnection, Context.BIND_AUTO_CREATE);

            }
            //Test function synchronous
            //int num = serviceBinder.getRandomNumber();
            //Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();

            URL[] urls = new URL[] {
                    new URL(etpdf1.getText().toString()),
                    new URL(etpdf2.getText().toString()),
                    new URL(etpdf3.getText().toString()),
                    new URL(etpdf4.getText().toString()),
                    new URL(etpdf5.getText().toString())};

            //Provide permission if required
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is already available

            } else {
                // Permission is missing and must be requested.
                ActivityCompat.requestPermissions(DownloadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }

            //Call bound method
            long output = serviceBinder.doInBackground(urls);//With AsyncTask. For synchronous processing you can get output
            //Toast.makeText(this, "Bouned files downloaded. Bytes: " + output, Toast.LENGTH_SHORT).show();


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //set connection for Bound Service
    private ServiceConnection boundconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //---called when the connection is made---
            serviceBinder = ((MyBoundService.MyBinder)service).getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName className) {
            //---called when the service disconnects---
            serviceBinder = null;
            isBound = false;
        }
    };

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getBaseContext(), "File downloaded!",
                    Toast.LENGTH_LONG).show();
        }
    };


    public void onbtnCancelClick(View view) {
        finish();
    }
}
