package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mapthatset.sim.GuesserAction;

public class SubProblem {
	
	private Strategy strategy;
	private ArrayList<Integer> domain;
	private ArrayList<Integer> range;
	private Map<Integer, Integer> restoreDomainMap;
	private Map<Integer, Integer> restoreRangeMap;
	private Map<Integer, Integer> normalizeRangeMap;
	private GuesserAction lastGottenAction;
	
	public enum SubProblemStrategy {
		BINARY, PERM
	}
	
	public SubProblem(boolean debug, ArrayList<Integer> domain,
			ArrayList<Integer> range, SubProblemStrategy  strategyType) {
		this.domain = domain;
		this.range = range;
		int n = 1;
		restoreDomainMap = new HashMap<Integer, Integer>();
		// i -> real
		for (int i : domain) {
			restoreDomainMap.put(n, i);
			n++;
		}
		normalizeRangeMap = new HashMap<Integer, Integer>();
		restoreRangeMap = new HashMap<Integer, Integer>();
		n = 1;
		// real -> i
		for (int i : range) {
			restoreRangeMap.put(n, i);
			normalizeRangeMap.put(i, n);
			n++;
		}
		switch (strategyType) {
			case BINARY:
				strategy = new BinaryStrategy(debug);
				break;
			case PERM:
				strategy = new PermStrategy(debug);
				break;
			default:
				System.err.println("Invalid solver for subproblem.");
				System.exit(1);
				break;
		}
		strategy.startNewMapping(domain.size());
	}
	
	public GuesserAction nextAction() {
		GuesserAction action = strategy.nextAction();
		lastGottenAction = action;
		// Convert from 1..n to the real domain
		ArrayList<Integer> remappedActionContent = new ArrayList<Integer>();
		for (int i : action.getContent()) {
			remappedActionContent.add(restoreDomainMap.get(i));
		}
		if (action.getType().equals("g")) {
			return new GuesserAction("g", remappedActionContent);
		}
		return new GuesserAction("q", remappedActionContent);
	}
	
	public void setResult(ArrayList<Integer> result) {
		ArrayList<Integer> remappedResult = new ArrayList<Integer>();
		for (int i : result) {
			remappedResult.add(normalizeRangeMap.get(i));
		}
		strategy.setResult(remappedResult);
	}
	
	public ArrayList<Integer> getRange() {
		return range;
	}
	
	public ArrayList<Integer> getDomain() {
		return domain;
	}

	public void solveSubsection(SubProblemMaster spm) {
		ArrayList<Integer> unalignedSolution = lastGottenAction.getContent();
		for (int i = 1; i <= unalignedSolution.size(); i++) {
			spm.solve(restoreDomainMap.get(i),
					restoreRangeMap.get(unalignedSolution.get(i)));
		}
	}
	
}
