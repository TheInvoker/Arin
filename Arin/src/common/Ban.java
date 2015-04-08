package common;

public interface Ban {
	
	public int getBanUserId();
	
	public int getBanDaysLeft();
	
	public void setBanDaysLeft(int days);
}
