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
 * This is a async task that edits fish
 */

public class Async_edit_fish extends AsyncTask<String, Void, String> implements HttpTask {

	private Activity context;
	private Fish fish;
	private String name;
	private String resource;
	private String comment;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "fish/editfish.php";
	
	public Async_edit_fish(Activity context, Fish fish) {
		this.context = context;
		this.fish = fish;
		
		handleEdit();
	}
	
	@SuppressLint("InflateParams")
	public void handleEdit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.edit);

        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_fish_edit, null);
		
		final EditText nameET = (EditText)dialoglayout.findViewById(R.id.name);
		final EditText resourceET = (EditText)dialoglayout.findViewById(R.id.resource_link);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		nameET.setText(fish.getName());
		fish.setResourceField(resourceET);
		
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	name = nameET.getText().toString();
            	resource = resourceET.getText().toString();
            	comment = commentET.getText().toString();
 
            	if (!FieldValidation.isValidFishName(name)) {
            		Popup.ShowErrorMessage(context, R.string.invalid_name, false);
            	} else {
	        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
	            	progressDialog.setMessage(context.getString(R.string.saving));
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
		urlValues.add(new BasicNameValuePair("is_node", fish.isCategory() ? "1" : "0"));
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
		fish.handleEditSuccess(context, name, resource, comment);
	}
}