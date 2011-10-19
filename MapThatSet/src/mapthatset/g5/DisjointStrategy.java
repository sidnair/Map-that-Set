package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import mapthatset.sim.GuesserAction;

/*
 * After initial guessing in chunks, guess in sets of numbers that map to a list
 * of possible numbers that has at most one repeat.
 */
public class DisjointStrategy extends Strategy {
	
	private int initialGroupSize;
	private int mappingLength;
	private ArrayList<Integer> currentQuery;
	private MappingTracker mappingTracker;
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
		mappingTracker = new MappingTracker(mappingLength);
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
			}
		}
		return currentQuery;
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
