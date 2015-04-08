package activity;

import java.util.List;
import location.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import fish.Species;
import fish.CategoryMVC;
import com.arin.R;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

/*
 * This activity displays locations on the map of the selected species.
 */

public class MapScreen extends FragmentActivity implements LocationListener {
	
	private Species selectedfish = null;
	private GoogleMap map = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_screen);
		
		setUpScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {

		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setUpScreen() {
		selectedfish = (Species) CategoryMVC.selectedFish;	
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		// enable getting current location when GPS is active
		map.setMyLocationEnabled(true);

		
		// get locations and add to the map
		List<Place> locations = selectedfish.getLocations();
		for(int i=0; i<locations.size(); i+=1) {
			Place location = locations.get(i);
			if (location.isApproved()) {
			
				LatLng latlong = new LatLng(location.getLatitude(), location.getLongitude());
		        
				map.addMarker(new MarkerOptions()
	            .title(location.getAddress())
	            .snippet(location.getComment())
	            .position(latlong));
				
				if (i==0) {
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 5));
				}
			}
		}
		
		// allow to get current location
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 2500f, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2500f, this);
	}

	@Override
	public void onLocationChanged(android.location.Location arg0) {
		// change current position on map to where you are
		LatLng latlong = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 5));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}
}
