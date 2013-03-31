package tk.redstoneeditor.jwpepperdownloader;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.widget.SeekBar;
import android.view.*;
import android.media.*;
import android.net.*;
import java.io.*;
import android.content.*;

public class MediaPlayerActivity extends Activity
{
	MediaPlayer mp;
	ProgressDialog progressDialog;
	SeekBar seekBar;
	Runnable notification;
    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediaplayer);

		
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		progressDialog = new ProgressDialog(MediaPlayerActivity.this);
		progressDialog
			.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Please wait...");
		progressDialog.show();
		new Thread(){
			public void run()
			{
				try
				{

					mp.setDataSource("http://www.jwpepper.com/mp3/" + getIntent().getExtras().getString("id") + ".mp3");
					mp.prepare();
					Message m = new Message();
					m.what = 0x101;
					MediaPlayerActivity.this.h.sendMessage(m);
				}
				catch (IOException e)
				{}
				catch (SecurityException e)
				{}
				catch (IllegalArgumentException e)
				{}
				catch (IllegalStateException e)
				{}
			}
		}.start();

        mp.setOnErrorListener(mErrorListener);
		Button play = (Button) findViewById(R.id.play);
		Button pause = (Button) findViewById(R.id.pause);

		play.setOnClickListener(new View.OnClickListener(){

				public void onClick(View v)
				{
					try
					{
						mp.prepare();
					}
					catch (IOException e)
					{}
					catch (IllegalStateException e)
					{}
					mp.start();
					startPlayProgressUpdater();
				}	
			});

		pause.setOnClickListener(new View.OnClickListener(){

				public void onClick(View v)
				{
					try
					{
						mp.prepare();
					}
					catch (IOException e)
					{}
					catch (IllegalStateException e)
					{}
					mp.pause();
				}


			});
	}

	@Override
	public void onDestroy()
	{
		try
		{
			mp.prepare();
		}
		catch (IOException e)
		{}
		catch (IllegalStateException e)
		{}
		h.removeCallbacks(notification);
		mp.stop();
		super.onDestroy();
		mp.release();
	}

	final Handler h = new Handler() {
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0x101:
		            mp.start();
					seekBar = (SeekBar) findViewById(R.id.SeekBar01);
					seekBar.setMax(mp.getDuration());
					startPlayProgressUpdater();
					seekBar.setOnTouchListener(new View.OnTouchListener() {
							@Override 
							public boolean onTouch(View v, MotionEvent event)
							{
								seekChange(v);
								return false; 
							}
						});
					if (!(progressDialog == null))
					{
						progressDialog.dismiss();
					}
					break;
					case 12345:
					seekBar.setProgress(mp.getCurrentPosition());
					break;
			}
		}
	};
	
	@Override
	public boolean onError(MediaPlayer player, int what, int extra) {
		mp.reset();
		return true;
	}
	
	private MediaPlayer.OnErrorListener mErrorListener =
	new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int a, int b) {
     
            /* If an error handler has been supplied, use it and finish. */
            
                if (onError(mp, a, b)) {
                    return true;
                
            }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
            if (1==1) {
                new AlertDialog.Builder(MediaPlayerActivity.this)
					.setTitle("Error")
					.setMessage("This track could not be played")
					.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/* If we get here, there is no onError listener, so
							 * at least inform them that the video is over.
							 */
							MediaPlayerActivity.this.mp.stop();
							MediaPlayerActivity.this.mp.reset();
						}
					})
					.setCancelable(false)
					.show();
            }
            return true;
        }
    };

	public void startPlayProgressUpdater()
	{
		
			try
			{
				mp.prepare();
			}
			catch (IOException e)
			{}
			catch (IllegalStateException e)
			{}
		Message msg = new Message();
		msg.what = 12345;
		MediaPlayerActivity.this.h.sendMessage(msg);
					if (mp.isPlaying())
		{
			notification = new Runnable() {
		        public void run()
				{
		        	startPlayProgressUpdater();
				}
			};
			h.postDelayed(notification, 1000);
		}
		else
		{	
			mp.pause();
			seekBar.setProgress(0);
		}
	} // This is event handler thumb moving event 
	private void seekChange(View v)
	{
			if (mp.isPlaying())
		{
			SeekBar sb = (SeekBar)v;
			mp.seekTo(sb.getProgress());
		}
	}

}
