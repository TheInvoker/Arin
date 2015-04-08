package async.image;

import recognition.BGRemoval;
import recognition.ImageResize;
import recognition.ImageTrim;
import recognition.IslandRemover;
import activity.SpeciesScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

public class Async_image_clean extends AsyncTask<Bitmap, Integer, Bitmap> {
	
	private SpeciesScreen context;
	private int requestCode;
	private Uri uri;
	private ProgressDialog progressDialog;
	
	public Async_image_clean(SpeciesScreen context, int requestCode, Bitmap bitmap, Uri uri) {
		this.context = context;
		this.requestCode = requestCode;
		this.uri = uri;
		
   		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
   		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
   		progressDialog.setMax(100);
    	progressDialog.setMessage(context.getString(R.string.extracting));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
		
    	execute(bitmap);
	}
	
	@Override
	protected Bitmap doInBackground(Bitmap... arg0) {

		// get the image
		Bitmap bitmap = arg0[0];

		// make it mutable
		bitmap = ImageResize.resizeMutable(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2);

		publishProgress(10); 
		
		// remove the bg
		BGRemoval.removeBG(bitmap, 50, false);
		
		publishProgress(50); 
		
		// remove the island pixels
		bitmap = IslandRemover.removeIslandPixels(bitmap);
		
		publishProgress(90); 
		
		// trim it
		bitmap = ImageTrim.Trim(bitmap);

		publishProgress(100); 
		
		return bitmap;
	}
	
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        progressDialog.setProgress(values[0]);
    } 
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		progressDialog.dismiss();
		context.imageCleanReturn(requestCode, bitmap, uri);
	}
}
