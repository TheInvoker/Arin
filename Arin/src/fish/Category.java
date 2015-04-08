package fish;

import http.Network;
import image.CategoryImage;
import image.FishImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import user.Actor;
import widget.Popup;
import activity.SpeciesScreen;
import android.app.Activity;
import com.arin.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.LinearLayout;
import arin.ArinContext;
import async.fish.Async_add_fish;
import async.fish.Async_delete_fish;
import async.fish.Async_move_fish;

/*
 * This is a fish category you see in the fish tree.
 */

public class Category extends Fish {

	private Category parent;
	private Boolean selected = false;
	private List<Category> categories = new ArrayList<Category>();
	private List<Species> speciesList = new ArrayList<Species>();

	private final int IMAGE_HEIGHT = 200;
	
	public Category (Context context, int id, String name, int version, Category parent) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.parent = parent;
	}
	
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public Boolean isSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public Boolean isRoot() {
		return getParent() == null;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public void addCategory(Category category) {
		categories.add(category);
	}
	public List<Species> getSpecies() {
		return speciesList;
	}
	public void addSpecies(Species species) {
		speciesList.add(species);
	}
	public Category getRoot() {
		if (getParent()==null) {
			return this;
		}
		return getParent().getRoot();
	}
	public Category getCategoryFromId(int id) {
		List<Category> categories = flattenCategories(this);
		for(Category c : categories) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}
	public Category getCategoryFromSpeciesId(int id) {
		List<Category> categories = flattenCategories(this);
		for(Category c : categories) {
			List<Species> species = c.getSpecies();
			for(Species s : species)
			if (s.getId() == id) {
				return c;
			}
		}
		return null;
	}
	public Species getSpeciesFromId(int id) {
		List<Category> categories = flattenCategories(this);
		for(Category c : categories) {
			List<Species> species = c.getSpecies();
			for(Species s : species)
			if (s.getId() == id) {
				return s;
			}
		}
		return null;
	}
	
	
	

	public void sortDirectChildCategories() {
		Collections.sort(getCategories(), new Comparator<Category>(){
		    public int compare(Category s1, Category s2) {
		    	return s1.getName().compareTo(s2.getName());
		    }
		});
	}
	public void sortDirectChildSpecies() {
		Collections.sort(getSpecies(), new Comparator<Species>(){
		    public int compare(Species s1, Species s2) {
		    	return s1.getName().compareTo(s2.getName());
		    }
		});
	}
	

	
	

	
	
	/*
	 * gets all categories recursively
	 */
	public static List<Category> flattenCategories(Category root) {
		List<Category> categories = new ArrayList<Category>();
		categories.add(root);
		for(Category category : root.getCategories()) {
			categories.addAll(flattenCategories(category));
		}
		return categories;
	}
	
	/*
	 * gets all species recursively
	 */
	public static List<Species> getAllSpecies(Category root) {
		List<Category> fishes = flattenCategories(root);
		List<Species> species = new ArrayList<Species>();
		for(Category category : fishes) {
			species.addAll(category.getSpecies());
		}
		return species;
	}
	
	
	
	
	
	
	@Override
	public Boolean isCategory() {
		return true;
	}	
	
	@Override
	public List<Category> getCategoriesForMoving(Category category) {
		Category root = category.getRoot();
		List<Category> categories = flattenCategories(root);
		categories.remove(category);
		categories.remove(category.getParent());
		return categories;
	}
	
	@Override
	public void setResourceField(EditText field) {
		field.setVisibility(EditText.GONE);
	}
	
	@Override
	public LinearLayout.LayoutParams getImageParams() {
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, IMAGE_HEIGHT);
	}
	
	@Override
	public LinearLayout.LayoutParams getParams() {
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public String formatStats(Context context, int version, int approved_history, int unapproved_history, int unapproved_images, int approved_images, int unapproved_locations, int approved_locations, int species) {
		String str = context.getString(R.string.catstats).replace("%%", "%");
		return String.format(Locale.getDefault(), str, 
				version,
				approved_history, 
				unapproved_history,
				approved_images,
				unapproved_images,
				species);
	}
	
	
	
	
	
	/*
	 * The below functions determine if the option in the settings is available
	 */
	
	@Override
    public Boolean allowsSpecies() {
		return true;
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
		return false;
	}
	@Override
	public Boolean allowsMap() {
		return false;
	}
	@Override
	public Boolean allowsAddFish() {
		Actor user = ArinContext.getUser();
		return !(ArinContext.isOfflineMode() || user.isBanned() || !user.canManageFish());
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
		Intent myIntent = new Intent(context, SpeciesScreen.class);
		myIntent.putExtra(SpeciesScreen.TYPE, SpeciesScreen.SPECIES);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(myIntent);
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
		
		Actor user = ArinContext.getUser();
		FishImage image = new CategoryImage(pic_id, getId(), false, bitmap.getByteCount(), comment, true, new Date(), user.getId(), elink, ArinContext.getUser().getRemainingBanDays());
		image.saveImageLocally(bitmap);
		bitmap.recycle();
		addImage(image);
	}
	
	@Override
	public void handleLocation(Activity context) {
	}
	
	@Override
	public void handleMap(Activity context) {
	}
	
	@Override
	public void handleAddChild(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			new Async_add_fish(context, this, true);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	@Override
	public Fish handleAddChildSuccess(Activity context, int fish_id, String name) {
		Category new_cat = new Category(context, fish_id, name, 1, this);
		addCategory(new_cat);
		sortDirectChildCategories();
		return new_cat;
	}
	
	@Override
	public void handleMove(Activity context) {
		if (!Network.isNetworkAvailable(context)) {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		} else if (isRoot()) {
			Popup.ShowErrorMessage(context, R.string.cantmodifyroot, false);
		} else {
			List<Category> categories = getCategoriesForMoving(this);
	    	new Async_move_fish(context, this, categories);
		}
	}
	
	@Override
	public void handleMoveSuccess(Activity context, Category parent, String comment) {
		getParent().getCategories().remove(this);
		parent.addCategory(this);
		setParent(parent);
		parent.sortDirectChildCategories();
		setVersion(getVersion()+1);
	}

	@Override
	public void handleDelete(Activity context) {
		if (!Network.isNetworkAvailable(context)) {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		} else if (isRoot()) {
			Popup.ShowErrorMessage(context, R.string.cantmodifyroot, false);
		} else {
			new Async_delete_fish(context, this);
		}
	}
	
	@Override
	public void handleDeleteSuccess(Activity context) {
		Category parent = getParent();
		parent.getCategories().remove(this);
	}

	@Override
	public void handleChangeRequestSuccess(Activity context, String newName, String newResource) {
		setVersion(getVersion()+1);
		setName(newName);
	}
}