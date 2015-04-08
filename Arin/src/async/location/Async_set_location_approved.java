package async.location;

import java.util.ArrayList;
import java.util.List;
import location.Place;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.LocationScreen;
import activity.SpeciesScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that sets location approval status
 */

public class Async_set_location_approved extends AsyncTask<String, Void, String> implements HttpTask {
	
	private LocationScreen context;
	private Place location;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "location/setlocationapproved.php";
	
	public Async_set_location_approved(LocationScreen context, Place location) {
		this.context = context;
		this.location = location;
		
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
		urlValues.add(new BasicNameValuePair("id", Integer.toString(location.getId(), 10)));
		
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
		progressDialog.dismiss();
	}
	
	private void Worked() {
		location.setApproved(!location.isApproved());
		context.setPlaceApproved(location);
		SpeciesScreen.locationChanged = true;
	}
}
