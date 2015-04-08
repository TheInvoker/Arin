package fish;

import http.Network;
import image.FishImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import widget.ExpandableListAdapter;
import widget.Popup;
import common.MyIntent;
import activity.GalleryScreen;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import com.arin.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import arin.ArinContext;
import async.fish.Async_edit_fish;
import async.history.Async_get_history;

/*
 * this is the base fish class that handles what both a category and specie does.
 */

public abstract class Fish {
	
	protected int id;
	protected String name;
	protected int version;
	protected List<FishImage> images = new ArrayList<FishImage>();

	public static final int UPLOAD_WIDTH = 240;
	public static final int UPLOAD_HEIGHT = 180;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	public int getVersion() {
		return version;
	}
	protected void setVersion(int version) {
		this.version = version;
	}
	public List<FishImage> getImageList() {
		List<FishImage> newList = new ArrayList<FishImage>();
		Boolean canManage = ArinContext.getUser().canManageImage();

		for(FishImage image : images) {
			if (canManage || image.getApproved()) {
				newList.add(image);
			}
		}
		return newList;
	}
	public void addImage(FishImage image) {
		images.add(image);
	}
	protected FishImage getImageToShow() {
		List<FishImage> images = getImageList();
		for(FishImage image : images) {
			if (image.getIsMain()) {
				return image;
			}
		}
		return null;
	}
	protected int numberOfImagesToShow() {
		return getImageList().size();
	}

	
	


	public abstract Boolean isCategory();
	
	public abstract List<Category> getCategoriesForMoving(Category category);
	
	public abstract void setResourceField(EditText field);
	
	public abstract LinearLayout.LayoutParams getImageParams();
	
	public abstract LinearLayout.LayoutParams getParams();
	
	public abstract String formatStats(Context context, int version, int approved_history, int unapproved_history, int unapproved_images, int approved_images, int unapproved_locations, int approved_locations, int species);
	
	
	
	
	/*
	 * displays the options in a expandable list view
     */
	@SuppressLint("InflateParams")
    public void handleOptions(Activity context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog alert = builder.create();
		alert.setTitle(R.string.settings);
		alert.setCancelable(true);

        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_fish_settings, null);
		
		ExpandableListView expListView = (ExpandableListView) dialoglayout.findViewById(R.id.lvExp);
		Pair<List<String>, HashMap<String, List<String>>> data = prepareListData(context);
		List<String> listDataHeader = data.first;
		HashMap<String, List<String>> listDataChild = data.second;
		
		ExpandableListAdapter listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
		
        expListView.setOnChildClickListener(getClickListener(context, alert, listDataHeader, listDataChild));
        
