package recognition;

import fish.Species;

/*
 * Manages the compare result.
 */

public class CompareResult {
	
	private Species specie;
	private double result;
	
	public CompareResult(Species specie, double result) {
		this.specie = specie;
		this.result = result;
	}
	
	public Species getSpecies() {
		return specie;
	}
	
	public int getSpeciesId() {
		return specie.getId();
	}
	
	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}
}
