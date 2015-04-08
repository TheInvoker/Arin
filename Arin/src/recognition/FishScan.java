package recognition;

import android.graphics.Bitmap;
import fish.Species;

/*
 * Stores the info about the fish as input.
 */

public class FishScan {

	private Bitmap bitmap;
	private Species specie;
	
	public FishScan(Bitmap bitmap, Species specie) {
		this.bitmap = bitmap;
		this.specie = specie;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public Species getSpecies() {
		return specie;
	}
}
