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
	
	/**
	 * Wraps the strategy's next action using the random layer.
	 */
	@Override
	public GuesserAction nextAction() {
		GuesserAction stratAction = strategy.nextAction();
		// Don't do any encoding if we're debugging to make debugging a logic
		// error in a strategy less painful.
		if (DEBUG) {
			return stratAction;
		}
		// Determine the type of the GuesserAction and decode or encode as
		// appropriate.
		if (stratAction.getType() == "g") {
			return decodeGuess(stratAction);
		} else {
			return encodeQuery(stratAction);
		}
	}
	
	/**
	 * Converts an encoded guess to a guess that corresponds with the mapper's
	 * real scheme.
	 * 
	 * @param action the encoded action.
	 * @return corrected GuesserAction.
	 */
	private GuesserAction decodeGuess(GuesserAction action) {
		Integer[] decodedGuess = new Integer[mappingLength];
		for (int i = 1; i <= mappingLength; i++) {
			int newIndex = randomLayer.getMapping(i) - 1;
			decodedGuess[newIndex] = action.getContent().get(i - 1);
		}
		return new GuesserAction("g",
					new ArrayList<Integer>(Arrays.asList(decodedGuess)));
	}
	
	/**
	 * Encodes a query. This makes it more difficult for the mapper from 
	 * determining if we're following a given strategy. Granted, some strategies
	 * will always be easy to detect (e.g. the pairwise strategy), but this will
	 * provide some safety against mappers that optimize for guessers that 
	 * guess certain numbered patterns.
	 * 
	 * @param action the encoded action.
	 * @return corrected GuesserAction.
	 */
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
