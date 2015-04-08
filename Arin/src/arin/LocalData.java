package arin;

import org.json.JSONException;
import org.json.JSONObject;
import common.Vars;
import android.app.Activity;
import android.content.SharedPreferences;

/*
 * This handles the management of the local data cache.
 */

public class LocalData {

	private String category_data;
	private String species_data;
	
	public LocalData(Activity context) {
		SharedPreferences prefs = context.getPreferences(Activity.MODE_PRIVATE); 
		
		this.category_data = prefs.getString(Vars.category_data, "[]");
		this.species_data = prefs.getString(Vars.species_data, "[]");
	}

	public String getCategory_data() {
		return category_data;
	}
	public void setCategory_data(String category_data) {
		this.category_data = category_data;
	}
	public String getSpecies_data() {
		return species_data;
	}
	public void setSpecies_data(String species_data) {
		this.species_data = species_data;
	}

	


	

	


	
	public void loadFromJSON(Activity context, JSONObject result) throws JSONException {
		String category_data = result.getString(Vars.category_data);
		String species_data = result.getString(Vars.species_data);
		
		setCategory_data(category_data);
		setSpecies_data(species_data);
	}
	
	public Boolean saveData(Activity context) {
		SharedPreferences.Editor editor = context.getPreferences(Activity.MODE_PRIVATE).edit();

		editor.putString(Vars.category_data, getCategory_data());
		editor.putString(Vars.species_data, getSpecies_data());
		
		return editor.commit();
	}
}