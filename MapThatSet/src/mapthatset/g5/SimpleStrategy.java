package mapthatset.g5;

import java.util.ArrayList;

import mapthatset.sim.GuesserAction;

/* Guesses 1...n. Gives answer in n guesses, but counting the first guess
 * from the controller, this takes N+1 guesses. This is implemented to avoid
 * timing out.
 */
public class SimpleStrategy extends Strategy {

	private ArrayList<Integer> answer;
	int nextGuess;
	private int mappingLength;

	protected SimpleStrategy(boolean debug) {
		super(debug);
	}
	
	@Override
	protected void startNewMapping(int mappingLength) {
		nextGuess = 1;
		answer = new ArrayList<Integer>();
		this.mappingLength = mappingLength;
	}

	@Override
	protected GuesserAction nextAction() {
		if (answer.size() == mappingLength) {
			return new GuesserAction("g", answer);
		}
		ArrayList<Integer> guess = new ArrayList<Integer>();
		guess.add(nextGuess);
		return new GuesserAction("q", guess);
	}

	@Override
	protected void setResult(ArrayList<Integer> result) {
		nextGuess++;
		answer.addAll(result);
	}

	@Override
	protected void startNewMapping(int mappingLength, ArrayList<Integer> query,
			ArrayList<Integer> result) {
		startNewMapping(mappingLength);
	}

	@Override
	protected boolean supportsSubProblems() {
		return false;
	}
	
	

}
