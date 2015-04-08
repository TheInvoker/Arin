package activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import http.Network;
import widget.Popup;
import common.AppRater;
import common.Transition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import com.arin.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import arin.ArinContext;
import arin.LocalData;
import async.fish.Async_get_database;

public class MenuScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_screen);
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
	 	if (ArinContext.isOfflineMode()) {
	 		findViewById(R.id.whatsthatfishline).setVisibility(View.GONE);
	 		findViewById(R.id.whatsthatfish).setVisibility(View.GONE);
	 	}
	 	
	 	Bundle bundle = getIntent().getExtras();
	 	if (bundle != null && bundle.containsKey(LoginScreen.MESSAGE)) {
	 		String message = bundle.getString(LoginScreen.MESSAGE);
	 		Popup.ShowWarningMessage(this, message, false);
	 	}
	 	
	 	AppRater.prepareVars(this);
	 	AppRater.app_launched(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_screen, menu);
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
	
	public void goToNextScreen(View view) {
		if (ArinContext.getLocaldata() == null) {
			ArinContext.setLocaldata(new LocalData(this));
		}
		
		if (ArinContext.isOfflineMode()) {
			goToNextScreenHelper();
		} else if (!Network.isNetworkAvailable(this)) {
			showMessageAndContinue(R.string.no_internet_using_saved);
		} else {
			new Async_get_database(this);
		}
	}
	
	public void goToNextScreenHelper() {
		if (ArinContext.saveMainData(this)) {
	    	Intent myIntent = new Intent(this, NavigationScreen.class);
	    	myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(myIntent);
			
			Transition.TransitionForward(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.localsaveerror, false);
		}
	}

	public void goToThreads(View view) {
    	Intent myIntent = new Intent(this, AllThreadsScreen.class);
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(myIntent);
		
		Transition.TransitionForward(this);
    }
	
	public void goToSettings(View view) {
    	Intent myIntent = new Intent(this, SettingsScreen.class);
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(myIntent);
		
		Transition.TransitionForward(this);
    }
	

	
	
	
	
	
	
	
	public void showMessageAndContinue(int message) {
		showMessageAndContinue(getString(message));
	}
	
	public void showMessageAndContinue(String message) {
		new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog))
	    .setTitle(R.string.warning_title)
	    .setMessage(message)
	    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	goToNextScreenHelper();
	        }
	     })
	     .setCancelable(false)
	    .show();
	}
	
	
	
	
	
	public static class AdFragment extends Fragment {
		
        private AdView mAdView;

        public AdFragment() {
        }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_ad, container, false);
	    }

	    @Override
	    public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
	    }
	    
        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }
	}
}
