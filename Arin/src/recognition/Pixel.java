package recognition;

/*
 * A class to manage pixel data.
 */

public class Pixel {
	
	private int id;
	private int x;
	private int y;
	private MyColor color;
	private Pixel parent;
	
	public Pixel(int x, int y, int width, MyColor color, Pixel parent) {
		this.id = y * width + x;
		this.x = x;
		this.y = y;
		this.color = color;
		this.parent = parent;
	}
	
	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public MyColor getColor() {
		return color;
	}

	public void setColor(MyColor color) {
		this.color = color;
	}

	public Pixel getParent() {
		return parent;
	}

	public void setParent(Pixel parent) {
		this.parent = parent;
	}

	
	
	
	
	public double CloseToParent() {
		if (getParent() == null) return 0;
		return CloseTo(getParent());
	} 
	
	public double CloseTo(Pixel otherpixel) {
		MyColor mycolor = getColor();
		MyColor othercolor = otherpixel.getColor();
	    return Pixel.colorDistance(mycolor, othercolor);
	} 
	
	public void makeTransparent() {
        setColor(getTransparentColor());
	}
	
	public Boolean isTransparent() {
		return getColor().getAlpha() == 0;
	}
	
	public static MyColor getTransparentColor() {
    	return new MyColor(0, 0, 0, 0);
	}
	
	/*
	 * Color compare
	 * http://stackoverflow.com/questions/2103368/color-logic-algorithm
	 */
	public static double colorDistance(MyColor color1, MyColor color2) {
	    double rmean = ( color1.getRed() + color2.getRed() )/2;
	    int r = color1.getRed() - color2.getRed();
	    int g = color1.getGreen() - color2.getGreen();
	    int b = color1.getBlue() - color2.getBlue();
	    double weightR = 2 + rmean/256;
	    double weightG = 4.0;
	    double weightB = 2 + (255-rmean)/256;
	    return Math.sqrt(weightR*r*r + weightG*g*g + weightB*b*b);
	}
}