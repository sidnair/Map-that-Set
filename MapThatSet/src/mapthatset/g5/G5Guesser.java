package mapthatset.g5;

import java.util.ArrayList;
import java.util.Arrays;
import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G5Guesser extends Guesser {
	
	private final boolean DEBUG = false;
	private Strategy strategy = new BadPairsStrategy(DEBUG);
	private RandomLayer randomLayer;
	
	private int mappingLength;
		
	public void startNewMapping(int mappingLength) {
		strategy.startNewMapping(mappingLength);
		randomLayer = new RandomLayer(mappingLength);
		
		this.mappingLength = mappingLength;
	}
	
	@Override
	public GuesserAction nextAction() {
		GuesserAction stratAction = strategy.nextAction();
		if (stratAction.getType() == "g") {
			return decodeGuess(stratAction);
		} else {
			return encodeQuery(stratAction);
		}
	}
	
	private GuesserAction decodeGuess(GuesserAction action) {
		Integer[] decodedGuess = new Integer[mappingLength];
		for (int i = 1; i <= mappingLength; i++) {
			int newIndex = randomLayer.getMapping(i) - 1;
			decodedGuess[newIndex] = action.getContent().get(i - 1);
		}
		return new GuesserAction("g",
					new ArrayList<Integer>(Arrays.asList(decodedGuess)));
	}
	
	private GuesserAction encodeQuery(GuesserAction action) {
		ArrayList<Integer> obfuscatedGuess = new ArrayList<Integer>();
		for (int i : action.getContent()) {
			obfuscatedGuess.add(randomLayer.getMapping(i));
		}	
		return new GuesserAction("q", obfuscatedGuess);
	}
	
	@Override
	public void setResult(ArrayList<Integer> result) {
		strategy.setResult(result);
	}
	
	@Override
	public String getID() {
		return "G5Guesser";
	}

}
