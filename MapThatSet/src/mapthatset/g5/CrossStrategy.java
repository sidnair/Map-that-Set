package mapthatset.g5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import mapthatset.sim.GuesserAction;

public class CrossStrategy extends Strategy {

	private int mappingLength;
	private ArrayList<Integer> currentQuery;
	private MappingTracker mappingTracker;
	private HashSet<String> guessedPairs;
	private Set<Integer> domain;
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
		this.domain = new HashSet<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			domain.add(i);
		}
	}
	protected void startNewMapping(int mappingLength,
			ArrayList<Integer> query, ArrayList<Integer> result) {
		startNewMapping(mappingLength);
		mappingTracker.updateTracker(result, query);
	}

	@Override
	protected GuesserAction nextAction() {
		if (mappingTracker.isMappingKnown()) {
			currentQuery = mappingTracker.getCorrectMapping();
			return new GuesserAction("g", currentQuery);
		}
		int limit = (int) Math.ceil(Math.sqrt(mappingLength));
		currentQuery = new ArrayList<Integer>();
		ArrayList<Integer> legalVars = new ArrayList<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			if (!mappingTracker.isKnown(i) && domain.contains(i)) {
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
		for (int i = 0; i < legalVars.size() && currentQuery.size() < limit; i++) {
			int firstInt = legalVars.get(i);
			if (!isPairUsed(firstInt, currentQuery)) {
				currentQuery.add(firstInt);
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
		setResult(result, currentQuery);
	}
	
	/*
	 * If the query shouldn't be the one used internally (e.g. if this is only
	 * part of a subproblem.
	 */
	protected void setResult(ArrayList<Integer> result,
			ArrayList<Integer> query) {
		mappingTracker.updateTracker(result, query);
		for (int i : query) {
			guessCount[i - 1]++;
			for (int j : query) {
				if (i < j) {
					guessedPairs.add(getPairString(i, j));
				}
			}
		}
	}
	
	private String getPairString(int a, int b) {
		return Math.min(a, b) + "," + Math.max(a, b);
	}
	
	public void restrictDomain(Set<Integer> domain) {
		this.domain = domain;
	}

	@Override
	protected boolean supportsSubProblems() {
		return true;
	}

}
