package my.home.asynctask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TypeListAsyncTask extends AsyncTask<String, String, String> {

	public static final String TAG = "TypeListAsyncTask";
	public static final String LIST_CMD = "/list";
	private int timeoutConnection = 3000;  
	private int timeoutSocket = 5000; 
	BasicHttpParams httpParameters;
	
	private ProgressBar progressBar;
	
	private ArrayAdapter<String> adapter;
	
	public TypeListAsyncTask(ArrayAdapter<String> adapter, ProgressBar progressBar) {
		this.adapter = adapter;
		this.progressBar = progressBar;
		this.setConnectionParams();
	}
	
	@Override
	protected void onPreExecute() {
		this.adapter.clear();
		progressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}
	
	private void setConnectionParams() {
		httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	}
	
	@Override
	protected String doInBackground(String... params) {
		String urlString = SendCommandAsyncTask.SERVER_ADDRESS + LIST_CMD;
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
		if (!TextUtils.isEmpty(result)) {
			this.adapter.addAll(result.split("\n"));
		}
		progressBar.setVisibility(View.INVISIBLE);
		super.onPostExecute(result);
	}

}
