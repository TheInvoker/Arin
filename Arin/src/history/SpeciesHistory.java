package history;

import java.util.Date;
import common.FieldValidation;
import common.Views;
import com.arin.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * Handles the history object for species.
 */

public class SpeciesHistory extends History {

	private String new_resource_link;
	
	public SpeciesHistory(int id, int user_id, String user_email, Date date_modified, String comment, String change_type, String new_name, String new_resource_link, int ban_days_left) {
		this.id = id;
		this.user_id = user_id;
		this.user_email = user_email;
		this.date_modified = date_modified;
		this.comment = comment;
		this.change_type = change_type;
		this.new_name = new_name;
		this.new_resource_link = FieldValidation.fixURL(new_resource_link);
		this.ban_days_left = ban_days_left;
	}
	
	@Override
	public String getNew_resource_link() {
		return new_resource_link;
	}
	
	public void setNew_resource_link(String new_resource_link) {
		this.new_resource_link = new_resource_link;
	}
	
	@Override
	public void setResourceField(TextView field, TextView field2) {
		final String url = getNew_resource_link();
		final Context context = field.getContext();
		
		field.setText(context.getString(R.string.new_resource));
		field2.setText(url);
		
		Views.makeTextViewHyperlink(field2);
		
		field2.setBackgroundResource(R.drawable.history_button_selector);
		field2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!url.equals("")) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					context.startActivity(browserIntent);
				}
			}
		});
	}
}
