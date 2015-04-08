package activity;

import common.Common;
import common.Transition;
import http.Network;
import widget.Popup;
import android.app.Activity;
import com.arin.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import arin.ArinContext;
import async.image.Async_clear_images;
import async.user.Async_edit_profile;

public class SettingsScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_screen);
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
	 	if (ArinContext.isOfflineMode()) {
	 		findViewById(R.id.profileeditLine).setVisibility(View.GONE);
	 		findViewById(R.id.profileedit).setVisibility(View.GONE);
	 	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void goToEditProfile(View view) {
		if (Network.isNetworkAvailable(this)) {
			new Async_edit_profile(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
    }
	
	public void goToClearCache(View view) {
    	new Async_clear_images(this);
	}
	
	public void goToHelp(View view) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.my_email)});
		try {
		    startActivity(Intent.createChooser(i, getString(R.string.help_email)));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void goToRate(View view) {
		Common.openRate(this);
	}
	
	public void goToAbout(View view) {
    	Intent myIntent = new Intent(this, AboutScreen.class);
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(myIntent);
		
		Transition.TransitionForward(this);
    }
}
