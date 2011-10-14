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
	}

	@Override
	public GuesserAction nextAction() {
		if (stratKnown) {
			return currentStrat.nextAction();
		} else {
			currentGuess = new ArrayList<Integer>();
			for (int i = 1; i <= mappingLength; i++) {
				currentGuess.add(i);
			}
			return new GuesserAction("q", currentGuess);
		}
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
			case OTHER:
//				currentStrat = new CrossStrategy(DEBUG);
				currentStrat = new DisjointStrategy(DEBUG);
				break;
			}
			currentStrat.startNewMapping(mappingLength);
			stratKnown = true;
		}
	}

	private MappingType determineMappingType(ArrayList<Integer> result,
			ArrayList<Integer> guess) {
		if (result.size() == mappingLength) {
			return MappingType.PERM;
		}
		if (result.size() == 2) {
			return MappingType.BINARY;
		}
		return MappingType.OTHER;
	}

}
