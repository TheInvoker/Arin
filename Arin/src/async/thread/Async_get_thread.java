package async.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.ThreadScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that gets comments
 */

public class Async_get_thread extends AsyncTask<String, Void, String> implements HttpTask {

	private ThreadScreen context;
	private ProgressDialog progressDialog;
	private int thread_id;
	private final String WEBFILE = "thread/getcomments.php";
	
	public Async_get_thread(ThreadScreen context, int thread_id) {
		this.context = context;
		this.thread_id = thread_id;
		
		progressDialog = new ProgressDialog(context.getContext(), ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getContext().getString(R.string.loading));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	execute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("thread_id", Integer.toString(thread_id, 10)));
		
		Pair<String, Integer> pair = Network.GetHTTPResponse(context.getContext().getString(R.string.arin_host), urlValues);
		
		return pair.first;
	}
	
	@Override
	protected void onPostExecute(String response) {
		if (response!=null) {
			try {
				JSONObject object = new JSONObject(response);
				int code = object.getInt(CODE);
				
				if (code==200) {
					String result = object.getString(RESPONSE);
					worked(result);
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), true);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context.getContext(), R.string.unexpected_error, true);
			}
		} else {
			Popup.ShowErrorMessage(context.getContext(), R.string.server_error, true);
		}
		progressDialog.dismiss();
	}
	
	private void worked(String result) {
		context.getThreadCallback(result);
	}
}
