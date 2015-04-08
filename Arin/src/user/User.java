package user;

import android.app.Activity;

/*
 * Regular user class functions. 
 */

public class User extends Actor {

	public User(Activity context) {
		loadValues(context);
	}
	
	@Override
	public Boolean canManageImage() {
		return false;
	}
	
	@Override
	public Boolean canManageLocation() {
		return false;
	}
	
	@Override
	public Boolean canManageHistory() {
		return false;
	}
	
	@Override
	public Boolean canSeeCounts() {
		return false;
	}
	
	@Override
	public Boolean canManageFish() {
		return false;
	}
}
