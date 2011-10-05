package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G5Guesser extends Guesser {

	private int mappingLength;
	private int lastQueryIndex;
	private int iNeedAnotherIntAndImTooTiredToNameIt;
	private Map<Integer, TreeSet<Integer>> possibleMappings;
	private TreeSet<Integer> notSolvedYet;
	private ArrayList<Integer> guess;
	
	private final boolean DEBUG = true;
		
	public void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		guess = new ArrayList<Integer>();
		possibleMappings = new HashMap<Integer, TreeSet<Integer>> ();
		notSolvedYet = new TreeSet<Integer>();
		
		// I dont like this, but its good for now...
		for(int i = 1; i <= mappingLength; ++i){
			possibleMappings.put(i, new TreeSet<Integer>());
			notSolvedYet.add(i);
			guess.add(0);
			/*HashSet<Integer> temp = (HashSet<Integer>) possibleMappings.get(i);
			for(int j = 1; j < intMappingLength; ++j){
				temp.add(j);
			}*/
		}
	}
	
	@Override
	public GuesserAction nextAction() {
		if (lastQueryIndex < mappingLength) {
			ArrayList <Integer> currentQuery = new ArrayList <Integer>();
			currentQuery.add(++lastQueryIndex);
			if (lastQueryIndex < mappingLength) {
				currentQuery.add(++lastQueryIndex);
			} else{
				++lastQueryIndex;
			}
			iNeedAnotherIntAndImTooTiredToNameIt = lastQueryIndex;
			return new GuesserAction("q", currentQuery);
		}
		for (int i : possibleMappings.keySet()) {
			if (DEBUG) {
				System.out.print(i + ": ");
				for (int j : possibleMappings.get(i)) {
					System.out.print(j + " ");
				}
				System.out.println();
			}
		}
		// I need a better way to do this
		TreeSet<Integer> tempTreeSet = new TreeSet <Integer>();
		while (!notSolvedYet.isEmpty()) {
			int current = notSolvedYet.pollFirst();
			if(possibleMappings.get(current).size() == 1){
				guess.remove(current - 1);
				guess.add(current - 1, possibleMappings.get(current).first());
			} else {
				tempTreeSet.add(current);
			}
		}
		notSolvedYet = tempTreeSet;
		if (notSolvedYet.isEmpty()) {
			return new GuesserAction("g", guess);
		}
		iNeedAnotherIntAndImTooTiredToNameIt = notSolvedYet.first();
		lastQueryIndex += 10;
		ArrayList<Integer> currentQuery = new ArrayList<Integer>();	
		currentQuery.add(iNeedAnotherIntAndImTooTiredToNameIt);
		return new GuesserAction("q", currentQuery);
	}
	
	@Override
	public void setResult(ArrayList<Integer> result) {
		if ((lastQueryIndex - 2) < mappingLength) {
			for (int i = 0; i < result.size(); i++) {
				TreeSet<Integer> temp = (TreeSet<Integer>)
						possibleMappings.get(lastQueryIndex - 1);
				temp.add(result.get(i));
				if (lastQueryIndex <= mappingLength) {
					TreeSet<Integer> temp2 = (TreeSet<Integer>)
							possibleMappings.get(lastQueryIndex);
					temp2.add(result.get(i));
				}
			}
		} else {
			for (Integer res : result) {
				TreeSet<Integer> temp =
						(TreeSet<Integer>)
						possibleMappings.get(iNeedAnotherIntAndImTooTiredToNameIt);
				temp.clear();
				temp.add(res);
				temp = (TreeSet<Integer>)possibleMappings.get(
						iNeedAnotherIntAndImTooTiredToNameIt + 1);
				if (DEBUG) {
					if(temp != null) {
						System.out.println(temp.remove(res));
					}
				}
			}
		}
	}
	
	@Override
	public String getID() {
		return "G5Guesser";
	}

}
