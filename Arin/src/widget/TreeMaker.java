package widget;

import fish.Category;
import fish.Fish;
import fish.Species;
import image.CategoryImage;
import image.FishImage;
import image.SpeciesImage;
import java.util.ArrayList;
import java.util.List;
import location.Place;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.SparseIntArray;
import common.MyDate;

/*
 * Generates the category object which is the root of the whole fish tree.
 */

public final class TreeMaker {
	
	private Context context;
	private final int ROOT_PARENT_ID = 0;
	private SparseIntArray parentMap = new SparseIntArray();
	
	public TreeMaker(Context context) {
		this.context = context;
	}
	
	public List<Category> getCategories(String category_data, String species_data) {
		try {
			JSONArray category_data_array = new JSONArray(category_data);
			JSONArray species_data_array = new JSONArray(species_data);
			return getCategoryList(category_data_array, species_data_array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Category getRoot(List<Category> catList) {
		return ConvertToTree(catList);
	}
	

	
	private List<Category> getCategoryList(JSONArray cat_array, JSONArray spc_array) throws JSONException {
		List<Category> catList = new ArrayList<Category>();
		for(int i=0; i<cat_array.length(); i+=1) {
			JSONObject obj = cat_array.getJSONObject(i);
			Category fishCategory = new Category(
				context, 
				obj.getInt("id"), 
				obj.getString("name"), 
				obj.getInt("version"),
				null
			);
			catList.add(fishCategory);
			loadImages(fishCategory, obj, true);
			parentMap.put(fishCategory.getId(), obj.getInt("parent_id"));
		}
		
		List<Fish> spcList = new ArrayList<Fish>();
		for(int i=0; i<spc_array.length(); i+=1) {
			JSONObject obj = spc_array.getJSONObject(i);
			Species fishSpecie = new Species(
				obj.getInt("id"), 
				obj.getString("name"), 
				obj.getInt("version"),
				obj.getString("resource_link")
			);
			spcList.add(fishSpecie);
			loadImages(fishSpecie, obj, false);
			loadLocations(fishSpecie, obj);
			
			for(Fish category : catList) {
				if (category.getId()==obj.getInt("category_id")) {
					((Category)category).addSpecies(fishSpecie);
					break;
				}
			}
		}

		for(Fish category : catList) {
			((Category)category).sortDirectChildSpecies();
		}

		return catList;
	}

	private static void loadImages(Fish fish, JSONObject object, Boolean isNode) throws JSONException {
		JSONArray imageints = object.getJSONArray("images");
		for(int i=0; i<imageints.length(); i+=1) {
			JSONObject obj = imageints.getJSONObject(i);
			FishImage image = isNode ? getCategoryImage(obj, fish) : getSpeciesImage(obj, fish);
			fish.addImage(image);
		}
	}
	
	private static void loadLocations(Species fish, JSONObject object) throws JSONException {
		JSONArray locations = object.getJSONArray("locations");
		for(int i=0; i<locations.length(); i+=1) {
			JSONObject obj = locations.getJSONObject(i);
			fish.addLocation(new Place(
				obj.getInt("id"),
				obj.getString("address"),
				obj.getDouble("latitude"),
				obj.getDouble("longitude"),
				obj.getString("comment"),
				obj.getInt("approved")==1,
				obj.getInt("user_id"),
				obj.getInt("ban_days_left")
			));
		}
	}
	
	private static CategoryImage getCategoryImage(JSONObject obj, Fish fish) throws JSONException {
		JSONObject info = obj.getJSONObject("info");
		
		CategoryImage image = new CategoryImage(
			obj.getInt("id"),
			fish.getId(),
			obj.getInt("main")==1,
			info.getInt("filesize"),
			obj.getString("comment"),
			obj.getInt("approved")==1,
			MyDate.getDateFromSQLDate(obj.getString("date_added")),
			obj.getInt("user_id"),
			info.getString("elink"),
			obj.getInt("ban_days_left")
		);
		
		return image;
	}
	
	private static SpeciesImage getSpeciesImage(JSONObject obj, Fish fish) throws JSONException {
		JSONObject info = obj.getJSONObject("info");
		
		SpeciesImage image = new SpeciesImage(
			obj.getInt("id"),
			fish.getId(),
			obj.getInt("main")==1,
			info.getInt("filesize"),
			obj.getString("comment"),
			obj.getInt("approved")==1,
			MyDate.getDateFromSQLDate(obj.getString("date_added")),
			obj.getInt("user_id"),
			info.getString("elink"),
			obj.getInt("ban_days_left")
		);
		
		return image;
	}
	

	
	private Category ConvertToTree(List<Category> categoryList) {
		Category root = null;
		
		for(Category fc1 : categoryList) {
			Category c1 = (Category) fc1;
			if (parentMap.get(c1.getId()) == ROOT_PARENT_ID) {
				root = c1;
			} else {
				for(Category fc2 : categoryList) {
					Category c2 = (Category) fc2;
					if (c1.getId()!=c2.getId() && c2.getId() == parentMap.get(c1.getId())) {
						c2.addCategory(c1);
						c1.setParent(c2);
						break;
					}
				}
			}
		}
		
		for(Category fc : categoryList) {
			((Category)fc).sortDirectChildCategories();
		}
		
		return root;
	}
}
