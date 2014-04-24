package my.home.musictrainning;

import my.home.adapter.TypeListAdapter;
import my.home.asynctask.SendCommandAsyncTask;
import my.home.asynctask.TypeListAsyncTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.musictrainning.R;

public class MainActivity extends Activity implements View.OnClickListener {

	public TypeListAdapter adapter;
	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btn = (Button) findViewById(R.id.nextButton);
		btn.setOnClickListener(this);
		btn = (Button) findViewById(R.id.pauseButton);
		btn.setOnClickListener(this);
		btn = (Button) findViewById(R.id.markButton);
		btn.setOnClickListener(this);
		
		adapter = new TypeListAdapter(this, android.R.layout.simple_list_item_1);
		final ListView listView = (ListView) findViewById(R.id.type_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelection(position);
				adapter.notifyDataSetChanged();
				new SendCommandAsyncTask(listView, progressBar).execute(SendCommandAsyncTask.NEXT, adapter.getItem(position));
			}
		});
		
		progressBar = (ProgressBar) findViewById(R.id.type_list_progress_bar);
		new TypeListAsyncTask(adapter, progressBar).execute();
	}

	@Override
	public void onClick(View view) {
		ListView rListView = (ListView)findViewById(R.id.type_list);
		switch (view.getId()) {
		case R.id.nextButton:
			new SendCommandAsyncTask(rListView, progressBar).execute(SendCommandAsyncTask.NEXT);
			break;
		case R.id.pauseButton:
			new SendCommandAsyncTask(rListView, progressBar).execute(SendCommandAsyncTask.PAUSE);
			break;
		case R.id.markButton:
			new SendCommandAsyncTask(rListView, progressBar).execute(SendCommandAsyncTask.MARK);
			break;
		default:
			break;
		}
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
		case R.id.action_settings:
			Intent intent = new Intent();
        	intent.setClass(MainActivity.this, SettingsActivity.class);
        	startActivityForResult(intent, 0); 
        	return true;
		case R.id.type_list_layout:
			new TypeListAsyncTask(adapter, progressBar).execute();
			return true;
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	String new_sub_address = SendCommandAsyncTask.SERVER_ADDRESS;
    	loadPref();
    	if (!new_sub_address.equals(SendCommandAsyncTask.SERVER_ADDRESS)) {
    		new TypeListAsyncTask(adapter, progressBar).execute();
		}
    }
    
    private void loadPref(){
    	SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	SendCommandAsyncTask.SERVER_ADDRESS = mySharedPreferences.getString("pref_server_address", "tcp://192.168.1.102:9000");
    }
}
