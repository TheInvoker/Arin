package async.location;

import java.util.ArrayList;
import java.util.List;
import location.Place;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import fish.Species;
import http.HttpTask;
import http.Network;
import activity.LocationScreen;
import activity.SpeciesScreen;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that deletes locations
 */

public class Async_delete_location extends AsyncTask<String, Void, String> implements HttpTask {
	
	private LocationScreen context;
	private Place location;
	private ProgressDialog progressDialog;
	private Species specie;
	private final String WEBFILE = "location/deletelocation.php";
	
	public Async_delete_location(LocationScreen context, Species specie, Place location) {
		this.context = context;
		this.location = location;
		this.specie = specie;
		
		handleDelete();
	}
	
    private void handleDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.confirmdelete);
	    builder.setMessage(R.string.deleteAll);
	    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	    		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
	        	progressDialog.setMessage(context.getString(R.string.deleting));
	        	progressDialog.setCancelable(false);
	        	progressDialog.show();
	    		
	        	execute();
	        }
	    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
	    });
	    builder.show();
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
		specie.getLocations().remove(location);
		context.removePlace(location);
		SpeciesScreen.locationChanged = true;
	}
}
