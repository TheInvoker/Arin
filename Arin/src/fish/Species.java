package fish;

import http.Network;
import image.FishImage;
import image.SpeciesImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import location.Place;
import user.Actor;
import widget.Popup;
import common.Transition;
import activity.LocationScreen;
import activity.MapScreen;
import activity.SpeciesScreen;
import android.app.Activity;
import com.arin.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.LinearLayout;
import arin.ArinContext;
import async.fish.Async_delete_fish;
import async.fish.Async_move_fish;

/*
 * This is a specie you see in fish categories.
 */

public class Species extends Fish {
	
	private String resourceLink;
	private List<Place> locations = new ArrayList<Place>();
	
	public Species(int id, String name, int version, String resourceLink) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.resourceLink = resourceLink;
	}
	public String getResourceLink() {
		return resourceLink;
	}
	public void setResourceLink(String resourceLink) {
		this.resourceLink = resourceLink;
	}
	public void addLocation(Place location) {
		locations.add(location);
	}
	public List<Place> getLocations() {
		List<Place> newList = new ArrayList<Place>();
		Boolean canManage = ArinContext.getUser().canManageLocation();

		for(Place image : locations) {
			if (canManage || image.isApproved()) {
				newList.add(image);
			}
		}
		return newList;
	}
	
	

	
	
	@Override
	public Boolean isCategory() {
		return false;
	}
	
	@Override
	public List<Category> getCategoriesForMoving(Category category) {
		Category root = category.getRoot();
		List<Category> categories = Category.flattenCategories(root);
		categories.remove(category);
		return categories;
	}
	
	@Override
	public void setResourceField(EditText field) {
		field.setText(getResourceLink());
	}
	
	@Override
	public LinearLayout.LayoutParams getImageParams() {
		return getParams();
	}
	
	@Override
	public LinearLayout.LayoutParams getParams() {
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public String formatStats(Context context, int version, int approved_history, int unapproved_history, int unapproved_images, int approved_images, int unapproved_locations, int approved_locations, int species) {
		String str = context.getString(R.string.spestats).replace("%%", "%");
		return String.format(Locale.getDefault(), str, 
				version,
				approved_history, 
				unapproved_history,
				approved_images,
				unapproved_images,
				approved_locations,
				unapproved_locations);
	}

	
	
	/*
	 * The below functions determine if the option in the settings is available
	 */
	
	@Override
    public Boolean allowsSpecies() {
		return false;
	}
	@Override
	public Boolean allowsAllImages() {
		return true;
	}
	@Override
	public Boolean allowsUploadImage() {
		Actor user = ArinContext.getUser();
		return !(user.isBanned());
	}
	@Override
	public Boolean allowsLocations() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned());
	}
	@Override
	public Boolean allowsMap() {
		return true;
	}
	@Override
	public Boolean allowsAddFish() {
		return false;
	}
	@Override
    public Boolean allowsMove() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned() || !user.canManageFish());
	}
	@Override
    public Boolean allowsDelete() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned() || !user.canManageFish());
	}
	@Override
    public Boolean allowsEdit() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned());
	}
	@Override
    public Boolean allowsChangeRequest() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned() || !user.canManageHistory());
	}
	@Override
    public Boolean allowsHistory() {
		return !ArinContext.isOfflineMode();
	}
	@Override
    public Boolean allowsVersion() {
		return true;
	}
	

	
	
	
	/*
	 * The functions below handle the click event and event success functions of the settings
	 */
	
	@Override
	public void handleSpecies(Context context) {
	}
	
	@Override
	public void handleUploadImageSuccess(int pic_id, Bitmap bitmap, JSONObject obj, String elink, int locationId) throws IOException {
		String comment = "";
		if (obj.has(SpeciesScreen.cmmtKey)) {
			try {
				comment = obj.getString(SpeciesScreen.cmmtKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		FishImage image = new SpeciesImage(pic_id, getId(), false, bitmap.getByteCount(), comment, true, new Date(), getId(), elink, ArinContext.getUser().getRemainingBanDays());
		image.saveImageLocally(bitmap);
		bitmap.recycle();
		addImage(image);
		
		if (locationId > 0) {
			Double lat = null, lng = null;
			String addr = null;
			if (obj.has(SpeciesScreen.latKey)) {
				try {
					lat = obj.getDouble(SpeciesScreen.latKey);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (obj.has(SpeciesScreen.longKey)) {
				try {
					lng = obj.getDouble(SpeciesScreen.longKey);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (obj.has(SpeciesScreen.addrKey)) {
				try {
					addr = obj.getString(SpeciesScreen.addrKey);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (lat!=null && lng!=null && addr!=null) {
				Place place = new Place(locationId, addr, lat, lng, "", true, ArinContext.getUser().getId(), ArinContext.getUser().getRemainingBanDays());
				addLocation(place);
			}
		}
	}
	
	@Override
	public void handleLocation(Activity context) {
		Intent myIntent = new Intent(context, LocationScreen.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(myIntent);
		
		Transition.TransitionForward(context);
	}
	
	@Override
	public void handleMap(Activity context) {
		if (getLocations().size()==0) {
			Popup.ShowWarningMessage(context, R.string.no_locations, false);
		} else {
			Intent myIntent = new Intent(context, MapScreen.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(myIntent);
			
			Transition.TransitionForward(context);
		}
	}
	
	@Override
	public void handleAddChild(Activity context) {
	}
	
	@Override
	public Fish handleAddChildSuccess(Activity context, int fish_id, String name) {
		CategoryMVC.selectedCategory.addSpecies(this);
		CategoryMVC.selectedCategory.sortDirectChildSpecies();
		return this;
	}
	
	@Override
	public void handleMove(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			Category cat = CategoryMVC.root.getCategoryFromSpeciesId(getId());
			List<Category> categories = getCategoriesForMoving(cat);
	    	new Async_move_fish(context, this, categories);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	@Override
	public void handleMoveSuccess(Activity context, Category toCategory, String comment) {
		Category fromCategory = CategoryMVC.root.getCategoryFromSpeciesId(getId());
		fromCategory.getSpecies().remove(this);
		toCategory.getSpecies().add(this);
		toCategory.sortDirectChildSpecies();
		setVersion(getVersion()+1);
	}
	
	@Override
	public void handleDelete(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			new Async_delete_fish(context, Species.this);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	@Override
	public void handleDeleteSuccess(Activity context) {
		Category holder = CategoryMVC.root.getCategoryFromSpeciesId(getId());
		holder.getSpecies().remove(this);
	}
	
	@Override
	public void handleChangeRequestSuccess(Activity context, String newName, String newResource) {
		setVersion(getVersion()+1);
		setName(newName);
		setResourceLink(newResource);
	}
}
