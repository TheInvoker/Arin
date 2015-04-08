package activity;

import widget.Popup;
import http.Network;
import location.Place;
import location.PlaceMVC;
import fish.Species;
import fish.CategoryMVC;
import android.app.Activity;
import com.arin.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import async.location.Async_edit_location;

/*
 * This activity displays the locations set on the fish and allows management of them.
 */

public class LocationScreen extends Activity {

	private PlaceMVC MVC = null;
	private Species selectedfish = null;  // keep track of the species you are seeing the locations from
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_screen);
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        setUpScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_screen, menu);
		
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
	        case R.id.action_add:
	        	addNewLocation();
	        	return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpScreen() {
		selectedfish = (Species) CategoryMVC.selectedFish;
		
		MVC = new PlaceMVC(this);
		MVC.setUpScreen(selectedfish);
	}
	
	private void addNewLocation() {
		if (Network.isNetworkAvailable(this)) {
			new Async_edit_location(this, selectedfish, null);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
	}
	
	public void addPlace(Place place) {
		MVC.addLocation(selectedfish, place);
	}
	
	public void reloadPlace(Place place) {
		MVC.reloadPlace(place);
	}
	
	public void removePlace(Place place) {
		MVC.removePlace(place);
	}
	
	public void setPlaceApproved(Place place) {
		MVC.setPlaceApproved(place);
	}
}
