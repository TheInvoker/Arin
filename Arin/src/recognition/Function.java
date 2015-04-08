package recognition;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;

/*
 * Basic image pixel functions.
 */

public class Function {
	
	public static Pixel getPixel(int x, int y, Bitmap bufferedImage, Pixel parent) {
		int pixel = bufferedImage.getPixel(x, y);
		MyColor color = new MyColor(pixel);
		return new Pixel(x, y, bufferedImage.getWidth(), color, parent);
	}
		
	public static List<Pixel> getAdj(Pixel t, Bitmap bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		List<Pixel> adj = new ArrayList<Pixel>();
		if (t.getY()>0) adj.add(getPixel(t.getX(), t.getY()-1, bufferedImage, t));
		if (t.getX()<width-1) adj.add(getPixel(t.getX()+1, t.getY(), bufferedImage, t));
		if (t.getY()<height-1) adj.add(getPixel(t.getX(), t.getY()+1, bufferedImage, t));
		if (t.getX()>0) adj.add(getPixel(t.getX()-1, t.getY(), bufferedImage, t));
		
		return adj;
	}
}
