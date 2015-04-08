package activity;

import java.io.IOException;
import image.FishImage;
import image.MyImage;
import user.Actor;
import widget.Popup;
import common.MyIntent;
import common.Transition;
import fish.Category;
import fish.ChildAdder;
import fish.ChildDeleter;
import fish.ChildMover;
import fish.ChildRefresher;
import fish.Fish;
import fish.CategoryMVC;
import android.app.Activity;
import com.arin.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import arin.ArinContext;
import arin.LocalData;

/*
 * This activity displays the fish tree and allows management of them.
 */

public class NavigationScreen extends Activity implements ChildAdder, ChildMover, ChildDeleter, ChildRefresher {

	private CategoryMVC MVC = null;
	
	// this is used as a flag to reload the pic from the selected category (when returning from the gallery)
	public static Boolean reloadPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_screen);
		
		reloadPic = false;
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        // make sure fish pictures don't show up in gallery applications
        setUpNoMedia();
        
        // set up the carousel (fish tree)
        setUpScreen();
        
        // shows any message like if you are banned
        showBanMessage();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_screen, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
			case R.id.action_recognition:
				recognition();
				return true;
			case R.id.action_collect:
				goToNextStep();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        if (reloadPic) {
        	reloadPic = false;
        	MVC.reloadSelectedPic();
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
        	// this is for uploading an image
        	if (requestCode == MyIntent.SELECT_PICTURE) { 
        		Bitmap bitmap = MyImage.GetBitmapFromPath(this, data.getData(), Fish.UPLOAD_WIDTH, Fish.UPLOAD_HEIGHT);
        		
        		// upload image
        		MVC.uploadImage(bitmap);
        	} 
        }
    }
    

	
	
	
	

	
	
    /*
     * handlers for the settings menu
     */
    private void recognition() {
    	if (CategoryMVC.subRootCategory != null) {
    		Intent myIntent = new Intent(this, SpeciesScreen.class);
			myIntent.putExtra(SpeciesScreen.TYPE, SpeciesScreen.RECOGNITION);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(myIntent);
    	} else {
			Popup.ShowErrorMessage(this, R.string.select_one, false);
		}
    }
    private void goToNextStep() {
		if (CategoryMVC.subRootCategory != null) {
			Intent myIntent = new Intent(this, SpeciesScreen.class);
			myIntent.putExtra(SpeciesScreen.TYPE, SpeciesScreen.ALL_SPECIES);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(myIntent);
			
			Transition.TransitionForward(this);
		} else {
			Popup.ShowErrorMessage(this, R.string.select_one, false);
		}
    }

    
    
    
    
    
	private void setUpNoMedia() {
		try {
			FishImage.markAsNoMedia();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void setUpScreen() {
		
		// retrieve data from local cache
		LocalData localdata = ArinContext.getLocaldata();
		String category_data = localdata.getCategory_data();
		String species_data = localdata.getSpecies_data();
		
		MVC = new CategoryMVC(this);
		MVC.prepareData(category_data, species_data);
	}
	private void showBanMessage() {
		Actor user = ArinContext.getUser();
        if (user.isBanned()) {
        	String message = getString(R.string.are_banned).replace("_", Integer.toString(user.getRemainingBanDays(), 10));
        	Popup.ShowString(this, R.string.banned, message, false);
        }
	}
	public void displayTree() {
		MVC.displayTree();
	}


	

	
	

	@Override
	public void childAdderReturn(Fish fish) {
		MVC.addCategory((Category)fish);
		
		Category parent = ((Category)fish).getParent();
		if (parent.isSelected()){
			MVC.unSelect(parent);
		}
	}

	@Override
	public void childMoverReturn(Fish fish) {
		MVC.unselectRoot((Category)fish);
	}

	@Override
	public void childDeleterReturn(Fish fish) {
		Category parent = ((Category)fish).getParent();
		if (parent.isSelected()){
			MVC.unSelect(parent);
		}
	}

	@Override
	public void childRefresherReturn(Fish fish) {
		MVC.reloadName(fish);
	}
}
