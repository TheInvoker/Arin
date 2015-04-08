package recognition;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.util.SparseArray;

/*
 * This class handles removing the background of the image using BFS algorithm.
 */

public final class BGRemoval extends Function {

	public static void removeBG(Bitmap bufferedImage, int tol, Boolean useGradient) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		removeALLBG(bufferedImage, 50, false, 0, 0);
		removeALLBG(bufferedImage, 50, false, width-1, 0);
		removeALLBG(bufferedImage, 50, false, 0, height-1);
		removeALLBG(bufferedImage, 50, false, width-1, height-1);
	}
	
	/*
	 * removes the background
	 */
	public static void removeALLBG(Bitmap bufferedImage, int tol, Boolean useGradient, int x, int y)  {
		List<Pixel> Q = new ArrayList<Pixel>();
		SparseArray<Pixel> V = new SparseArray<Pixel>();
		Pixel initialPixel = getPixel(x, y, bufferedImage, null);
		Pixel v = getPixel(x, y, bufferedImage, null);
		V.put(v.getId(), v);
		Q.add(v);
		int new_pixel = Pixel.getTransparentColor().getARGB();
		
		while (Q.size() > 0) {
			Pixel t = Q.remove(Q.size()-1);

			if (!t.isTransparent() && ((useGradient && t.CloseToParent() <= tol) || (!useGradient && t.CloseTo(initialPixel) <= tol))) {
				t.makeTransparent();
				bufferedImage.setPixel(t.getX(), t.getY(), new_pixel); 
			} else {
				continue;
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
