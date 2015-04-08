package async.user;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import common.FieldValidation;
import widget.Popup;
import http.HttpTask;
import http.Network;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.EditText;
import android.widget.Toast;
import arin.ArinContext;

/*
 * This is a async task that emails you your password
 */

public class Async_forgot_password extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "user/forgotpassword.php";
	
	public Async_forgot_password(Activity context) {
		this.context = context;
		handleForgot();
	}
	
    private void handleForgot() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.action_forgot_password);
	    builder.setMessage(R.string.type_your_email);
	    
	    String def_email = ArinContext.getUser().getEmail();
	    
	    final EditText et = new EditText(context);
	    et.setHint(R.string.hint_email);
	    if (def_email != null) et.setText(def_email);
	    builder.setView(et);
	    
	    builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
	    }).setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	String email = et.getText().toString();
	        	
	        	if (!FieldValidation.isValidEmail(email)) {
	        		Popup.ShowErrorMessage(context, R.string.invalid_email, false);
	        	} else {
		    		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
		        	progressDialog.setMessage(context.getString(R.string.emailing));
		        	progressDialog.setCancelable(false);
		        	progressDialog.show();
		    		
		        	execute(email);
	        	}
	        }
	    });
	    
	    builder.show();
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("email", arg0[0]));
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
		Toast.makeText(context, R.string.pass_emailed, Toast.LENGTH_SHORT).show();
	}
}
