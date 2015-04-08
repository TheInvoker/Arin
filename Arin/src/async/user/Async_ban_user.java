package async.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import user.Actor;
import widget.Popup;
import http.HttpTask;
import http.Network;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import common.Ban;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import arin.ArinContext;

/*
 * This is a async task that bans users
 */

public class Async_ban_user extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Ban ban;
	private int days;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "user/banuser.php";
	
	public Async_ban_user(Activity context, Ban ban) {
		this.context = context;
		this.ban = ban;
		
		askForBan();
	}
	
    private void askForBan() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.ban_user);
	    builder.setMessage(context.getString(R.string.currently_banned).replace("_", Integer.toString(ban.getBanDaysLeft(), 10)));
	    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	confirmAskForBan();
	        }
	    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
	    });
	    builder.show();
    }
    
    private void confirmAskForBan() {
    	Actor user = ArinContext.getUser();
    	
    	if (user.getId() == ban.getBanUserId()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.this_you).setMessage(R.string.you_sure);
		    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	showNumberPicker();
		        }
		    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        }
		    });
		    AlertDialog alert = builder.create();
		    alert.show();
		} else {
			showNumberPicker();
    	}
    }
    
    @SuppressLint("InflateParams")
	private void showNumberPicker() {
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View npView = inflater.inflate(R.layout.dialog_number_picker, null);
	    
    	final NumberPicker np = (NumberPicker)npView.findViewById(R.id.np);
    	np.setMinValue(0); 
    	np.setMaxValue(30); 
    	np.setValue(7);
        np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	    
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.ban_days)
        .setView(npView)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	days = np.getValue(); 
            	
            	getComment();
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        
        AlertDialog alert = builder.create();
	    alert.show();
    }
    
    
    
	@SuppressLint("InflateParams")
	private void getComment() {
        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_comment, null);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	String comment = commentET.getText().toString();
            	
            	progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            	progressDialog.setMessage(context.getString(R.string.banning));
            	progressDialog.setCancelable(false);
            	progressDialog.show();
        		
            	execute(comment);
            }
        };
        
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.ban_reason).setCancelable(true);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).setPositiveButton(R.string.ok, listener);
        
        builder.setView(dialoglayout);
		builder.show();
	}
    
    
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("ban_user_id", Integer.toString(ban.getBanUserId(), 10)));
		urlValues.add(new BasicNameValuePair("days", Integer.toString(days, 10)));
		urlValues.add(new BasicNameValuePair("ban_reason", arg0[0]));
		
		Pair<String, Integer> pair = Network.GetHTTPResponse(context.getString(R.string.arin_host), urlValues);
		
		return pair.first;
	}
	
	@Override
	protected void onPostExecute(String response) {
		if (response!=null) {
			try {
				JSONObject object = new JSONObject(response);
				int code = object.getInt(CODE);
				
				if (code==200) {
					worked();
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context, R.string.unexpected_error, false);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, false);
		}
		progressDialog.dismiss();
	}
	
	private void worked() {
		ban.setBanDaysLeft(days);
		
		Actor user = ArinContext.getUser();
		if (user.getId() == ban.getBanUserId()) {
			user.setBan_days(days);
			user.setBan_start_date(new Date());
		}
	}
}
