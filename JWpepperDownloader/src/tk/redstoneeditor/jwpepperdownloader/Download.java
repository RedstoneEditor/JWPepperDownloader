package tk.redstoneeditor.jwpepperdownloader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Download extends Activity
{
    String id;
	String name;
	long enqueue;
	DownloadManager dm;
    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		registerReceiver(receiver, new IntentFilter(
							 DownloadManager.ACTION_DOWNLOAD_COMPLETE));


		id = getIntent().getExtras().getString("id");
		name = getIntent().getExtras().getString("name");

		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Request request = new Request(Uri
									  .parse("http://www.jwpepper.com/mp3/" + id
											 + ".mp3"));
		if (name != "")
		{
			request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, name
				+ ".mp3");
		}
		enqueue = dm.enqueue(request);
		Toast.makeText(getApplicationContext(),
					   "Downloading " + name, Toast.LENGTH_LONG)
			.show();

	}
	
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		registerReceiver(receiver, new IntentFilter(
							 DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Query query = new Query();
                query.setFilterById(enqueue);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {

                    } else if(DownloadManager.ERROR_FILE_ALREADY_EXISTS == c.getInt(columnIndex)){
                    	Toast.makeText(Download.this, "This file already Exists!", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    };

	public void showDownload()
	{
		Intent i = new Intent();
		i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(i);
	}

}
