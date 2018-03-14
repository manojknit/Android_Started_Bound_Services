package com.cloudjibe.android_started_bound_services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by mk194903 on 3/13/18.
 */

public class MyBoundService extends Service {
    private String TAG = "TestService";

    private final IBinder myBinder = new MyBinder();

    public MyBoundService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind done");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public class MyBinder extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }


    // Methods used by the binding client components

    /** method for clients */
    public int getRandomNumber() {
        Random mGenerator = new Random();
        return mGenerator.nextInt(100);
    }

    //call from client to download files
    public Long doInBackground(URL... urls) {
        int count = urls.length;
        long totalBytesDownloaded = 0;
        new DoBackgroundDownloadTask().execute(urls);
        return totalBytesDownloaded; //Just for test
    }


    private class DoBackgroundDownloadTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(urls[i]);
                //---calculate precentage downloaded and
                // report its progress---
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("Downloading files",
                    String.valueOf(progress[0]) + "% downloaded");
            Toast.makeText(getBaseContext(),
                    String.valueOf(progress[0]) + "% downloaded",
                    Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(Long result) {
            Toast.makeText(getBaseContext(),
                    "Downloaded " + result + " files with bound service.",
                    Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }
    //called from task
    private int DownloadFile(URL url) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            //Download file
            connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
//			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//				String errormsg =  "Server returned HTTP " + connection.getResponseCode()
//						+ " " + connection.getResponseMessage();
//				Toast.makeText(this, errormsg, Toast.LENGTH_LONG).show();
//			}
//			Log.i("DownloadTask","Response " + connection.getResponseCode());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            String filename = url.getFile();
            filename = URLUtil.guessFileName(filename, null, null);
            File ofile = new File(Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS),filename);
            //output = new FileOutputStream(ofile);
            //output = new FileOutputStream("/sdcard/file_name.extension");
            output = new FileOutputStream(Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS)+filename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
//				if (isCancelled()) {
//					Log.i("DownloadTask","Cancelled");
//					input.close();
//					return null;
//				}
                total += count;
                // publishing the progress....
                //if (fileLength > 0) // only if total length is known
                //	publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return 1;
    }

}
