package mapthatset.g5;

import java.util.ArrayList;

import mapthatset.sim.GuesserAction;

public abstract class Strategy {

	protected final boolean DEBUG;
	
	protected Strategy(boolean debug) {
		this.DEBUG = debug;
	}
	
	//Changed to protected, otherwise other groups can call these methods.
	protected abstract void startNewMapping(int mappingLength);
	
	protected abstract GuesserAction nextAction();
	
	protected abstract void setResult(ArrayList<Integer> result);
	protected abstract void startNewMapping(int mappingLength,
			ArrayList<Integer> query, ArrayList<Integer> result);

}
