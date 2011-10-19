package mapthatset.g5;

import java.util.ArrayList;

import mapthatset.sim.GuesserAction;

/**
 * Detects which strategy the mapper is using and then delegates to the optimal
 * specific strategy.
 */
public class ControllerStrategy extends Strategy {

	private Strategy currentStrat;
	private boolean stratKnown;
	private int mappingLength;
	private ArrayList<Integer> currentGuess;
	private enum MappingType {
		BINARY, PERM, DISJOINT, OTHER
	}
	
	public ControllerStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		stratKnown = false;
		currentStrat = null;
	}

	@Override
	public GuesserAction nextAction() {
		if (stratKnown) {
			return currentStrat.nextAction();
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
			currentStrat.setResult(result);
		} else {
			switch (determineMappingType(result, currentGuess)) {
			case BINARY:
				 currentStrat = new BinaryStrategy(DEBUG);
				break;
			case PERM:
				currentStrat = new PermStrategy(DEBUG);
				break;
			case DISJOINT:	
				currentStrat = new DisjointStrategy(DEBUG,2);
				break;
			case OTHER:
				currentStrat = new CrossStrategy(true);
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

	private MappingType determineMappingType(ArrayList<Integer> result,
			ArrayList<Integer> guess) {
		if (result.size() == mappingLength) {
			return MappingType.PERM;
		}
		if (result.size() == 2 ) {
			return MappingType.BINARY;
		}
		if (result.size()<=7){
			return MappingType.DISJOINT;
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

}
