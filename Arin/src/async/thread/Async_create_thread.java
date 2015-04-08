package async.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import common.FieldValidation;

import user.Actor;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.AllThreadsScreen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import arin.ArinContext;

/*
 * This is a async task that adds a fish
 */

public class Async_create_thread extends AsyncTask<String, Void, String> implements HttpTask {

	private AllThreadsScreen context;
	private ProgressDialog progressDialog;
	private String title, comment;
	private final String WEBFILE = "thread/addthread.php";
	
	public Async_create_thread(AllThreadsScreen context) {
		this.context = context;
		
		addThread();
	}
	
    @SuppressLint("InflateParams")
	public void addThread() {
        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_thread, null);
		final EditText titleET = (EditText)dialoglayout.findViewById(R.id.title);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	title = titleET.getText().toString();
            	comment = commentET.getText().toString();
            	
            	if (!FieldValidation.isValidThreadTitle(title)) {
            		Popup.ShowErrorMessage(context, R.string.invalid_title, false);
            	} else {
	            	progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
	            	progressDialog.setMessage(context.getString(R.string.adding));
	            	progressDialog.setCancelable(false);
	            	progressDialog.show();
	            	
	            	execute();
            	}
            }
        };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.new_thread).setCancelable(true);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).setPositiveButton(R.string.create, listener);
        
        builder.setView(dialoglayout);
		builder.show();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		Actor user = ArinContext.getUser();
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(user.getId(), 10)));
		urlValues.add(new BasicNameValuePair("title", title));
		urlValues.add(new BasicNameValuePair("comment", comment));
		urlValues.add(new BasicNameValuePair("user_id", String.valueOf(ArinContext.getUser().getId())));
		
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
					worked();
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
	
	private void worked() {
		context.addPostCallback();
	}
}
