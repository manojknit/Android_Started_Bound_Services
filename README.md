# Android app to demonstrate service behavior of android
####                                                                                                     By Manoj Kumar
## Introduction 
Android app to demonstrate service behavior of android. This app download files using service. 
#### Started vs Bounded:
Started services cannot return results/values or interact with its starting component. Bound services on the other hand can send data to the launching component (client). So for example a bound service might be playing an audio file and sending data regarding audio start/pause/stop and the time elapsed to the launching Activity component so that the UI can be updated accordingly.
When the last bound client unbinds, the runtime terminates the service. On the other side Started service completes the execusion.

## How to Run
1.	Prerequisite: Android Studio, some Java knowledge
2.	Download or clone project code and open in Android studio.
3.	Run in Nexus 5X API 26 emulator.


## Technologies Used
1.	Java.
2.	Android Studio


## Application Code and Screenshots 

This app is to download files using service. It will demonstrate permissions too.
Main Activity
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-03-12 23-55-21.png">
Download Activity
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-03-12 23-55-44.png">
#### Started Service
Toast message progress update 40%
<img src="images/Android Emulator - Nexus_5X_API_265554 40.png">
Toast message progress update 100%
<img src="images/Android Emulator - Nexus_5X_API_265554 100.png">
Toast message completion 100%
<img src="images/Android Emulator - Nexus_5X_API_265554 Downloaded Started.png">
#### Bound Service
Toast message completion 100%. I am not adding progress screenshots as those are similar.
<img src="images/Android Emulator - Nexus_5X_API_265554 Downloaded Bounded.png">

## Code: MyService.java
```
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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

//Started service
public class MyService extends Service {
	int counter = 0;
	public URL[] urls;
	
	static final int UPDATE_INTERVAL = 1000;
	private Timer timer = new Timer();

	private final IBinder binder = new MyBinder();
	
	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		//return null;
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly    
		// stopped, so return sticky.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        
        /*
        try {
			int result = DownloadFile(new URL("http://www.amazon.com"));
			Toast.makeText(getBaseContext(), "Downloaded " + result + " bytes", Toast.LENGTH_LONG).show();    
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        
        /*
        try {
			new DoBackgroundTask().execute(
					new URL("http://www.amazon.com/somefiles.pdf"), 
					new URL("http://www.wrox.com/somefiles.pdf"),
					new URL("http://www.google.com/somefiles.pdf"),
					new URL("http://www.learn2develop.net/somefiles.pdf"));
        	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		*/	
        
        //doSomethingRepeatedly();
		
        Object[] objUrls = (Object[]) intent.getExtras().get("URLs");
        URL[] urls = new URL[objUrls.length];
        for (int i=0; i<objUrls.length; i++) {
        	urls[i] = (URL) objUrls[i];
        }        
    	new DoBackgroundTask().execute(urls);
		//START_STICKY – Service will be restarted if it gets terminated whether any requests are pending or not.
		return START_STICKY;
	}	

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate( new TimerTask() {
			public void run() {
                Log.d("MyService", String.valueOf(++counter));
                try {
					Thread.sleep(4000);
	                Log.d("MyService", counter + " Finished");

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, UPDATE_INTERVAL);
	}
		
	@Override
    public void onDestroy() {
        super.onDestroy();     
        if (timer != null){
        	timer.cancel();
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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

	private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
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
        			"Downloaded " + result + " files with Started service.",
        			Toast.LENGTH_LONG).show();
        	stopSelf();
        }        
	}
}

```

## Code: MyBoundService.java
```
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

```  

## Code: DownloadActivity.java
```
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

```
## Refrence
- [*Pro Android 5*](https://github.com/Apress/pro-android-5) by Dave MacLean, Satya Komatineni, and Grant Allen (Apress, 2015)
- [*Android Site*](https://developer.android.com/guide/components/bound-services.html)

## Thank You
#### [*Manoj Kumar*](https://www.linkedin.com/in/manojkumar19/)
#### Solutions Architect
