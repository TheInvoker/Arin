package recognition;

import android.graphics.Bitmap;

/*
 * A class to help auto trim images.
 */

public class ImageTrim extends Function {
	
	public static Bitmap Trim(Bitmap bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		int topBoundary = getTopBoundary(bufferedImage, width, height);
		int bottomBoundary = getBottomBoundary(bufferedImage, width, height);
		int leftBoundary = getLeftBoundary(bufferedImage, width, height);
		int rightBoundary = getRightBoundary(bufferedImage, width, height);
		
		if (topBoundary + bottomBoundary + leftBoundary + rightBoundary == -4) {
			return bufferedImage;
		}
		
		if (topBoundary==-1) topBoundary=0;
		if (bottomBoundary==-1) bottomBoundary=height-1;
		if (leftBoundary==-1) leftBoundary=0;
		if (rightBoundary==-1) rightBoundary=width-1;
		
		Bitmap dest = Bitmap.createBitmap(bufferedImage, leftBoundary, topBoundary, rightBoundary-leftBoundary, bottomBoundary-topBoundary);
		bufferedImage.recycle();
		
		return dest;
	}
	
	private static int getTopBoundary(Bitmap bufferedImage, int width, int height) {
		for(int y=0; y<height; y+=1) {
			for(int x=0; x<width; x+=1) {
				Pixel pixel = getPixel(x, y, bufferedImage, null);
				if (!pixel.isTransparent()) {
					return y;
				}
			}
		}
		return -1;
	}
	
	private static int getBottomBoundary(Bitmap bufferedImage, int width, int height) {
		for(int y=height-1; y>=0; y-=1) {
			for(int x=0; x<width; x+=1) {
				Pixel pixel = getPixel(x, y, bufferedImage, null);
				if (!pixel.isTransparent()) {
					return y;
				}
			}
		}
		return -1;
	}
	
	private static int getLeftBoundary(Bitmap bufferedImage, int width, int height) {
		for(int x=0; x<width; x+=1) {
			for(int y=0; y<height; y+=1) {
				Pixel pixel = getPixel(x, y, bufferedImage, null);
				if (!pixel.isTransparent()) {
					return x;
				}
			}
		}
		return -1;
	}
	
	private static int getRightBoundary(Bitmap bufferedImage, int width, int height) {
		for(int x=width-1; x>=0; x-=1) {
			for(int y=0; y<height; y+=1) {
				Pixel pixel = getPixel(x, y, bufferedImage, null);
				if (!pixel.isTransparent()) {
					return x;
				}
			}
		}
		return -1;
	}
}
