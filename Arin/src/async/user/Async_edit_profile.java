package async.user;

import http.HttpTask;
import http.Network;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import user.Actor;
import widget.Popup;
import common.FieldValidation;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import arin.ArinContext;

/*
 * This is a async task that edits your profile
 */

public class Async_edit_profile extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private String newName;
	private String newEmail;
	private String newPassword;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "user/editprofile.php";
	
	public Async_edit_profile(Activity context) {
		this.context = context;

		Actor user = ArinContext.getUser();
		enterUsername(user.getName(), user.getEmail(), user.getPassword(), user.getPassword());
	}
	
	@SuppressLint("InflateParams")
	private void enterUsername(String defaultusername, String defaultemail, String defaultpassword, String defaultconfirmpassword) {
		LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_registration, null);
		
		final AlertDialog d = new AlertDialog.Builder(context)
        .setView(dialoglayout)
        .setTitle(R.string.edit_profile)
        .setPositiveButton(R.string.save, null)
        .setNegativeButton(R.string.cancel, null)
        .create();

        final EditText usernameET = (EditText)dialoglayout.findViewById(R.id.username);
        final EditText emailET = (EditText)dialoglayout.findViewById(R.id.email);
        final EditText passwordET = (EditText)dialoglayout.findViewById(R.id.password);
        final EditText confirmET = (EditText)dialoglayout.findViewById(R.id.confirm_password);
        
        usernameET.setText(defaultusername);
        emailET.setText(defaultemail);
        passwordET.setText(defaultpassword);
        confirmET.setText(defaultconfirmpassword);
		
		d.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface dialog) {
		        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	newName = usernameET.getText().toString().trim();
		            	newEmail = emailET.getText().toString().trim();
		            	newPassword = passwordET.getText().toString().trim();
		            	String confirmpassword = confirmET.getText().toString().trim();
		            	
						if (!FieldValidation.isValidName(newName)) {
							Popup.ShowErrorMessage(context, R.string.invalid_username, false);
						} else if (!FieldValidation.isValidEmail(newEmail)) {
							Popup.ShowErrorMessage(context, R.string.invalid_email, false);
						} else if (!FieldValidation.isValidPassword(newPassword)) {
							Popup.ShowErrorMessage(context, R.string.invalid_password, false);
						} else if (!newPassword.equals(confirmpassword)) {
							Popup.ShowErrorMessage(context, R.string.not_same_password, false);
						} else {
							d.dismiss();
							start();
						}
		            }
		        });
	        }
		});
		
		d.show();
	}
	
	private void start() {
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.saving));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	Actor user = ArinContext.getUser();
    	execute(Integer.toString(user.getId(), 10));
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", arg0[0]));
		urlValues.add(new BasicNameValuePair("username", newName));
		urlValues.add(new BasicNameValuePair("email", newEmail));
		urlValues.add(new BasicNameValuePair("password", newPassword));
		
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
		Actor user = ArinContext.getUser();
		user.setName(newName);
		user.setEmail(newEmail);
		user.setPassword(newPassword);
		
		if (ArinContext.saveUserData(context)) {
			Toast.makeText(context, R.string.profile_saved, Toast.LENGTH_SHORT).show();
		} else {
			Popup.ShowErrorMessage(context, R.string.localsaveerror, false);
		}
	}
}
