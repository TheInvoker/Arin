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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;

/*
 * This is a async task that deletes images
 */

public class Async_delete_image extends AsyncTask<String, Void, String> implements HttpTask {

	private GalleryScreen context;
	private Fish fish;
	private FishImage image;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "image/deleteimage.php";
	
	public Async_delete_image(GalleryScreen context, Fish fish, FishImage image) {
		this.context = context;
		this.fish = fish;
		this.image = image;
		
    	confirmDialog();
	}
	
	private void confirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.delete_image).setMessage(R.string.yes_to_delete)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            	progressDialog.setMessage(context.getString(R.string.deleting));
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
		urlValues.add(new BasicNameValuePair("id", Integer.toString(image.getId(), 10)));
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
		fish.getImageList().remove(image);
		context.deleteCallback(image);
	}
}
