package async.image;

import org.json.JSONObject;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.ThreadScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.graphics.Bitmap;
import android.os.AsyncTask;

/*
 * This is a async task that uploads images
 */

public class Async_up_thread_image extends AsyncTask<String, Void, String> implements HttpTask {

	private Bitmap bitmap;
	private ThreadScreen context;
	private int thread_id;
	private String thread_title;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "image/uploadthreadimage.php";
	
	public Async_up_thread_image(ThreadScreen context, int thread_id, String thread_title, Bitmap bitmap) {
		this.context = context;
		this.thread_id = thread_id;
		this.thread_title = thread_title;
		this.bitmap = bitmap;
		
   		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(R.string.uploading));
    	progressDialog.setCancelable(false);
    	progressDialog.show();

    	execute();
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			return Network.UploadThreadImage(context, bitmap, WEBFILE, thread_id, thread_title);
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
					worked(pic_id);
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
	
	private void worked(int pic_id) {
		context.imageUploadReturn(bitmap, pic_id);
	}
}