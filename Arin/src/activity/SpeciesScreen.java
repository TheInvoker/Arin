package activity;

import image.MyImage;
import android.location.Location;
import android.net.Uri;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import recognition.CompareResult;
import widget.ColorGrid;
import widget.Popup;
import common.Common;
import common.MyIntent;
import common.Point;
import fish.Category;
import fish.ChildAdder;
import fish.ChildDeleter;
import fish.ChildMover;
import fish.ChildRefresher;
import fish.Fish;
import fish.Species;
import fish.SpeciesMVC;
import fish.CategoryMVC;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import arin.ArinContext;
import async.fish.Async_add_fish;
import async.image.Async_image_clean;
import async.recognition.Async_recognition;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

/*
 * This activity displays species on the screen and allows management of them. Also has
 * a search and location filter.
 */

public class SpeciesScreen extends Activity implements ChildAdder, ChildMover, ChildDeleter, ChildRefresher, ConnectionCallbacks, OnConnectionFailedListener {

	private SpeciesMVC MVC = null;
	private ColorGrid colorgrid = null;
	private Category selectedfish = null;   // the selected fish
	private Menu menu = null;
	private ProgressDialog progressDialog = null;
	
	public Boolean paused = false;
	
	public final static int ALL_SPECIES = 0;
	public final static int SPECIES = 1;
	public final static int RECOGNITION = 2;
	public final static String TYPE = "type";
	
