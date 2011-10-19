package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import mapthatset.sim.GuesserAction;

/**
 * Detects which strategy the mapper is using and then delegates to the optimal
 * specific strategy.
 */
public class ControllerStrategy extends Strategy {

	private boolean ENABLE_SUBPROBLEMS = false;
	private Strategy currentStrat;
	private boolean stratKnown;
	private int mappingLength;
	private ArrayList<Integer> currentGuess;
	private ArrayList<SubProblem> subproblems;
	private ArrayList<Integer> lastSubProblemQuery;
	private enum MappingType {
		BINARY, PERM, OTHER
	}
	
	public ControllerStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		stratKnown = false;
		currentStrat = null;
		subproblems = new ArrayList<SubProblem>();
		lastSubProblemQuery = new ArrayList<Integer>();
	}

	@Override
	public GuesserAction nextAction() {
		if (stratKnown) {
			if (ENABLE_SUBPROBLEMS) {
				return nextActionWithSubProblems();
			} else {
				return currentStrat.nextAction();
			}
		} else {
			return makeInitialGuess();
		}
	}
	

	private GuesserAction makeInitialGuess() {
		currentGuess = new ArrayList<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			currentGuess.add(i);
		}
		return new GuesserAction("q", currentGuess);
	}

	@Override
	public void setResult(ArrayList<Integer> result) {
		if (stratKnown) {
			if (ENABLE_SUBPROBLEMS) {
				setSubProblemsResult(result);
			} else {
				currentStrat.setResult(result);
			}
		} else {
			switch (determineMappingType(result, currentGuess)) {
			case BINARY:
				 currentStrat = new BinaryStrategy(DEBUG);
				break;
			case PERM:
				currentStrat = new PermStrategy(DEBUG);
				break;
			case OTHER:
				currentStrat = new CrossStrategy(DEBUG);
				// Currently never use disjoint strategy.
				// currentStrat = new DisjointStrategy(DEBUG);
				break;
			default:
				System.err.println("No strategy selected...");
				System.exit(1);
			}
			currentStrat.startNewMapping(mappingLength, currentGuess, 
					result);
			stratKnown = true;
		}

	}
	
	private Set<Integer> updateSubProblems() {
		Iterator<SubProblem> iter = subproblems.iterator();
		Set<Integer> subproblemDomains = new HashSet<Integer>();
		while(iter.hasNext()) {
			SubProblem sp = iter.next();
			if (((SubProblemMaster) currentStrat).isSolved(sp)) {
				iter.remove();
			} else {
				subproblemDomains.addAll(sp.getDomain());
			}
		}
		Set<Integer> unmatchedDomains = new HashSet<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			if (!subproblemDomains.contains(i)) {
				unmatchedDomains.add(i);
			}
		}
		
		// TODO - get disjoint graphs from unmatched, create new subproblems
		// and add
		
		return unmatchedDomains;
	}
	
	private GuesserAction nextActionWithSubProblems() {
		// Subproblems updated here
		Set<Integer> unmatchedDomain = updateSubProblems();
		Set<Integer> nextQuery = new HashSet<Integer>();
		
		// iterate through the subproblem solvers, adding all the guesses to
		// the guess list
		for (SubProblem sp : subproblems) {
			nextQuery.addAll(sp.nextAction().getContent());
		}
		
		// restrict domain on the general problem (All - U(subproblem domains))
		((SubProblemMaster) currentStrat).restrictDomain(unmatchedDomain);
		
		GuesserAction masterAction = currentStrat.nextAction();
		if (masterAction.getType() == "g") {
			System.err.println("rly?");
			lastSubProblemQuery = masterAction.getContent();
			return masterAction;
		} else {
			// add the general stuff to the guess list
			nextQuery.addAll(masterAction.getContent());
		}
		lastSubProblemQuery = new ArrayList<Integer>(nextQuery);
		return new GuesserAction("q", lastSubProblemQuery);
	}

	private void setSubProblemsResult(ArrayList<Integer> result) {
		// for each subproblem, call setResult on intersection of subproblem
		// range and result
		for (SubProblem sp : subproblems) {
			ArrayList<Integer> relevantResults =
					new ArrayList<Integer>(sp.getRange());
			relevantResults.retainAll(result);
			sp.setResult(relevantResults);
		}
		// Call set result on the main solver
		((SubProblemMaster) currentStrat).setResult(result, lastSubProblemQuery);
	}

	private MappingType determineMappingType(ArrayList<Integer> result,
			ArrayList<Integer> guess) {
		if (result.size() == mappingLength) {
			return MappingType.PERM;
		}
		if (result.size() == 2 ) {
			return MappingType.BINARY;
		}
		return MappingType.OTHER;
	}

	@Override
	protected void startNewMapping(int mappingLength, ArrayList<Integer> query,
			ArrayList<Integer> result) {
		System.err.println("Controller strat shouldn't be called with " +
					"initial results known...");
		System.exit(1);
	}

	@Override
	protected boolean supportsSubProblems() {
		return false;
	}

}
