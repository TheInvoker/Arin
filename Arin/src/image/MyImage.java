package image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.MediaStore;

/*
 * Handles some image tasks.
 */

public final class MyImage {
	
	
	public static String getPath(Context context, Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String filePath = cursor.getString(column_index);
		cursor.close();
		return filePath;
	}
	
	/*
	 * get bitmap from path
	 */
	public static Bitmap GetBitmapFromPath(Context context, Uri uri, int IVwidth, int IVheight) {
		String filePath = getPath(context, uri);
		return getScaledImage(filePath, IVwidth, IVheight);
	}
	
	public static Bitmap getBitmapForRecognition(File file) {
		if (file.exists()) {
			BitmapFactory.Options op = new BitmapFactory.Options(); 
			op.inPreferredConfig = Bitmap.Config.ARGB_8888; 
			Bitmap bufferedImage = BitmapFactory.decodeFile(file.getAbsolutePath(), op);
			return bufferedImage;
		}
		return null;
	}
	
	/*
	 * resizes image until it is small enough to not crash the app
	 */
	private static Bitmap getScaledImage(String filePath, int surfaceWidth, int surfaceHeight) {
		int sampleSize = 1;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = sampleSize;
		
		BitmapFactory.decodeFile(filePath, options);
		int width = options.outWidth;
		int height = options.outHeight;

		while (width/sampleSize > surfaceWidth && height/sampleSize > surfaceHeight) {
			sampleSize++;
		}
		
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888; 
		
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
    	bitmap.compress(CompressFormat.PNG, 100, out);
    	return bitmap;
	}
}
