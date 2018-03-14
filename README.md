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
Toast message progress update 20%
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-03-12 23-55-54.png">
Toast message progress update 40%
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-03-12 23-56-15.png">
Toast message progress update 100%
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-03-12 23-57-23.png">


## Code: MyService.java
```
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
			//---simulate taking some time to download a file---
			//Thread.sleep(5000);

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

		return 100;
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
        			"Downloaded " + result + " bytes", 
        			Toast.LENGTH_LONG).show();
        	stopSelf();
        }        
	}
}

```


## Code: DownloadActivity.java
```
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
```
## Refrence
- [*Pro Android 5*](https://github.com/Apress/pro-android-5) by Dave MacLean, Satya Komatineni, and Grant Allen (Apress, 2015)

## Thank You
#### [*Manoj Kumar*](https://www.linkedin.com/in/manojkumar19/)
#### Solutions Architect
