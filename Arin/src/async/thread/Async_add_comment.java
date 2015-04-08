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
import arin.ArinContext;

/*
 * This is a async task that sets location approval status
 */

public class Async_add_comment extends AsyncTask<String, Void, String> implements HttpTask {
	
	private ThreadScreen context;
	private String comment;
	private int thread_id;
	private String thread_title;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "thread/addcomment.php";
	
	public Async_add_comment(ThreadScreen context, int thread_id, String thread_title, String comment) {
		this.context = context;
		this.comment = comment;
		this.thread_id = thread_id;
		this.thread_title = thread_title;

		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.saving));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
    	execute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("thread_id", Integer.toString(thread_id, 10)));
		urlValues.add(new BasicNameValuePair("comment", comment));
		urlValues.add(new BasicNameValuePair("thread_title", thread_title));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(ArinContext.getUser().getId(), 10)));
		
		Pair<String, Integer> pair = Network.GetHTTPResponse(context.getString(R.string.arin_host), urlValues);
		
		return pair.first;
	}
	
	@Override
	protected void onPostExecute(String response) {
		if (response!=null) {
			try {
				JSONObject object = new JSONObject(response);
				int code = object.getInt(CODE);
				
				if (code==200) {
					
					JSONObject result = object.getJSONObject(RESPONSE);
					int comment_id = result.getInt("comment_id");
					
					Worked(comment_id);
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context, R.string.unexpected_error, false);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, false);
		}
		progressDialog.dismiss();
	}
	
	private void Worked(int comment_id) {
		context.addcommentCallback(comment_id);
	}
}
