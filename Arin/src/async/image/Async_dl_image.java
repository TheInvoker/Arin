package async.image;

import image.ImageDownloader;

import java.io.IOException;
import http.Network;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/*
 * This is a async task that downloads images
 */

public class Async_dl_image extends AsyncTask<String, String, Bitmap> {

	private ImageDownloader callerContext;
	private ImageView imageview;
	
	public Async_dl_image(ImageDownloader callerContext, ImageView imageview, String link) {
		this.callerContext = callerContext;
		this.imageview = imageview;
    	
    	execute(link);
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		try {
			return Network.DownloadImage(callerContext.getContext(), arg0[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		callerContext.imageDownloadReturn(imageview, bitmap);
	}
}