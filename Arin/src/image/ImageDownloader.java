package image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageDownloader {

	public Activity getContext();
	public void imageDownloadReturn(ImageView imageview, Bitmap bitmap);
	
}
