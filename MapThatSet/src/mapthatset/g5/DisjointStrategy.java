package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mapthatset.sim.GuesserAction;

/*
 * After initial guessing in chunks, guess in sets of numbers that map to a list
 * of possible numbers that has at most one repeat.
 */
public class DisjointStrategy extends Strategy {
	
	private int initialGroupSize;
	private int mappingLength;
	private Map<Integer, Set<Integer>> possibleMappings;
	private ArrayList<Integer> currentQuery;
	private Map<ArrayList<Integer>, ArrayList<Integer>> pastQueries;
	private enum State {
		INITIAL_GUESSES,
		DISJOINT_STAGE;
		
		private int lastStart;

		public int getLastStart() {
			return lastStart;
		}

		public void setLastStart(int lastStart) {
			this.lastStart = lastStart;
		}
	}
	private State currentState;
	
	public DisjointStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		possibleMappings = new HashMap<Integer, Set<Integer>>();
		pastQueries = new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
		currentState = State.INITIAL_GUESSES;
		currentState.setLastStart(0);
		initialGroupSize = determineInitialGroupSize();
	}
	
	private int determineInitialGroupSize() {
		// TODO - this is just a suboptimal guess - experiment with this; there 
		// should be some formula that maximizes the expected number of things 
		// that can be fit within a disjoint set. At the very least,
		// run lots of experiments.
		
		// TODO - optimize this based on the spread of values (determine this
		// after previous rounds with the mapper).
		
		if (mappingLength < 6) {
			return Math.max(1, mappingLength / 2);
		}
		if (mappingLength < 12) {
			return Math.max(1, mappingLength / 3);
		}
		
		// Ensure at least 4 groups
		int groupSize = 2;
		int numGroups = 0;
		int divisor = 10;
		while (numGroups < 4 || groupSize < 3) {
			groupSize = mappingLength / divisor; 
			numGroups = mappingLength / groupSize ;
			divisor -= 2;
			// Just to make sure we don't divide by 0 or loop infinitely.
			if (divisor <= 0) {
				return 3;
			}
		}
		return groupSize;
	}
	
	@Override
	public GuesserAction nextAction() {
		currentQuery = new ArrayList<Integer>();
		if (currentState == State.INITIAL_GUESSES) {
			int lastStart = currentState.getLastStart();
			for (int i = lastStart + 1; i <= lastStart + initialGroupSize &&
					i <= mappingLength; i++) {
				currentQuery.add(i);
			}
			return new GuesserAction("q", currentQuery);
		} else if (currentState == State.DISJOINT_STAGE) {
			if (isMappingKnown()) {
				return new GuesserAction("g", getCorrectMapping());
			} else {
				return new GuesserAction("q", getMaximalMappedToSet());
			}
		} else {
			System.err.println("Unexpected state!");
			return null;
		}
	}
	
	private ArrayList<Integer> getCorrectMapping() {
		ArrayList<Integer> mapping = new ArrayList<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			mapping.add((Integer) possibleMappings.get(i).toArray()[0]); 
		}
		return mapping;
	}

	/*
	 * This is an approximation of the maximal set of numbers that includes at
	 * most one overlap. The real solution is NP-hard; a better approximation
	 * will be implemented later;
	 */
	private ArrayList<Integer> getMaximalMappedToSet() {
		Set<Integer> possiblyMappedToSet = new HashSet<Integer>();
		// TODO - could get infinite loop? Shouldn't if intersection algorithm
		// works correctly
		for (int i = 1; i <= mappingLength; i++) {
			if (possibleMappings.get(i).size() > 1) {
				Set<Integer> possiblyMappedToOverlap = new HashSet<Integer>();
				possiblyMappedToOverlap.addAll(possiblyMappedToSet);
				possiblyMappedToOverlap.retainAll(possibleMappings.get(i));
				if (possiblyMappedToOverlap.size() <= 1) {
					possiblyMappedToSet.addAll(possibleMappings.get(i));
					currentQuery.add(i);
				}
			}
		}
		return currentQuery;
	}
	
	private boolean isMappingKnown() {
		for (int i = 1; i <= mappingLength; i++) {
			if (possibleMappings.get(i).size() > 1) {
				return false;
			}
		}
		return true;
	}
	
	private void updateCurrentState() {
		if (currentState != State.INITIAL_GUESSES) {
			return;
		}
		int lastStart = currentState.getLastStart();
		if (lastStart + initialGroupSize >= mappingLength) {
			currentState = State.DISJOINT_STAGE;
		} else {
			currentState.setLastStart(lastStart + initialGroupSize);
		}
	}

	@Override
	public void setResult(ArrayList<Integer> result) {
		if (currentState == State.INITIAL_GUESSES) {
			// If we're doing initial guesses, we don't need to worry about 
			// overlap; just add the possible mappings to the query.
			for (int i : currentQuery) {
				Set<Integer> possibleValues = new HashSet<Integer>();
				for (int r : result) {
					possibleValues.add(r);
				}
				possibleMappings.put(i, possibleValues);
			}
		} else {
			reducePossibilities(currentQuery, result);
			for (ArrayList<Integer> q : pastQueries.keySet()) {
				reducePossibilities(q, pastQueries.get(q));
			}
		}
		pastQueries.put(currentQuery, result);
		updateCurrentState();
	}
	
	private void reducePossibilities(ArrayList<Integer> query,
			ArrayList<Integer> result) {
		// Maps one of the return values to each of the possible numbers that 
		// map to it.
		Map<Integer, Set<Integer>> coverage =
				new HashMap<Integer, Set<Integer>>(); 
		// Initialize coverage.
		for (int possibility : result) {
			Set<Integer> possibleMappingValues = new HashSet<Integer>();
			for (int i : query) {
				if (possibleMappings.get(i).contains(possibility)) {
					possibleMappingValues.add(i);
				}
			}
			coverage.put(possibility, possibleMappingValues);
		}
		
		if (DEBUG) {
			System.out.println(query);
			System.out.println(result);
			System.out.println(coverage);
		}
		
		/*
		 * Next, if any of the integers in the result (the integers which
		 * are mapped to) have only one integer, x, that might map to it in 
		 * the guess, we know that x must be used for this mapping. Thus,
		 * we can remove x from all other possible mappings. We repeat this
		 * n times to make sure that the process of reducing possible
		 * mappings is complete. Then, we update the possible results for
		 * each of the integer. 
		 */
		// Must iterate for |result| times for this logic to be accurate.
		for (int i = 0; i < result.size(); i++) {
			for (int mappedTo : coverage.keySet()) {
				Set<Integer> possibleMappers = coverage.get(mappedTo);
				if (possibleMappers.size() == 1) {
					removeMappings(coverage, mappedTo,
							(Integer) possibleMappers.toArray()[0]);
				}
			}
		}
		for (int i : query) {
			Set<Integer> possibleValues = new HashSet<Integer>();
			for (int r : coverage.keySet()) {
				if (coverage.get(r).contains(i)) {
					possibleValues.add(r);
				}
			}
			possibleMappings.put(i, possibleValues);
		}
		
		if (DEBUG) {
			System.out.println(possibleMappings);
		}
	}

	private void removeMappings(Map<Integer, Set<Integer>> coverage,
			int mappedTo, int mapped) {
		for (int i : coverage.keySet()) {
			if (i != mappedTo) {
				coverage.get(i).remove(mapped);
			}
		}
	}
	
	/**
	 * Modifies possibleMappings so that every integer maps to every other
	 * integer. This isn't really useful when we have a two-tiered strategy.
	 */
	@Deprecated
	private void initPossibleMappings() {
		possibleMappings = new HashMap<Integer, Set<Integer>>();
		for (int i = 1; i <= mappingLength; i++) {
			Set<Integer> possibleValues = new HashSet<Integer>();
			for (int j = 1; j <= mappingLength; j++) {
				possibleValues.add(j);
			}
			possibleMappings.put(i, possibleValues);
		}
	}


}
