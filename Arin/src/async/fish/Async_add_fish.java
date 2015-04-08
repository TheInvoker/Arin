package async.fish;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import common.FieldValidation;


import user.Actor;
import widget.Popup;
import fish.Category;
import fish.ChildAdder;
import fish.Fish;
import fish.Species;
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
 * This is a async task that adds a fish
 */

public class Async_add_fish extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Category fish;
	private String name;
	private String resource;
	private String comment;
	private Boolean forCategory;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/addfish.php";
	
	public Async_add_fish(Activity context, Category fish, Boolean forCategory) {
		this.context = context;
		this.fish = fish;
		this.forCategory = forCategory;
		
		handleAddFish();
	}
	
    @SuppressLint("InflateParams")
	public void handleAddFish() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.edit);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_fish_edit, null);
		
		final EditText nameET = (EditText)dialoglayout.findViewById(R.id.name);
		final EditText resourceET = (EditText)dialoglayout.findViewById(R.id.resource_link);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		resourceET.setVisibility(forCategory ? EditText.GONE : EditText.VISIBLE);
		
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	name = nameET.getText().toString();
            	resource = resourceET.getText().toString();
            	comment = commentET.getText().toString();
            	
            	if (!FieldValidation.isValidFishName(name)) {
            		Popup.ShowErrorMessage(context, R.string.invalid_name, false);
            	} else {
	        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
	            	progressDialog.setMessage(context.getString(R.string.adding));
	            	progressDialog.setCancelable(false);
	            	progressDialog.show();
	        		
	            	execute();
            	}
            }
        });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

		builder.setView(dialoglayout);
		builder.show();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		Actor user = ArinContext.getUser();
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", Integer.toString(fish.getId(), 10)));
		urlValues.add(new BasicNameValuePair("add_category", forCategory ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("name", name));
		urlValues.add(new BasicNameValuePair("resource", resource));
		urlValues.add(new BasicNameValuePair("comment", comment));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(user.getId(), 10)));
		
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
					JSONObject result = object.getJSONObject(RESPONSE);
					int fish_id = result.getInt("fish_id");
					worked(fish_id);
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
	
	private void worked(int fish_id) {
		Fish actingFish = forCategory ? fish : new Species(fish_id, name, 1, resource);
		Fish newfish = actingFish.handleAddChildSuccess(context, fish_id, name);
		((ChildAdder)context).childAdderReturn(newfish);
	}
}
