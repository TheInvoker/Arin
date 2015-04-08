package recognition;

import fish.Species;
import android.graphics.Bitmap;

/*
 * The main recognition code that analyzes color and shape of fish images.
 */

public class Recognize {

	public static final int SCAN_SIZE = 128;

	/*
	 * gets a cleaned initial image
	 */
	public static Result prepareForScan(Bitmap bufferedImage, Species specie) {
		return new Result(specie, ImageResize.resizeMutableFitCenter(bufferedImage, SCAN_SIZE));
	}
	
	/*
	 * save the image
	 */
	/*
	private static int nameInt = 0; 
	private static void saveImage(Bitmap bitmap) throws IOException {
		String path = Environment.getExternalStorageDirectory().toString();
		String imgpath = "arin/TEST/file" + Integer.toString(nameInt++, 10) + ".png"; 
		
		File file = new File(path, imgpath);
		file.getParentFile().mkdirs();
		
		if (file.exists()) {
			file.delete();
		}
		
		OutputStream fOut = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

		fOut.flush();
		fOut.close();
	}
	*/
}
