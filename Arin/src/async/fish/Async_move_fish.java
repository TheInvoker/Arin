package async.fish;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import user.Actor;
import widget.Popup;
import fish.Category;
import fish.ChildMover;
import fish.Fish;
import http.HttpTask;
import http.Network;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import arin.ArinContext;

/*
 * This is a async task that moves a fish
 */

public class Async_move_fish extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Category parent;
	private Fish fish;
	private String comment;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/movefish.php";
	
	public Async_move_fish(Activity context, Fish fish, List<Category> categories) {
		this.context = context;
		this.fish = fish;
		
		getNewParent(categories);
	}
	
	private void getNewParent(final List<Category> categories) {
    	final String[] names = new String[categories.size()];
    	for(int i=0; i<categories.size(); i+=1) {
    		names[i] = categories.get(i).getName();
    	}
    	
    	AlertDialog ad = new AlertDialog.Builder(context)
    	.setTitle(R.string.new_parent)
    	.setItems(names, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	parent = categories.get(which);
	            getComment();
	        }
		})
    	.create();
    	ad.show();
	}
	
	@SuppressLint("InflateParams")
	private void getComment() {
        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_comment, null);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            	progressDialog.setMessage(context.getString(R.string.moving));
            	progressDialog.setCancelable(false);
            	progressDialog.show();
            	
            	comment = commentET.getText().toString();
            	execute();
            }
        };
        
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_comment).setCancelable(true);

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
		
		Actor user = ArinContext.getUser();
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("parent_id", Integer.toString(parent.getId(), 10)));
		urlValues.add(new BasicNameValuePair("comment", comment));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(user.getId(), 10)));
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("version", Integer.toString(fish.getVersion(), 10)));
		
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
		fish.handleMoveSuccess(context, parent, comment);
		((ChildMover)context).childMoverReturn(fish);
	}
}
