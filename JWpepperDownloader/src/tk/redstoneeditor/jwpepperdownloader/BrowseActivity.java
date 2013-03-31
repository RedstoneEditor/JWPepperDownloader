package tk.redstoneeditor.jwpepperdownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BrowseActivity extends ListActivity implements Runnable {
	EditText term;
	EditText pn;
	EditText rn;
	Document doc;
	ProgressDialog p;
	private boolean done;
	protected static final int GUIUPDATEIDENTIFIER = 0x101;

	public String filename(String s) { // gets filename without extension
		int dot = s.lastIndexOf(".item");
		int sep = s.lastIndexOf("/");
		return s.substring(sep + 1, dot);
	}

	SimpleAdapter sa;
	private ProgressDialog progressDialog;

	public void run() {

		try {
			doc = Jsoup
					.connect(
							"http://www.jwpepper.com/sheet-music/search.jsp?keywords="
									+ Uri.encode("" + term.getText())
									+ "&perPage="
									+ rn.getText()
									+ "&startIndex="
									+ (Integer.parseInt(pn.getText().toString()) * Integer.parseInt(rn.getText().toString()) - Integer.parseInt(rn.getText().toString())))
					.timeout(1000000000).get();
		} catch (IOException e) {
			Toast.makeText(BrowseActivity.this, "Failed to connect",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		Elements names;
		names = new Elements(doc.select("a.titlelink"));
		List<String> nameList = new ArrayList<String>();
		List<String> idList = new ArrayList<String>();
		for (Element name : names) {
			String nameText = name.text();
			nameList.add(nameText);
		}

		for (Element id : names) {
			String idText = id.attr("href");
			idList.add(filename(idText));
		}
		StatesAndCapitals = new String[nameList.size()][2];
		for (int i = 0; i < nameList.size(); i++) {
			Log.v("LINKS", Integer.toString(i));
			Log.v("NAMELIST", Integer.toString(nameList.size()));
			StatesAndCapitals[i][0] = nameList.get(i);
			StatesAndCapitals[i][1] = idList.get(i);
		}
		HashMap<String, String> item;
		for (int i = 0; i < StatesAndCapitals.length; i++) {
			item = new HashMap<String, String>();
			item.put("name", StatesAndCapitals[i][0]);
			item.put("id", StatesAndCapitals[i][1]);
			list.add(item);
		}
		Message m = new Message();
		m.what = GUIUPDATEIDENTIFIER;
		this.h.sendMessage(m);
	}

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
		final ListView lv = getListView();
		sa = new SimpleAdapter(this, list, android.R.layout.two_line_list_item,
				new String[] { "name", "id" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int row, long arg3) {
				TextView name1 = (TextView) arg1
						.findViewById(android.R.id.text1);
				TextView keyword1 = (TextView) arg1
						.findViewById(android.R.id.text2);
				String keyword = keyword1.getText().toString();
				String name = name1.getText().toString();
				Intent intent = new Intent(getBaseContext(), Download.class);
				intent.putExtra("id", keyword);
				intent.putExtra("name", name);
				startActivity(intent);
				Toast.makeText(getApplicationContext(), "Downloading " + name,
						Toast.LENGTH_LONG).show();
				return true;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView txt = (TextView) parent.getChildAt(
						position - lv.getFirstVisiblePosition()).findViewById(
						android.R.id.text2);
				String keyword = txt.getText().toString();
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("http://www.jwpepper.com/mp3/" + keyword
						+ ".mp3");
				it.setDataAndType(uri, "audio/mp3");
				startActivity(it);

			}

		});
		setListAdapter(sa);
		term = (EditText) findViewById(R.id.term);
		pn = (EditText) findViewById(R.id.page);
		rn = (EditText) findViewById(R.id.rpp);
		Button search = (Button) findViewById(R.id.buttonSearch);
		search.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!term.getText().toString().equals("")
						&& !pn.getText().toString().equals("")
						&& !rn.getText().toString().equals("")) {
					progressDialog = new ProgressDialog(BrowseActivity.this);
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("Please wait...");
					progressDialog.show();
					setListAdapter(null);
					StatesAndCapitals = null;
					Log.v("CLEARED LIST", "List cleared");
					list = new ArrayList<HashMap<String, String>>();
					new Thread(BrowseActivity.this).start();
				} else {
					Toast.makeText(BrowseActivity.this,
							"Please fill in all fields!", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	public void showDownload() {
		Intent i = new Intent();
		i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(i);
	}

	private String[][] StatesAndCapitals;
	final Handler h = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUIUPDATEIDENTIFIER:
				setListAdapter(new SimpleAdapter(BrowseActivity.this, list,
						android.R.layout.two_line_list_item, new String[] {
								"name", "id" }, new int[] { android.R.id.text1,
								android.R.id.text2 }));
				sa.notifyDataSetChanged();
				if (!(progressDialog == null)) {
					progressDialog.dismiss();
				}
				break;
			}
		}
	};

}
