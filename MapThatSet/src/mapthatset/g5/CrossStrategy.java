package mapthatset.g5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import mapthatset.sim.GuesserAction;

public class CrossStrategy extends Strategy {

	private int mappingLength;
	private ArrayList<Integer> currentQuery;
	private MappingTracker mappingTracker;
	private HashSet<String> guessedPairs;
	int[] guessCount;

	protected CrossStrategy(boolean debug) {
		super(debug);
	}

	@Override
	protected void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		mappingTracker = new MappingTracker(mappingLength);
		guessCount = new int[mappingLength];
		guessedPairs = new HashSet<String>();
	}

	@Override
	protected GuesserAction nextAction() {
		if (mappingTracker.isMappingKnown()) {
			return new GuesserAction("g", mappingTracker.getCorrectMapping());
		}
		int limit = (int) Math.ceil(Math.sqrt(mappingLength));
		currentQuery = new ArrayList<Integer>();
		ArrayList<Integer> legalVars = new ArrayList<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			if (!mappingTracker.isKnown(i)) {
				legalVars.add(i);
			}
		}
		Collections.sort(legalVars, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				// Higher guessCount is put later in the array
				return guessCount[a - 1] - guessCount[b - 1];
			}
		});
//		System.out.println("\n----------------");
//		System.out.println("\n\t" + legalVars);
//		System.out.println("\t" + guessedPairs);
		for (int i = 0; i < legalVars.size() && currentQuery.size() < limit; i++) {
			int firstInt = legalVars.get(i);
			if (!isPairUsed(firstInt, currentQuery)) {
				currentQuery.add(firstInt);
				guessCount[firstInt - 1]++;
			}
		}
		return new GuesserAction("q", currentQuery);
	}
	
	private boolean isPairUsed(int firstInt, ArrayList<Integer> onGoingQuery) {
		for (int secondInt : onGoingQuery) {
			String pairString = getPairString(firstInt, secondInt);
			if (guessedPairs.contains(pairString)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void setResult(ArrayList<Integer> result) {
		mappingTracker.updateTracker(result, currentQuery);
		for (int i : currentQuery) {
			for (int j : currentQuery) {
				if (i < j) {
					guessedPairs.add(getPairString(i, j));
				}
			}
		}
	}
	
	private String getPairString(int a, int b) {
		return Math.min(a, b) + "," + Math.max(a, b);
	}

}
