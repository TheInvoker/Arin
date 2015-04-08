package history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import user.Actor;
import widget.Popup;
import common.Ban;
import common.MyDate;
import common.Vars;
import common.Views;
import fish.Fish;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import com.arin.R;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import arin.ArinContext;
import async.history.Async_approve_request;
import async.user.Async_ban_user;

/*
 * Handles the history object for categories and species.
 */

public abstract class History implements Ban {
	
	protected int id;
	protected int user_id;
	protected String user_email;
	protected Date date_modified;
	protected String comment;
	protected String change_type;
	protected String new_name;
	protected int ban_days_left;
	
	public int getId() {
		return id;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public Date getDate_modified() {
		return date_modified;
	}
	public void setDate_modified(Date date_modified) {
		this.date_modified = date_modified;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getChange_type() {
		return change_type;
	}
	public void setChange_type(String change_type) {
		this.change_type = change_type;
	}
	public String getNew_name() {
		return new_name;
	}
	public void setNew_name(String new_name) {
		this.new_name = new_name;
	}

	
	
	
	public int getBanUserId() {
		return user_id;
	}
	
	public int getBanDaysLeft() {
		return ban_days_left;
	}
	
	public void setBanDaysLeft(int days) {
		this.ban_days_left = days;
	}
	
	
	


	public abstract String getNew_resource_link();
	
	public abstract void setResourceField(TextView field, TextView field2);
	


	
	public static void displayHistory(Activity context, Fish fish, JSONArray array) {
		dialogHelper(context, fish, array, R.string.history, true);
	}
	
	public static void displayChangeRequsts(Activity context, Fish fish, JSONArray array) {
		dialogHelper(context, fish, array, R.string.requsts, false);
	}
	
	@SuppressLint("InflateParams")
	private static void dialogHelper(final Activity context, final Fish fish, JSONArray array, int title, final Boolean forHistory) {
		List<History> historyList = convertArrayToHistory(fish, array);
		
		if (historyList.size() == 0) {
			Popup.ShowWarningMessage(context, forHistory ? R.string.nohistory : R.string.nochange, false);
		} else {
		
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			final AlertDialog alert = builder.create();
			alert.setTitle(title);
			
	        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.dialog_history, null);
			TextView message = (TextView)dialoglayout.findViewById(R.id.message);
	        LinearLayout table = (LinearLayout)dialoglayout.findViewById(R.id.history_root);
	        
	        final Actor user = ArinContext.getUser();
	        if (user.canManageHistory()) {
	        	message.setText(forHistory ? R.string.to_ban : R.string.to_approve);
	        } else {
	        	message.setVisibility(View.GONE);
	        }
	        
	        int count = 0;
			for (final History item : historyList) {
					
				LinearLayout row = Views.getContainer(context, true, 7);
				row.setBackgroundColor(count % 2 == 0 ? Color.parseColor("#373737") : Color.parseColor("#494949"));
				
				TextView et;
				if (forHistory) {
					et = Views.getText(context, item.getChange_type());
					row.addView(et);
				}
				
				if (item.getChange_type().equals(Vars.history_update)) {
					et = Views.getText(context, context.getString(R.string.new_name) + item.getNew_name());
					row.addView(et);
					
					et = Views.getText(context, "");
					row.addView(et);
					
					TextView et2 = Views.getText(context, "");
					row.addView(et2);
					
					item.setResourceField(et, et2);
				}

				et = Views.getCommentText(context, item.getComment());
				row.addView(et);
				
				et = Views.getDateText(context, MyDate.dateToString(item.getDate_modified()));
				row.addView(et);
				
				if (user.canManageHistory()) {
					et = Views.getText(context, context.getString(R.string.email));
					row.addView(et);
					
					Views.makeTextViewHyperlink(et);
					
					et.setBackgroundResource(R.drawable.history_button_selector);
					et.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = new Intent(Intent.ACTION_SEND);
							i.setType("message/rfc822");
							i.putExtra(Intent.EXTRA_EMAIL, new String[]{item.getUser_email()});
							try {
							    context.startActivity(Intent.createChooser(i, context.getString(R.string.email)));
							} catch (android.content.ActivityNotFoundException ex) {
							    Toast.makeText(context, context.getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
							}
						}
					});
				
					row.setBackgroundResource(R.drawable.history_button_selector);
					row.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (forHistory) {
								if (user.isBanned()) {
									Toast.makeText(context, context.getString(R.string.your_banned), Toast.LENGTH_SHORT).show();
								} else {
									new Async_ban_user(context, item);
								}
							} else {
								new Async_approve_request(context, fish, item, alert);
							}
						}
					});
				}
				
				count += 1;
				table.addView(row);
			}

			alert.setView(dialoglayout);
			alert.show();
		}
	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static List<History> convertArrayToHistory(Fish fish, JSONArray array) {
		List<History> historyList = new ArrayList<History>();
		
		for(int i=0; i<array.length(); i+=1) {
			try {
				JSONObject obj = array.getJSONObject(i);
				History item = fish.isCategory() ? getCategoryHistory(obj) : getSpeciesHistory(obj);
				historyList.add(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return historyList;
	}
	
	private static CategoryHistory getCategoryHistory(JSONObject obj) throws JSONException {
		return new CategoryHistory(
			obj.getInt("id"),
			obj.getInt("user_id"),
			obj.getString("email"),
			MyDate.getDateFromSQLDate(obj.getString("date_modified")),
			obj.getString("comment"),
			obj.getString("type"),
			obj.getString("new_name"),
			obj.getInt("ban_days_left")
		);
	}
	
	private static SpeciesHistory getSpeciesHistory(JSONObject obj) throws JSONException {
		return new SpeciesHistory(
			obj.getInt("id"),
			obj.getInt("user_id"),
			obj.getString("email"),
			MyDate.getDateFromSQLDate(obj.getString("date_modified")),
			obj.getString("comment"),
			obj.getString("type"),
			obj.getString("new_name"),
			obj.getString("new_resource_link"),
			obj.getInt("ban_days_left")
		);
	}
}