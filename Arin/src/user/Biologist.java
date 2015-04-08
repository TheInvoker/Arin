package user;

import android.app.Activity;

/*
 * Biologist class functions. 
 */

public class Biologist extends Actor {

	public Biologist(Activity context) {
		loadValues(context);
	}
	
	@Override
	public Boolean canManageImage() {
		return true;
	}
	
	@Override
	public Boolean canManageLocation() {
		return true;
	}
	
	@Override
	public Boolean canManageHistory() {
		return true;
	}
	
	@Override
	public Boolean canSeeCounts() {
		return true;
	}
	
	@Override
	public Boolean canManageFish() {
		return true;
	}
}
