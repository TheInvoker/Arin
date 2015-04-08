package async.user;

import http.HttpTask;
import http.Network;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import widget.Popup;
import activity.LoginScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;
import arin.ArinContext;

/*
 * This is a async task that allows you to login and downloads the database
 */

public class Async_login extends AsyncTask<String, Void, String> implements HttpTask {

	private LoginScreen context;
	private String email;
	private String password;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "user/login.php";
	
	public Async_login(LoginScreen context, String email, String password) {
		this.context = context;
		this.email = email;
		this.password = password;

		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.loggining));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	execute();
	}

	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("email", email));
		urlValues.add(new BasicNameValuePair("password", password));
		
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
					ArinContext.loadUserFromJSON(context, result);
					
					worked();
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (JSONException e) {
				context.goToNextScreen(context.getString(R.string.unexpected_error_offline_mode));
			}
		} else {
			context.goToNextScreen(context.getString(R.string.server_error_offline_mode));
		}
		
		progressDialog.dismiss();
	}
	
	private void worked() {
		context.goToNextScreen(null);
	}
}
