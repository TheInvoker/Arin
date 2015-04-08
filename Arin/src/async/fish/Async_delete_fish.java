package async.fish;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import fish.ChildDeleter;
import fish.Fish;
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
 * This is a async task that deletes fish
 */

public class Async_delete_fish extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Fish fish;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/deletefish.php";
	
	public Async_delete_fish(Activity context, Fish fish) {
		this.context = context;
		this.fish = fish;
		
		handleDelete();
	}
	
    private void handleDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.confirmdelete);
	    builder.setMessage(R.string.deletefull);
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
		urlValues.add(new BasicNameValuePair("id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
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
		((ChildDeleter)context).childDeleterReturn(fish);
		fish.handleDeleteSuccess(context);
	}
}
