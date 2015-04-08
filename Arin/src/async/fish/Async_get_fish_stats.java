package async.fish;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import fish.Fish;
import http.HttpTask;
import http.Network;
import android.app.Activity;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;

public class Async_get_fish_stats extends AsyncTask<String, Void, String> implements HttpTask {
	
	private Activity context;
	private Fish fish;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/fishstats.php";
	
	public Async_get_fish_stats(Activity context, Fish fish) {
		this.context = context;
		this.fish = fish;
		
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.loading));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
    	execute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
		
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
					int version = result.getInt("version");
					int approved_history = result.getInt("approved_history");
					int unapproved_history = result.getInt("unapproved_history");
					int unapproved_images = result.getInt("unapproved_images");
					int approved_images = result.getInt("approved_images");
					int unapproved_locations = result.getInt("unapproved_locations");
					int approved_locations = result.getInt("approved_locations");
					int species = result.getInt("species");
					
					worked(version, 
							approved_history, 
							unapproved_history,
							unapproved_images,
							approved_images,
							unapproved_locations,
							approved_locations,
							species);
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
	
	public void worked(int version, int approved_history, int unapproved_history, int unapproved_images, int approved_images, int unapproved_locations, int approved_locations, int species) {
		
		String stats = fish.formatStats(context, version, 
				approved_history, 
				unapproved_history,
				unapproved_images,
				approved_images,
				unapproved_locations,
				approved_locations,
				species);
		
		Popup.ShowString(context, R.string.stats, stats, false);
	}
}
