package recognition;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.util.SparseArray;

/*
 * A class to remove island pixels.
 */

public final class IslandRemover extends Function {

	public static Bitmap removeIslandPixels(Bitmap bufferedImage) {
		
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		int center_x = width/2;
		int center_y = height/2;
		
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		drawFish(bufferedImage, newBitmap, center_x, center_y);
		bufferedImage.recycle();
		
		return newBitmap;
	}
	
	private static void drawFish(Bitmap bufferedImage, Bitmap newBitmap, int x, int y) {

		List<Pixel> Q = new ArrayList<Pixel>();
		SparseArray<Pixel> V = new SparseArray<Pixel>();
		Pixel v = getPixel(x, y, bufferedImage, null);
		V.put(v.getId(), v);
		Q.add(v);
		
		while (Q.size() > 0) {
			Pixel t = Q.remove(Q.size()-1);

			if (t.isTransparent()) {
				continue;
			} else {
				newBitmap.setPixel(t.getX(), t.getY(), t.getColor().getARGB());
			}

			List<Pixel> adj = getAdj(t, bufferedImage);
			for(Pixel u : adj) {
				if(V.indexOfKey(u.getId()) < 0) {
					V.put(u.getId(), u);
					Q.add(u);
				}
			}
		}	
	}
}
