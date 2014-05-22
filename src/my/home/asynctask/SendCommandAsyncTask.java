package my.home.asynctask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import my.home.adapter.TypeListAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.R.string;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;



public class SendCommandAsyncTask extends AsyncTask<String, String, String> {
	
	public static final String TAG = "SendCommandAsyncTask";
	
	public static final String NEXT = "/next";
	public static final String PAUSE = "/pause";
	public static final String MARK = "/mark";
	public static String SERVER_ADDRESS = "http://192.168.1.100:8888";
	
	private int timeoutConnection = 3000;  
	private int timeoutSocket = 5000; 
	BasicHttpParams httpParameters;

	private WeakReference<ListView> responseListViewReference;
	private ProgressBar progressBar;
	
	public SendCommandAsyncTask(ListView typeListView, ProgressBar progressBar) {
		responseListViewReference = new WeakReference<ListView>(typeListView);
		this.progressBar = progressBar;
		this.setConnectionParams();
	}
	
	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}
	
	private void setConnectionParams() {
		httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	}
	
	@Override
    protected String doInBackground(String... cmd) {
		String urlString = SERVER_ADDRESS + cmd[0];
		if (cmd.length > 1) {
			String type = cmd[1];
			if (!TextUtils.isEmpty(type)) {
				try {
					urlString += "?type=" + URLEncoder.encode(type, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		Log.d(TAG, ("sending cmd: " + urlString));
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(urlString));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
        	System.out.println(e);
        } catch (IOException e) {
        	System.out.println(e);
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
		TypeListAdapter adapter = (TypeListAdapter) responseListViewReference.get().getAdapter();
    	final int pos = adapter.getPosition(result);
    	final ListView listView = responseListViewReference.get();
    			listView.post(new Runnable() {
            @Override
            public void run() {
            	if (pos >= listView.getLastVisiblePosition()
            			||	pos <= listView.getFirstVisiblePosition()) {
            		listView.setSelection(pos);
				}
            }
        });
    	adapter.setSelection(pos);
		adapter.notifyDataSetChanged();
		progressBar.setVisibility(View.INVISIBLE);
        super.onPostExecute(result);
    }

}
