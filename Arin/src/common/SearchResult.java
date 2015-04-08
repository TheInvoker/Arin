package common;

import fish.Species;
import recognition.CompareResult;

/*
 * This is a object to hold special information about search
 * results and its visibility state.
 */

public class SearchResult {

	private CompareResult compareResult;
	private double closestDist = 0;
	
	public SearchResult(CompareResult compareResult, double closestDist) {
		this.compareResult = compareResult;
		this.closestDist = closestDist;
	}
	
	public Species getSpecie() {
		return compareResult.getSpecies();
	}

	public CompareResult getCompareResult() {
		return compareResult;
	}
	
	public double getClosestDist() {
		return closestDist;
	}

	public void setClosestDist(double closestDist) {
		this.closestDist = closestDist;
	}
}