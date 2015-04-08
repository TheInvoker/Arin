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
import common.FieldValidation;
import activity.LoginScreen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * This is a async task that gets you resistered
 */

public class Async_register extends AsyncTask<String, Void, String> implements HttpTask {

	private LoginScreen context;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "user/register.php";
	
	public Async_register(LoginScreen context) {
		this.context = context;
		register();
	}
	
	@SuppressLint("InflateParams")
	private void register() {
		LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_registration, null);
		
		final AlertDialog d = new AlertDialog.Builder(context)
        .setView(dialoglayout)
        .setTitle(R.string.register)
        .setNegativeButton(R.string.submit, null)
        .setPositiveButton(R.string.cancel, null)
        .create();

	    final EditText usernameET = (EditText)dialoglayout.findViewById(R.id.username);
	    final EditText emailET = (EditText)dialoglayout.findViewById(R.id.email);
	    final EditText passwordET = (EditText)dialoglayout.findViewById(R.id.password);
	    final EditText confirmpasswordET = (EditText)dialoglayout.findViewById(R.id.confirm_password);
		
		d.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface dialog) {
		        Button b = d.getButton(AlertDialog.BUTTON_NEGATIVE);
		        b.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	String username = usernameET.getText().toString().trim();
						String email = emailET.getText().toString().trim();
						String password = passwordET.getText().toString().trim();
						String confirmpassword = confirmpasswordET.getText().toString().trim();
						
						if (!FieldValidation.isValidName(username)) {
							Popup.ShowErrorMessage(context, R.string.invalid_username, false);
						} else if (!FieldValidation.isValidEmail(email)) {
							Popup.ShowErrorMessage(context, R.string.invalid_email, false);
						} else if (!FieldValidation.isValidPassword(password)) {
							Popup.ShowErrorMessage(context, R.string.invalid_password, false);
						} else if (!password.equals(confirmpassword)) {
							Popup.ShowErrorMessage(context, R.string.not_same_password, false);
						} else {
							d.dismiss();
							start(username, email, password);
						}
		            }
		        });
	        }
		});
		
		d.show();
	}
	
	private void start(String username, String email, String password) {
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.registering));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	execute( 
			username, 
			email,
			password
		);
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("username", arg0[0]));
		urlValues.add(new BasicNameValuePair("email", arg0[1]));
		urlValues.add(new BasicNameValuePair("password", arg0[2]));
		
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
		Toast.makeText(context, R.string.registered, Toast.LENGTH_SHORT).show();
	}
}
