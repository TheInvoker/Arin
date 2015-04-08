package async.image;

import image.FishImage;

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
import activity.GalleryScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that approves image requests
 */

public class Async_update_image_state extends AsyncTask<String, Void, String> implements HttpTask {
	
	private GalleryScreen context;
	private Boolean isUsedButtonClicked;
	private ProgressDialog progressDialog;
	private Fish fish;
	private Boolean mainState;
	private Boolean usedState;
	private FishImage image;
	private final String WEBFILE = "image/setimagestate.php";
	
	public Async_update_image_state(GalleryScreen context, Fish fish, FishImage image, Boolean mainState, Boolean usedState, Boolean isUsedButtonClicked) {
		this.context = context;
		this.isUsedButtonClicked = isUsedButtonClicked;
		this.fish = fish;
		this.mainState = mainState;
		this.usedState = usedState;
		this.image = image;
		
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
		urlValues.add(new BasicNameValuePair("fish_id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(image.getId(), 10)));
		urlValues.add(new BasicNameValuePair("main_state", mainState ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("used_state", usedState ? "1" : "0"));
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
		if (isUsedButtonClicked) {
			context.usedClickWorked();
		} else {
			context.mainClickedWorked();
		}
	}
}
