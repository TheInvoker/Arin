package arin;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import user.Actor;

/*
 * Current context for the login session.
 */

public class ArinContext {

	private static Actor user = null;
	private static LocalData localdata = null;
	
	public static Actor getUser() {
		return user;
	}
	
	public static void setUser(Actor user) {
		ArinContext.user = user;
	}
	
	public static LocalData getLocaldata() {
		return localdata;
	}
	
	public static void setLocaldata(LocalData localdata) {
		ArinContext.localdata = localdata;
	}
	
	public static Boolean isOfflineMode() {
		return getUser().isHollow();
	}
	
	public static void loadUserFromJSON(Activity context, JSONObject result) throws JSONException {
		getUser().loadFromJSON(context, result);
	}
	
	public static void loadDataFromJSON(Activity context, JSONObject result) throws JSONException {
		getLocaldata().loadFromJSON(context, result);
	}
	
	public static Boolean saveUserData(Activity context) {
		return getUser().saveData(context);
	}
	
	public static Boolean saveMainData(Activity context) {
		return getLocaldata().saveData(context);
	}
}