	public final static String latKey = "lat";
	public final static String longKey = "long";
	public final static String addrKey = "addr";
	public final static String cmmtKey = "comment";
	
	
	// this is used as a flag to reload the data (when returning from the location manager)
	public static Boolean locationChanged;
	
	
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_species_screen);
		
		locationChanged = false;
		colorgrid = new ColorGrid(this);
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
		// set up screen
        setUpScreen();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.species_screen, menu);
		
		this.menu = menu;
		
		// if opened directly from notifications
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			menu.removeItem(R.id.action_add);
		} else {
			int type = bundle.getInt(TYPE);
			
	        if (type != SpeciesScreen.SPECIES || ArinContext.isOfflineMode()) {
		 		menu.removeItem(R.id.action_add);
		 	}
	        
	        // activate the search filter on the action bar
	 		handleSearchFilter(menu);
		}

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
	        case R.id.action_refresh:
	        	MVC.setSearchText("");
	        	MVC.loadFirst(false);
	        	return true;
	        case R.id.action_add:
	        	handleAddSpecies();
	        	return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    paused = true;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    paused = false;
	    
	    // clear notifications
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Async_recognition.SCAN_COMPLETE_NOTIFICATION_ID);
	    
	    // reload species if location changed
	    if (locationChanged) {
	    	locationChanged = false;
	    	showSpecies();
	    }
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        MVC.clear();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
        	Bitmap bitmap = MyImage.GetBitmapFromPath(this, data.getData(), Fish.UPLOAD_WIDTH, Fish.UPLOAD_HEIGHT);
			if (bitmap == null) {
				Popup.ShowErrorMessage(this, R.string.image_error, requestCode == MyIntent.RECOGNITION);
			} else {
        		new Async_image_clean(this, requestCode, bitmap, data.getData());
			}
        } else {
        	if (requestCode == MyIntent.RECOGNITION) {
        		finish();
        	}
        }
    }
    
    public void imageCleanReturn(int requestCode, Bitmap bitmap, Uri uri) {
    	
    	// get a json object of its location
		JSONObject obj = new JSONObject();
    	try {
    		Point point = Common.GetLatLongFromImage(this, uri);
    		if (point != null) {
    			obj.put(latKey, point.getLatitude());
				obj.put(longKey, point.getLongitude());
				obj.put(addrKey, point.getAddress());
    		}
		} catch (JSONException e) {
			obj.remove(latKey);
			obj.remove(longKey);
			obj.remove(addrKey);
			e.printStackTrace();
		}
    	
    	
    	
		// this is for uploading an image
    	if (requestCode == MyIntent.SELECT_PICTURE) {

    		// preview it
    		showImage(bitmap, obj);
    		
		// this is for picking an image for image recognition
    	} else if (requestCode == MyIntent.RECOGNITION) { 
    		
    		// preview it
    		recognitionImagePicker(bitmap, obj);	
    	}	
    }
    
    @SuppressLint("InflateParams")
	private void showImage(final Bitmap bitmap, final JSONObject obj) {
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_image, null);
		
		final ImageView imageView = (ImageView) dialoglayout.findViewById(R.id.image_placeholder);
		
		final AlertDialog d = new AlertDialog.Builder(this)
        .setView(dialoglayout)
        .setTitle(R.string.image_preview)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	imageView.setImageDrawable(null);
            	bitmap.recycle();
            }
         })
        .setNeutralButton(R.string.redo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	imageView.setImageDrawable(null);
            	bitmap.recycle();
            	CategoryMVC.selectedFish.handleUploadImage(SpeciesScreen.this);
            }
         })
        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	imageView.setImageDrawable(null);
            	MVC.uploadImage(bitmap, obj);
            }
         })
        .create();

	    imageView.setImageBitmap(bitmap);
	    
	    d.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				imageView.setImageDrawable(null);
				bitmap.recycle();
			}
	    });
		
		d.show();
    }
    
    @SuppressLint("InflateParams")
	private void recognitionImagePicker(final Bitmap bitmap, final JSONObject obj) {
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_image, null);
		
		final ImageView imageView = (ImageView) dialoglayout.findViewById(R.id.image_placeholder);
		
		final AlertDialog d = new AlertDialog.Builder(this)
        .setView(dialoglayout)
        .setTitle(R.string.image_preview)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	imageView.setImageDrawable(null);
            	bitmap.recycle();
				finish();
            }
         })
        .setNeutralButton(R.string.redo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	imageView.setImageDrawable(null);
            	bitmap.recycle();
            	doRecognition();
            }
         })
        .setPositiveButton(R.string.scan, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	imageView.setImageDrawable(null);
            	List<Species> speciesList = Category.getAllSpecies(selectedfish);
            	new Async_recognition(SpeciesScreen.this, bitmap, speciesList, colorgrid, obj);
            }
         })
        .create();

	    imageView.setImageBitmap(bitmap);
	    
	    d.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				imageView.setImageDrawable(null);
				bitmap.recycle();
				finish();
			}
	    });
	    
		d.show();
    }

    



    @Override
    public boolean onSearchRequested() {
    	MenuItem searchMenuItem = menu.findItem(R.id.action_search);
    	searchMenuItem.expandActionView();
        return false;  // don't go ahead and show the search box
    }


    /*
     * handles the search filter
     */
    private void handleSearchFilter(Menu menu) {
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String text) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String text) {
				MVC.setSearchText(text);
				MVC.loadFirst(true);
				return false;
			}
	    });
    }
	
    private void setUpScreen() {
    	MVC = new SpeciesMVC(this);
    	
    	Bundle bundle = getIntent().getExtras();
    	if (bundle != null) {
        	int type = bundle.getInt(TYPE);
        	MVC.setType(type);
        	
    		buildGoogleApiClient();
    	} else {
    		finish();
    		
    		Intent myIntent = new Intent(this, LoginScreen.class);
    		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		startActivity(myIntent);
    	}
    }
    
	protected synchronized void buildGoogleApiClient() {
		progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(getString(R.string.gettinglocation));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
		mGoogleApiClient.connect();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, R.string.not_used_location, Toast.LENGTH_SHORT).show();
		setUpSpecies();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Provides a simple way of getting a device's location and is well suited for
		// applications that do not require a fine-grained location and that do not need location
		// updates. Gets the best and most recent location currently available, which may be null
		// in rare cases when a location is not available.
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			MVC.setLatitude(mLastLocation.getLatitude());
			MVC.setLongitude(mLastLocation.getLongitude());
			Toast.makeText(this, R.string.used_location, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.not_used_location, Toast.LENGTH_SHORT).show();
		}
		
		setUpSpecies();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Toast.makeText(this, R.string.not_used_location, Toast.LENGTH_SHORT).show();
		setUpSpecies();
	}
	
    /*
     * gets the right species to show
     */
	private void setUpSpecies() {
		int type = MVC.getType();
		
		if (type == ALL_SPECIES) { // view results
			selectedfish = CategoryMVC.subRootCategory;
			
			List<Species> species = Category.getAllSpecies(selectedfish);
			MVC.addRawData(species);
			
			showSpecies();
		} else if (type == SPECIES) { // view species for that category
			selectedfish = CategoryMVC.selectedCategory;

			List<Species> species = selectedfish.getSpecies();
			MVC.addRawData(species);
			
			showSpecies();
		} else { // image recognition
			selectedfish = CategoryMVC.subRootCategory;

			doRecognition();
		}
		
		progressDialog.dismiss();
	}
	
	public void doRecognition() {
		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.select_or_take));
		chooserIntent.putExtra
		(
		  Intent.EXTRA_INITIAL_INTENTS, 
		  new Intent[] { takePhotoIntent }
		);
		startActivityForResult(chooserIntent, MyIntent.RECOGNITION);
	}

	public void recognitionReturn(List<CompareResult> compareResults, JSONObject obj) {
		if (compareResults == null) {
			Popup.ShowErrorMessage(this, R.string.unexpected_error, true);
		} else {
			
			if (obj.has(latKey) && obj.has(longKey)) {
				try {
					MVC.setLatitude(obj.getDouble(latKey));
					MVC.setLongitude(obj.getDouble(longKey));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			MVC.addRecognitionData(compareResults);
			showSpecies();
		}
	}
	
	
	public void showSpecies() {
		MVC.displayData();
	}
	
	public void insertSpecie(Species specie) {
		MVC.insertSpecie(specie);
	}
	
	public void removeSpecie(Species specie) {
		MVC.removeSpecie(specie);
	}
	
	
	public void loadPrev(View view) {
		MVC.loadPrev();
	}
	public void loadNext(View view) {
		MVC.loadNext();
	}
	
	
	/*
	 * adds a new species
	 */
	private void handleAddSpecies() {
		new Async_add_fish(this, selectedfish, false);
	}

	
	
	
	@Override
	public void childAdderReturn(Fish fish) {
		insertSpecie((Species)fish);
	}

	@Override
	public void childMoverReturn(Fish fish) {
		MVC.moveFinish((Species)fish);
	}

	@Override
	public void childDeleterReturn(Fish fish) {
		removeSpecie((Species)fish);
	}

	@Override
	public void childRefresherReturn(Fish fish) {
		MVC.reloadName(fish);
	}
}