		alert.setView(dialoglayout);
		alert.show();
    }	
	
    /*
     * Preparing the list data
     */
    private Pair<List<String>, HashMap<String, List<String>>> prepareListData(Context context) {
    	List<String> listDataHeader = new ArrayList<String>();
    	HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        List<String> specie = new ArrayList<String>();
        if (allowsSpecies()) specie.add(context.getString(R.string.action_species));
        
        List<String> image = new ArrayList<String>();
        if (allowsAllImages()) image.add(context.getString(R.string.action_more_images));
        if (allowsUploadImage()) image.add(context.getString(R.string.action_upload));
 
        List<String> location = new ArrayList<String>();
        if (allowsLocations()) location.add(context.getString(R.string.action_locations));
        if (allowsMap()) location.add(context.getString(R.string.action_map));
        
        List<String> structure = new ArrayList<String>();
        if (allowsAddFish()) structure.add(context.getString(R.string.action_add_child));
        if (allowsMove()) structure.add(context.getString(R.string.action_move)); 
        if (allowsDelete()) structure.add(context.getString(R.string.action_delete)); 
        
        List<String> change = new ArrayList<String>();
        if (allowsEdit()) change.add(context.getString(R.string.action_edit));
        if (allowsChangeRequest()) change.add(context.getString(R.string.action_change_requests)); 
        if (allowsHistory()) change.add(context.getString(R.string.action_history)); 
        if (allowsVersion()) change.add(context.getString(R.string.action_version));   
        
        settingsHelper(context, R.string.group_specie, specie, listDataHeader, listDataChild);
        settingsHelper(context, R.string.group_image, image, listDataHeader, listDataChild);
        settingsHelper(context, R.string.group_location, location, listDataHeader, listDataChild);
        settingsHelper(context, R.string.group_structure, structure, listDataHeader, listDataChild);
        settingsHelper(context, R.string.group_change, change, listDataHeader, listDataChild);
        
        return new Pair<List<String>, HashMap<String, List<String>>>(listDataHeader, listDataChild);
    }	
    
    private void settingsHelper(Context context, int nameID, List<String> group, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        if (group.size() > 0) {
        	String name = context.getString(nameID);
        	listDataHeader.add(name);
        	listDataChild.put(name, group);
        }
    }
	
	
    /*
     * below functions are for determining if option is allowed
     */
    
    public abstract Boolean allowsSpecies();
    public abstract Boolean allowsAllImages();
    public abstract Boolean allowsUploadImage();
    public abstract Boolean allowsLocations();
    public abstract Boolean allowsMap();
    public abstract Boolean allowsAddFish();
    public abstract Boolean allowsMove();
    public abstract Boolean allowsDelete();
    public abstract Boolean allowsEdit();
    public abstract Boolean allowsChangeRequest();
    public abstract Boolean allowsHistory();
    public abstract Boolean allowsVersion();
    
    /*
     * handles the click event
     */
	public OnChildClickListener getClickListener(final Activity context, final AlertDialog alert, final List<String> listDataHeader, final HashMap<String, List<String>> listDataChild) {
		return new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	String itemName = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
            	
            	if (itemName.equals(context.getString(R.string.action_species))) {
            		handleSpecies(context);
            	} else if (itemName.equals(context.getString(R.string.action_more_images))) {
            		handleMoreImages(context);
            	} else if (itemName.equals(context.getString(R.string.action_upload))) {
            		handleUploadImage(context);
            	} else if (itemName.equals(context.getString(R.string.action_locations))) {
            		handleLocation(context);
            	} else if (itemName.equals(context.getString(R.string.action_map))) {
            		handleMap(context); 
            	} else if (itemName.equals(context.getString(R.string.action_add_child))) {
            		handleAddChild(context);
            	} else if (itemName.equals(context.getString(R.string.action_move))) {
            		handleMove(context);
            	} else if (itemName.equals(context.getString(R.string.action_delete))) {
            		handleDelete(context);
            	} else if (itemName.equals(context.getString(R.string.action_edit))) {
            		handleEdit(context);
            	} else if (itemName.equals(context.getString(R.string.action_change_requests))) {
            		handleChangeRequest(context);
            	} else if (itemName.equals(context.getString(R.string.action_history))) {
            		handleHistory(context);
            	} else if (itemName.equals(context.getString(R.string.action_version))) {
            		handleVersion(context);
            	}

            	alert.cancel();
                return false;
            }
        };
	}
    
    
    /*
	 *	Functions below handle the click event for the settings that are common for both categories and species.
     */
	
	public abstract void handleSpecies(Context context);
    
	public void handleMoreImages(Activity context) {
		if (numberOfImagesToShow() > 0) {
			Intent myIntent = new Intent(context, GalleryScreen.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(myIntent);
		} else {
			Popup.ShowWarningMessage(context, R.string.no_images_to_show, false);
		}
	}
	
	public void handleUploadImage(Activity context) {
		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Intent chooserIntent = Intent.createChooser(pickIntent, context.getString(R.string.select_or_take));
		chooserIntent.putExtra
		(
		  Intent.EXTRA_INITIAL_INTENTS, 
		  new Intent[] { takePhotoIntent }
		);
		context.startActivityForResult(chooserIntent, MyIntent.SELECT_PICTURE);
	}
	
	public abstract void handleUploadImageSuccess(int pic_id, Bitmap bitmap, JSONObject obj, String elink, int locationId) throws IOException;
    
	public abstract void handleLocation(Activity context);
	
	public abstract void handleMap(Activity context);
	
	public abstract void handleAddChild(Activity context);
	
	public abstract Fish handleAddChildSuccess(Activity context, int fish_id, String name);
	
	public abstract void handleMove(Activity context);
	
	public abstract void handleMoveSuccess(Activity context, Category parent, String comment);
	
	public abstract void handleDelete(Activity context);
	
	public abstract void handleDeleteSuccess(Activity context);
	
	public void handleEdit(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			new Async_edit_fish(context, this);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	public void handleEditSuccess(Activity context, String name, String resource, String comment) {
		Popup.ShowWarningMessage(context, R.string.wait_for_approved, false);
	}

	public void handleChangeRequest(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			new Async_get_history(context, this, false);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	public abstract void handleChangeRequestSuccess(Activity context, String newName, String newResource);

	public void handleHistory(Activity context) {
		if (Network.isNetworkAvailable(context)) {
			new Async_get_history(context, this, true);
		} else {
			Popup.ShowErrorMessage(context, R.string.no_internet, false);
		}
	}
	
	public void handleVersion(Activity context) {
		Popup.ShowString(context, R.string.fishversion, Integer.toString(getVersion(), 10), false);
	}
}
