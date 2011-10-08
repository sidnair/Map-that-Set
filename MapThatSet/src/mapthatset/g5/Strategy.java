package mapthatset.g5;

import java.util.ArrayList;

import mapthatset.sim.GuesserAction;

public abstract class Strategy {
	
	protected final boolean DEBUG;
	
	public Strategy(boolean debug) {
		this.DEBUG = debug;
	}
	
	public abstract void startNewMapping(int mappingLength);
	
	public abstract GuesserAction nextAction();
	
	public abstract void setResult(ArrayList<Integer> result);

}
