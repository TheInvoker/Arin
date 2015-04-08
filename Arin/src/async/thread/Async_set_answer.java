package async.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import thread.Comment;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.ThreadScreen;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that sets location approval status
 */

public class Async_set_answer extends AsyncTask<String, Void, String> implements HttpTask {
	
	private ThreadScreen context;
	private Comment comment;
	private int thread_id;
	private final String WEBFILE = "thread/setanswer.php";
	
	public Async_set_answer(ThreadScreen context, Comment comment, int thread_id) {
		this.context = context;
		this.comment = comment;
		this.thread_id = thread_id;

    	execute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("thread_id", Integer.toString(thread_id, 10)));
		urlValues.add(new BasicNameValuePair("comment_id", Integer.toString(comment.getId(), 10)));
		urlValues.add(new BasicNameValuePair("new_state", comment.is_answer() ? "0" : "1"));
		
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
					Worked();
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context, R.string.unexpected_error, false);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, false);
		}
	}
	
	private void Worked() {
		context.handleSetAsAnswerCallback(comment);
	}
}
