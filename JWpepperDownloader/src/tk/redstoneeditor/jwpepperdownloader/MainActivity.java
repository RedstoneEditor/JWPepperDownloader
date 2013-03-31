package tk.redstoneeditor.jwpepperdownloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	EditText id;
	EditText name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		id = (EditText) findViewById(R.id.id);
		name = (EditText) findViewById(R.id.name);
		Button browse = (Button) findViewById(R.id.buttonBrowse);
		Button preview = (Button) findViewById(R.id.buttonPreview);
		Button dl = (Button) findViewById(R.id.buttonDownload);
		browse.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, BrowseActivity.class);
				startActivity(i);
			}
		});
		dl.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
Intent i = new Intent(MainActivity.this, Download.class);
				i.putExtra("id", id.getText().toString());
				i.putExtra("name", name.getText().toString());
				startActivity(i);
			}
		});
		preview.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent it = new Intent(Intent.ACTION_VIEW);
				//Uri uri = Uri.parse("http://www.jwpepper.com/mp3/"
				//		+ id.getText() + ".mp3");
				//it.setDataAndType(uri, "audio/mp3");
				//startActivity(it);
				
				Intent i = new Intent(MainActivity.this, MediaPlayerActivity.class);
				i.putExtra("id", id.getText().toString());
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
