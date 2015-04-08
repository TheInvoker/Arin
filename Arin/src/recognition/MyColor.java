package recognition;

/*
 * Custom color class.
 */

public class MyColor {

	private int A;
	private int R;
	private int G;
	private int B;
	private int ARGB;
	
	public MyColor(int A, int R, int G, int B) {
		this.A = A;
		this.R = R;
		this.G = G;
		this.B = B;
		this.ARGB = ((A << 24) | 0xFF) + ((R << 16) | 0xFF) + ((G << 8) | 0xFF) + (B | 0xFF);
	}
	
	public MyColor(int ARGB) {
		this.A = (ARGB >> 24) & 0xFF;
		this.R = (ARGB >> 16) & 0xFF;
		this.G = (ARGB >> 8) & 0xFF;
		this.B = ARGB & 0xFF;
		this.ARGB = ARGB;
	}

	public int getAlpha() {
		return A;
	}

	public int getRed() {
		return R;
	}

	public int getGreen() {
		return G;
	}

	public int getBlue() {
		return B;
	}

	public int getARGB() {
		return ARGB;
	}
}
