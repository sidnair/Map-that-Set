package mapthatset.g5;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class ThreeStrategy extends Strategy {
	
	private int counter;
	private int result12[];
	private int result23[];
	private int result3;
	private ArrayList<Integer> guess;
	
	public ThreeStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		guess = new ArrayList<Integer>();
		counter = 0;
		result12 = new int[2];
		result23 = new int[2];
		result3 = 0;
		
		
		for(int i = 1; i <= 3; ++i){
			guess.add(0);
		}
		
	}

	@Override
	public GuesserAction nextAction() {
		ArrayList<Integer> query = new ArrayList<Integer>();
		int resultTemp = result3;
		switch(counter){
		case 0:
			query.add(new Integer(1));
			query.add(new Integer(2));
			++counter;
			return (new GuesserAction("q", query));
		case 1:
			query.add(new Integer(2));
			query.add(new Integer(3));
			++counter;
			return (new GuesserAction("q", query));
		case 2:
			query.add(new Integer(3));
			++counter;
			return (new GuesserAction("q", query));
		case 3:
			if(!guess.contains(0)){
				break;
			}
			if(result23[0] == result3){
				resultTemp = result23[1];
				guess.remove(1);
				guess.add(1, result23[1]);
			} else {
				resultTemp = result23[0];
				guess.remove(1);
				guess.add(1, result23[0]);
			}
			
		case 4:
			if(result12[0] == resultTemp){
				guess.remove(0);
				guess.add(0, result12[1]);
			} else {
				guess.remove(0);
				guess.add(0, result12[0]);
			}
		}
		
		return (new GuesserAction("g",guess));
	}

	@Override
	public void setResult(ArrayList<Integer> result) {
		switch (counter) {
		case 1:
			if (result.size() == 1) {
				guess.remove(0);
				guess.add(0, result.get(0));
				guess.remove(1);
				guess.add(1, result.get(0));
				counter = 2;
			} else {
				result12[0] = result.get(0);
				result12[1] = result.get(1);
			}
			break;
		case 2:
			if (result.size() == 1) {
				result3 = result.get(0);
				guess.remove(1);
				guess.add(1, result3);
				guess.remove(2);
				guess.add(2, result3);
				counter = 4;
			} else {
				result23[0] = result.get(0);
				result23[1] = result.get(1);
			}
			break;
		case 3:
			result3 = result.get(0);
			guess.remove(2);
			guess.add(2, result3);
			break;
		default:
			break;
		}
	}

	@Override
	protected void startNewMapping(int mappingLength, ArrayList<Integer> query,
			ArrayList<Integer> result) {
		System.err.println("This should not be called after an initial query.");
		System.exit(1);
	}
}