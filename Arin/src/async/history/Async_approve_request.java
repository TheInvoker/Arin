package async.history;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import fish.ChildRefresher;
import fish.Fish;
import history.History;
import http.HttpTask;
import http.Network;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that approves edit requests
 */

public class Async_approve_request extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Fish fish;
	private History history;
	private AlertDialog alert;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "history/approverequest.php";
	
	public Async_approve_request(Activity context, Fish fish, History item, AlertDialog alert) {
		this.context = context;
		this.fish = fish;
		this.history = item;
		this.alert = alert;
		
		handleClick();
	}
	
	private void handleClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.notice)
		.setMessage(R.string.approve_the_request)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            	progressDialog.setMessage(context.getString(R.string.approving));
            	progressDialog.setCancelable(false);
            	progressDialog.show();
        		
            	execute();
            }
        })
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("name", history.getNew_name()));
		urlValues.add(new BasicNameValuePair("resource", history.getNew_resource_link()));
		urlValues.add(new BasicNameValuePair("history_id", Integer.toString(history.getId(), 10)));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(history.getBanUserId(), 10)));
		urlValues.add(new BasicNameValuePair("version", Integer.toString(fish.getVersion(), 10)));
		
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
		alert.cancel();
		fish.handleChangeRequestSuccess(context, history.getNew_name(), history.getNew_resource_link());
		((ChildRefresher)context).childRefresherReturn(fish);
	}
}
