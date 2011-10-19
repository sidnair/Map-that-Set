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
	private Map<Integer, Integer> normalizeRangeMap;
	
	public enum SubProblemStrategy {
		BINARY, PERM
	}
	
	public SubProblem(boolean debug, ArrayList<Integer> domain,
			ArrayList<Integer> range, SubProblemStrategy  strategyType) {
		this.domain = domain;
		this.range = range;
		int n = 1;
		restoreDomainMap = new HashMap<Integer, Integer>();
		for (int i : domain) {
			restoreDomainMap.put(n, i);
			n++;
		}
		normalizeRangeMap = new HashMap<Integer, Integer>();
		n = 1;
		for (int i : range) {
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
		// Convert from 1..n to the real domain
		ArrayList<Integer> remappedAction = new ArrayList<Integer>();
		for (int i : action.getContent()) {
			remappedAction.add(restoreDomainMap.get(i));
		}
		if (action.getType().equals("g")) {
			System.err.println("Guessing from subproblem");
			System.exit(1);
		}
		return action;
	}
	
	public void setResult(ArrayList<Integer> result) {
		ArrayList<Integer> remappedResult = new ArrayList<Integer>();
		for (int i : result) {
			remappedResult.add(normalizeRangeMap.get(i));
		}
	}
	
	public ArrayList<Integer> getRange() {
		return range;
	}
	
	public ArrayList<Integer> getDomain() {
		return domain;
	}

}
