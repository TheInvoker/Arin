package async.image;

import image.TempCategoryImage;
import image.TempImage;
import image.TempSpeciesImage;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import fish.Fish;
import http.HttpTask;
import http.Network;
import activity.SpeciesScreen;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/*
 * This is a async task that uploads images
 */

public class Async_up_image extends AsyncTask<String, Void, String> implements HttpTask {

	private Bitmap bitmap;
	private Fish fish;
	private JSONObject obj;
	private Activity context;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "image/uploadimage.php";
	
	public Async_up_image(Activity context, Bitmap bitmap, Fish fish, JSONObject obj) {
		this.context = context;
		this.fish = fish;
		this.bitmap = bitmap;
		this.obj = obj;
		
		getComment();
	}
	
	@SuppressLint("InflateParams")
	private void getComment() {
        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_comment, null);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            	progressDialog.setMessage(context.getString(R.string.uploading));
            	progressDialog.setCancelable(false);
            	progressDialog.show();
            	
            	try {
            		String comment = commentET.getText().toString();
					obj.put(SpeciesScreen.cmmtKey, comment);
				} catch (JSONException e) {
					e.printStackTrace();
					Popup.ShowErrorMessage(context, R.string.unexpected_error, false);
					return;
				}
            	
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
		try {
			TempImage tempImage = fish.isCategory() ? 
					new TempCategoryImage(null, null, obj, fish.getId()) :
					new TempSpeciesImage(null, null, obj, fish.getId());
			
			String result = Network.UploadImage(context, bitmap, WEBFILE, tempImage);
			
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(String response) {
		if (response!=null) {
			try {
				JSONObject object = new JSONObject(response);
				int code = object.getInt(CODE);
				
				if (code==200) {
					JSONObject result = object.getJSONObject(RESPONSE);
					int pic_id = result.getInt("pic_id");
					String elink = result.getString("elink");
					int locationId = result.getInt("locationId");
					
					worked(pic_id, elink, locationId);
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (Exception e) {
				Popup.ShowErrorMessage(context, R.string.imageuploaderror, false);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, false);
		}
		
		progressDialog.dismiss();
	}
	
	private void worked(int pic_id, String elink, int locationId) throws IOException, JSONException {
		fish.handleUploadImageSuccess(pic_id, bitmap, obj, elink, locationId);
	}
}