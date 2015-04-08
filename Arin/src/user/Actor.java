package user;

import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.SharedPreferences;
import arin.ArinContext;
import common.MyDate;
import common.Vars;

/*
 * Abstract user.
 */

public abstract class Actor {

	protected int id;
	protected String name;
	protected String email;
	protected String password;
	protected Date ban_start_date;
	protected int ban_days;
	private Boolean auto_login;
	
	protected static final int OFFLINE_ID = -1;

	protected void loadValues(Activity context) {
		SharedPreferences prefs = context.getPreferences(Activity.MODE_PRIVATE); 
		
		this.id = OFFLINE_ID;
	    this.name = prefs.getString(Vars.username, null);
	    this.email = prefs.getString(Vars.email, null);
	    this.password = prefs.getString(Vars.password, null);
	    this.ban_start_date = MyDate.getDateFromSQLDate(prefs.getString(Vars.ban_date, "2000-01-01 01:01:01"));
	    this.ban_days = prefs.getInt(Vars.ban_days, 0);
	    this.auto_login = prefs.getBoolean(Vars.autologin, false);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getBan_start_date() {
		return ban_start_date;
	}
	public void setBan_start_date(Date ban_start_date) {
		this.ban_start_date = ban_start_date;
	}
	public int getBan_days() {
		return ban_days;
	}
	public void setBan_days(int ban_days) {
		this.ban_days = ban_days;
	}
	public Boolean isHollow() {
		return id == OFFLINE_ID;
	}
	public Boolean isAutoLogin() {
		return auto_login;
	}
	public void setAutoLogin(Boolean auto_login) {
		this.auto_login = auto_login;
	}
	
	/*
	 * gets amount of days until you are not banned anymore
	 */
	public int getRemainingBanDays() {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(getBan_start_date());  
		cal.add(Calendar.DATE, getBan_days());
		
		Date ban_end = cal.getTime();
		Date now = new Date();
		
		if (now.equals(ban_end) || now.after(ban_end)) {
			return 0;
		}
		
		long ban_end_time = ban_end.getTime();
		long now_time = now.getTime();
		long diffTime = ban_end_time - now_time;
		float diffDays = (float)diffTime / (1000 * 60 * 60 * 24);
		return (int) Math.ceil(diffDays);
	}

	/*
	 * checks if you are banned
	 */
	public Boolean isBanned() {
		return getRemainingBanDays() > 0;
	}
	
	
	
	/*
	 * Functions that convert the user to another type of user
	 */
	
	private Actor asBiologist(Activity context) {
		Actor biologist = new Biologist(context);
		fillDetails(biologist);
		return biologist;
	}
	
	private void fillDetails(Actor actor) {
		actor.setId(getId());
		actor.setName(getName());
		actor.setEmail(getEmail());
		actor.setPassword(getPassword());
		actor.setBan_start_date(getBan_start_date());
		actor.setBan_days(getBan_days());
		actor.setAutoLogin(isAutoLogin());
	}
	
	
	
	
	
	
	public void loadFromJSON(Activity context, JSONObject result) throws JSONException {
		int id = result.getInt(Vars.id);
		String username = result.getString(Vars.username);
		Date ban_start_date = MyDate.getDateFromSQLDate(result.getString(Vars.ban_date));
		int ban_days = result.getInt(Vars.ban_days);

		setId(id);
		setName(username);
		setBan_start_date(ban_start_date);
		setBan_days(ban_days);
		
		int roldId = result.getInt(Vars.roleId);
		if (roldId == 2) {
			ArinContext.setUser(asBiologist(context));
		}
	}
	
	public Boolean saveData(Activity context) {
		SharedPreferences.Editor editor = context.getPreferences(Activity.MODE_PRIVATE).edit();
		
		editor.putString(Vars.username, getName());
		editor.putString(Vars.email, getEmail());
		editor.putString(Vars.password, getPassword());
		editor.putString(Vars.ban_date, MyDate.dateToSQLString(getBan_start_date()));
		editor.putInt(Vars.ban_days, getBan_days());
		editor.putBoolean(Vars.autologin, isAutoLogin());
		
		return editor.commit();
	}
	
	
	
	

	public abstract Boolean canManageImage();
	
	public abstract Boolean canManageLocation();
	
	public abstract Boolean canManageHistory();
	
	public abstract Boolean canSeeCounts();
	
	public abstract Boolean canManageFish();
}
