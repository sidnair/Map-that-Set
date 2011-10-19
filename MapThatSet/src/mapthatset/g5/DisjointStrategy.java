package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import mapthatset.sim.GuesserAction;

public class DisjointStrategy extends Strategy {
	
	private int initialGroupSize;
	private int mappingLength;
	private int guessSize;
	private ArrayList<Integer> currentQuery;
	private MappingTracker mappingTracker;
	private Map<Integer, Set<Integer>> bindings;
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
	
	public DisjointStrategy(boolean debug, int k) {
		super(debug);
		this.guessSize=2;
	}

	@Override
	public void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		mappingTracker = new MappingTracker(mappingLength);
		bindings = new HashMap<Integer, Set<Integer>>();
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
			return Math.min(this.guessSize,Math.max(1, mappingLength / 2));
		}
		if (mappingLength < 12) {
			return Math.min(guessSize, Math.max(1, mappingLength / 3));
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
		return Math.min(guessSize, groupSize);
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
			if (mappingTracker.isMappingKnown()) {
				return new GuesserAction("g", mappingTracker.getCorrectMapping());
			} else {
				return new GuesserAction("q", getMaximalMappedToSet());
			}
		} else {
			System.err.println("Unexpected state!");
			return null;
		}
	}

	/*
	 * This is an approximation of the maximal set of numbers that includes at
	 * most one overlap. The real solution is NP-hard; a better approximation
	 * will be implemented later;
	 */
	private ArrayList<Integer> getMaximalMappedToSet() {
		Set<Integer> possiblyMappedToSet = new HashSet<Integer>();
		Map<Integer, Set<Integer>> possibleMappings =
				mappingTracker.getPossibleMappings();
		
		
		
		for (int i = 1; i <= mappingLength; i++) {
			if (possibleMappings.get(i).size() > 1) {
				Set<Integer> possiblyMappedToOverlap = new HashSet<Integer>();
				possiblyMappedToOverlap.addAll(possiblyMappedToSet);
				possiblyMappedToOverlap.retainAll(possibleMappings.get(i));
				if (possiblyMappedToOverlap.size() <= 1) {
					possiblyMappedToSet.addAll(possibleMappings.get(i));
					currentQuery.add(i);
				}
				// we don't want to make a query of size one in early stage, so we look for two elements that are not 
				// "bind together", which means we don't know either they are same or opposite
				if (guessSize == 2 && currentQuery.size() == 1) {
					Integer nextUnbound = getNextUnbound(currentQuery.get(0));
					if (nextUnbound != null) {
						currentQuery.add(nextUnbound);
					}
				}
			}
		}
		
		updateBindings(currentQuery);
		
		return currentQuery;
	}
	
	private Integer getNextUnbound(int first) {
		Set<Integer> possible = new HashSet<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			possible.add(i);
		}
		if (bindings.get(first) == null) {
			return null;
		}
		possible.retainAll(bindings.get(first));
		return (Integer) (possible.size() > 0 ? possible.toArray()[0] : null);
	}
	
	private void updateBindings(ArrayList<Integer> query) {
		Set<Integer> newlyBoundSet = new HashSet<Integer>();
		for (int i : query) {
			if (bindings.get(i) != null) {
				newlyBoundSet.addAll(bindings.get(i));
				newlyBoundSet.addAll(query);
			}
		}
		for (int i : query) {
			if (bindings.get(i) != null) {
				for (int j : bindings.get(i)) {
					bindings.put(j, newlyBoundSet);	
				}
			} else {
				bindings.put(i, newlyBoundSet);
			}
		}
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
	protected void setResult(ArrayList<Integer> result) {
		if (DEBUG) {
			System.out.println(currentQuery + " --> " + result);
		}
		mappingTracker.updateTracker(result, currentQuery);
		updateCurrentState();
	}

	@Override
	protected void startNewMapping(int mappingLength, ArrayList<Integer> query,
			ArrayList<Integer> result) {
		startNewMapping(mappingLength);
		mappingTracker.updateTracker(result, query);
	}

	@Override
	protected boolean supportsSubProblems() {
		return false;
	}

}
