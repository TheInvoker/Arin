package recognition;

import fish.Species;
import android.graphics.Bitmap;

/*
 * A class to manage result data.
 */

public class Result {
	
	private Species specie;
	private Bitmap bitmap;
	
	private final double STEP_SIZE = 2.0;
	private final int MAX_COLOR_DIST = 765;
	
	public Result(Species specie, Bitmap bitmap) {
		this.specie = specie;
		this.bitmap = bitmap;
	}

	public Species getSpecies() {
		return specie;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public CompareResult compareTo(Result otherResult) {
		Bitmap fish1 = getBitmap();
		Bitmap fish2 = otherResult.getBitmap();
		
		/////////////////////////////////////////////////////////
		double color_sum = 0;
		double intersect_sum = 0;
		int fish1size = 0;
		int fish2size = 0;
		
		int width = fish1.getWidth();
		int height = fish1.getHeight();
		
		for(int i=0; i<width; i+=STEP_SIZE) {
			for(int j=0; j<height; j+=STEP_SIZE) {
				Pixel fish1pixel = Function.getPixel(i, j, fish1, null);
				Pixel fish2pixel = Function.getPixel(i, j, fish2, null);
				
				Boolean f1Trans = fish1pixel.isTransparent();
				Boolean f2Trans = fish2pixel.isTransparent();
				
				if (!f1Trans && !f2Trans) {
					fish1size += 1;
					fish2size += 1;
					intersect_sum += 1;
					color_sum += Pixel.colorDistance(fish1pixel.getColor(), fish2pixel.getColor());
				} else if (!f1Trans && f2Trans) {
					fish1size += 1;
					color_sum += MAX_COLOR_DIST;
				} else if (f1Trans && !f2Trans) {
					fish2size += 1;
					color_sum += MAX_COLOR_DIST;
				}
			}
		}
		/////////////////////////////////////////////////////////

		double gridSize = width * height;
		double colorGridSize = gridSize * MAX_COLOR_DIST;
		
		double ratio1 = fish1size == 0 ? 0 : (intersect_sum / fish1size);
		double ratio2 = fish2size == 0 ? 0 : (intersect_sum / fish2size);
		double intersectrank = ((ratio1 + ratio2)/2.0) * (colorGridSize/STEP_SIZE);
		double colorrank = colorGridSize - color_sum;
		double rank = intersectrank + colorrank;
		
		return new CompareResult(otherResult.getSpecies(), rank);
	}
}