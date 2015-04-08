package recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/*
 * A class to manage resizing images.
 */

public class ImageResize {
	
	public static Bitmap resizeMutable(Bitmap bufferedImage, int newWidth, int newHeight) {

		Bitmap resizedImg = resizeMutable(bufferedImage, Bitmap.Config.ARGB_8888, newWidth, newHeight);
		bufferedImage.recycle();

		return resizedImg;
	}
	
	public static Bitmap resizeMutableFitCenter(Bitmap bufferedImage, int scan_size) {
		Bitmap convertedBitmap = Bitmap.createBitmap(scan_size, scan_size, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(convertedBitmap);
	    
		if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
		    int newHeight = (scan_size * bufferedImage.getHeight()) / bufferedImage.getWidth();
		    canvas.drawBitmap(bufferedImage, null, new RectF(0, (scan_size-newHeight)/2, scan_size, (scan_size+newHeight)/2), null);
		} else {
		    int newWidth = (scan_size * bufferedImage.getWidth()) / bufferedImage.getHeight();
		    canvas.drawBitmap(bufferedImage, null, new RectF((scan_size-newWidth)/2, 0, (scan_size+newWidth)/2, scan_size), null);
		}
		
		bufferedImage.recycle();
		return convertedBitmap;
	}
	
	public static Bitmap resizeImmutable(Bitmap bufferedImage, int newWidth, int newHeight) {
		if (bufferedImage.getWidth() == newWidth && bufferedImage.getHeight() == newHeight) {
			return bufferedImage;
		}
			
		Bitmap resizedImg = Bitmap.createScaledBitmap(bufferedImage, newWidth, newHeight, false);
		bufferedImage.recycle();
		
		return resizedImg;
	}
	
	private static Bitmap resizeMutable(Bitmap bufferedImage, Bitmap.Config config, int width, int height) {
	    Bitmap convertedBitmap = Bitmap.createBitmap(width, height, config);
	    Canvas canvas = new Canvas(convertedBitmap);
	    canvas.drawBitmap(bufferedImage, null, new RectF(0, 0, width, height), null);
	    return convertedBitmap;
	}
}
