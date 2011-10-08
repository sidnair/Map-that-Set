package mapthatset.g5;

import java.util.ArrayList;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G5Guesser extends Guesser {
	
	private final boolean DEBUG = false;
	private Strategy strategy = new BadPairsStrategy(DEBUG);
		
	public void startNewMapping(int mappingLength) {
		strategy.startNewMapping(mappingLength);
	}
	
	@Override
	public GuesserAction nextAction() {
		return strategy.nextAction();
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
