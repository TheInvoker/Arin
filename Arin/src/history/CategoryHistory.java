package history;

import java.util.Date;
import android.widget.TextView;

/*
 * Handles the history object for categories.
 */

public class CategoryHistory extends History {
	
	public CategoryHistory(int id, int user_id, String user_email, Date date_modified, String comment, String change_type, String new_name, int ban_days_left) {
		this.id = id;
		this.user_id = user_id;
		this.user_email = user_email;
		this.date_modified = date_modified;
		this.comment = comment;
		this.change_type = change_type;
		this.new_name = new_name;
		this.ban_days_left = ban_days_left;
	}
	
	@Override
	public String getNew_resource_link() {
		return "";
	}
	
	@Override
	public void setResourceField(TextView field, TextView field2) {
		field.setVisibility(TextView.GONE);
		field2.setVisibility(TextView.GONE);
	}
}
