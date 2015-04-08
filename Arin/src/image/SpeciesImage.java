package image;

import java.util.Date;

/*
 * This is a class for images for a species.
 */

public class SpeciesImage extends FishImage {

	public static final String tablename = "species";
	
	public SpeciesImage(int id, int fish_id, Boolean isMain, int fileSize, String comment, Boolean approved, Date date_added, int user_id, String elink, int ban_days_left) {
		this.id = id;
		this.fish_id = fish_id;
		this.isMain = isMain;
		this.fileSize = fileSize;
		this.comment = comment;
		this.approved = approved;
		this.date_added = date_added;
		this.user_id = user_id;
		this.elink = elink;
		this.ban_days_left = ban_days_left;
	}
	
	@Override
	public String getTableName() {
		return tablename;
	}
}
