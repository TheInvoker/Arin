package activity;

import user.Actor;
import user.User;
import widget.Popup;
import common.FieldValidation;
import common.Transition;
import http.Network;
import android.app.Activity;
import android.app.NotificationManager;
import com.arin.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import arin.ArinContext;
import async.recognition.Async_recognition;
import async.user.Async_forgot_password;
import async.user.Async_login;
import async.user.Async_register;


/*
 * This activity handles the login activity.
 */

public class LoginScreen extends Activity {
	
	// to keep track of the views on the screen
	private EditText emailET = null;
	private EditText passwordET = null;
	private Button login = null;		
	private CheckBox checkbox = null;
	
	public static final String MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		
	    // clear notifications
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Async_recognition.SCAN_COMPLETE_NOTIFICATION_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
			case R.id.action_forgotpassword:
				forgotPassword();
				return true;
			case R.id.action_offline_login:
				offlineLoginButtonClick();
				return true;
			case R.id.action_register:
				registerButtonClick();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	



	


	
	
	


	
	
	
	


	
	
	@Override
	protected void onResume() {
	    super.onResume();

	    // checks if app started for first time
	    Boolean firstTime = emailET == null;
	    
	    // reset cached data
	    logout();
	    
	    // if first time, then set 
	    if (firstTime) {
	    	recordFields();
	    	fillInFields();
			handleAutoLogin();
	    }
	}
	
	private void logout() {
		ArinContext.setUser(new User(this));
	}

	private void recordFields() {
		emailET = (EditText)findViewById(R.id.email);
        passwordET = (EditText)findViewById(R.id.password);
		login = (Button) findViewById(R.id.loginButton);
		checkbox = (CheckBox) findViewById(R.id.checkBox);
	}
	
	private void fillInFields() {
		Actor user = ArinContext.getUser();
        String email = user.getEmail();
        String password = user.getPassword();
        
        Boolean autoLogin = ArinContext.getUser().isAutoLogin();
        if (email != null) emailET.setText(email);
        if (autoLogin) {
        	if (password != null) passwordET.setText(password);
        } else {
        	if (email == null) emailET.requestFocus();
        	else passwordET.requestFocus();
        }
        checkbox.setChecked(autoLogin);
	}
	
	private void handleAutoLogin() {
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				ArinContext.getUser().setAutoLogin(arg1);
			}
		});
		
	    if (checkbox.isChecked() && FieldValidation.isValidEmail(emailET.getText().toString()) && FieldValidation.isValidPassword(passwordET.getText().toString())) {   
			login.performClick();
		}
	}
	
	
	
	

	

	
	public void goToNextScreen(String message) {
		if (ArinContext.saveUserData(this)) {
	    	Intent myIntent = new Intent(this, MenuScreen.class);
	    	myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	
	    	if (message != null) {
	    		myIntent.putExtra(MESSAGE, message);
	    	}
	    	
			startActivity(myIntent);
			
			Transition.TransitionForward(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.localsaveerror, false);
		}
	}
	
	
	
	
	
	/*
	 * click handlers
	 */
	
	
	
	public void loginButtonClick(View view) {
		if (emailET != null && passwordET != null) {
	        String email = emailET.getText().toString();
	        String password = passwordET.getText().toString();
	
	        Actor user = ArinContext.getUser();
	        user.setEmail(email);
	        user.setPassword(password);
	           
	        if (!FieldValidation.isValidEmail(email)) {
	        	Popup.ShowErrorMessage(this, R.string.invalid_email, false);
	        } else if (!FieldValidation.isValidPassword(password)) {
	        	Popup.ShowErrorMessage(this, R.string.invalid_password, false);
	        } else if (!Network.isNetworkAvailable(this)) {
	        	goToNextScreen(getString(R.string.no_internet_offline_mode));
			} else {
				new Async_login(this, email, password);
			}
		}
	}

	private void offlineLoginButtonClick() {
		goToNextScreen(getString(R.string.offline_mode));
	}
	
	private void forgotPassword() {
		if (Network.isNetworkAvailable(this)) {
			new Async_forgot_password(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
	}
	
	private void registerButtonClick() {
		if (Network.isNetworkAvailable(this)) {
			new Async_register(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
	}
	
	public void openMenu(View view) {
		openOptionsMenu();
	}
}
