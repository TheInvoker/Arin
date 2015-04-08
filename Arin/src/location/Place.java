package location;

import common.Ban;

/*
 * Class to manage species locations.
 */

public class Place implements Ban {

	private int id;
	private String address;
	private double latitude;
	private double longitude;
	private String comment;
	private Boolean approved;
	private int user_id;
	private int ban_days_left;
	
	public Place(int id, String address, double latitude, double longitude, String comment, Boolean approved, int user_id, int ban_days_left) {
		this.id = id;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.comment = comment;
		this.approved = approved;
		this.user_id = user_id;
		this.ban_days_left = ban_days_left;
	}

	public int getId() {
		return id;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean isApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	
	
	@Override
	public int getBanUserId() {
		return user_id;
	}

	@Override
	public int getBanDaysLeft() {
		return ban_days_left;
	}
	
	@Override
	public void setBanDaysLeft(int days) {
		ban_days_left = days;
	}
}
