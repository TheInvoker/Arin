package async.history;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import widget.Popup;
import android.app.Activity;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;
import fish.Fish;
import history.History;
import http.HttpTask;
import http.Network;

public class Async_get_history extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Fish fish;
	private Boolean forHistory;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "history/gethistory.php";
	
	public Async_get_history(Activity context, Fish fish, Boolean forHistory) {
		this.context = context;
		this.fish = fish;
		this.forHistory = forHistory;
		
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.loading));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	execute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("fish_id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("forHistory", forHistory ? "1" : "0"));
		
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
					JSONArray result = object.getJSONArray(RESPONSE);
					showHistory(result);
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
	
	private void showHistory(JSONArray array) {
		if (forHistory) {
			History.displayHistory(context, fish, array);
		} else {
			History.displayChangeRequsts(context, fish, array);
		}
	}
}
