package async.fish;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import http.HttpTask;
import http.Network;
import activity.MenuScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;
import arin.ArinContext;

public class Async_get_database extends AsyncTask<String, Void, String> implements HttpTask {
	
	private MenuScreen context;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/getdatabase.php";

	public Async_get_database(MenuScreen context) {
		this.context = context;

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
					ArinContext.loadDataFromJSON(context, result);
					
					worked();
				} else {
					context.showMessageAndContinue(object.getString(RESPONSE) + " " + context.getString(R.string.using_saved));
				}
			} catch (JSONException e) {
				context.showMessageAndContinue(R.string.unexpected_error_using_saved);
			}
		} else {
			context.showMessageAndContinue(R.string.server_error_using_saved);
		}
		
		progressDialog.dismiss();
	}
	
	private void worked() {
		context.goToNextScreenHelper();
	}
}
