package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G5Guesser extends Guesser {

	private int mappingLength;
	private int intLastQueryIndex = 0;
	private int iNeedAnotherIntAndImTooTiredToNameIt = 0;
	private Map <Integer, TreeSet<Integer>> possibleMappings;
	private TreeSet <Integer> notSolvedYet;
	private ArrayList<Integer> guess = new ArrayList<Integer>();
		
	public void startNewMapping(int intMappingLength) {
		this.mappingLength = intMappingLength;
		intLastQueryIndex = 0;
		guess = new ArrayList<Integer>();
		possibleMappings = new HashMap <Integer, TreeSet<Integer>> ();
		notSolvedYet = new TreeSet  <Integer> ();
		
		// I dont like this, but its good for now...
		for(int i = 1; i <= intMappingLength; ++i){
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
		if(intLastQueryIndex < mappingLength){
			ArrayList <Integer> currentQuery = new ArrayList <Integer>();
			currentQuery.add(++intLastQueryIndex);
			if(intLastQueryIndex < mappingLength)
				currentQuery.add(++intLastQueryIndex);
			else{
				++intLastQueryIndex;
			}
			iNeedAnotherIntAndImTooTiredToNameIt = intLastQueryIndex;
			return new GuesserAction("q", currentQuery);
		}
		for(int i : possibleMappings.keySet()){
			System.out.print(i + ": ");
			for(int j : possibleMappings.get(i)){
				System.out.print(j + " ");
			}
			System.out.println();
		}
		// I need a better way to do this
		TreeSet <Integer> tempTreeSet = new TreeSet <Integer>();
		while(!notSolvedYet.isEmpty()){
			int current = notSolvedYet.pollFirst();
			if(possibleMappings.get(current).size() == 1){
				guess.remove(current - 1);
				guess.add(current - 1, possibleMappings.get(current).first());
			}
			else
				tempTreeSet.add(current);
		}
		notSolvedYet = tempTreeSet;
		if(notSolvedYet.isEmpty()){
			return new GuesserAction("g", guess);
		}
		iNeedAnotherIntAndImTooTiredToNameIt = notSolvedYet.first();
		intLastQueryIndex+=10;
		ArrayList <Integer> currentQuery = new ArrayList <Integer>();
		currentQuery.add(iNeedAnotherIntAndImTooTiredToNameIt);
		return new GuesserAction("q", currentQuery);
		
		
		//return null;
	}
	
	@Override
	public void setResult( ArrayList< Integer > alResult ) {
		if ((intLastQueryIndex-2) < mappingLength) {
			for (int i = 0; i < alResult.size(); ++i) {
				TreeSet<Integer> temp = (TreeSet<Integer>) possibleMappings.get(intLastQueryIndex - 1);
				temp.add(alResult.get(i));
				if (intLastQueryIndex <= mappingLength) {
					TreeSet<Integer> temp2 = (TreeSet<Integer>) possibleMappings.get(intLastQueryIndex);
					temp2.add(alResult.get(i));
				}
			}
		}
		else{
			for (int i = 0; i < alResult.size(); ++i) {
				TreeSet<Integer> temp = (TreeSet<Integer>) possibleMappings.get(iNeedAnotherIntAndImTooTiredToNameIt);
				temp.clear();
				temp.add(alResult.get(i));
				temp = (TreeSet<Integer>) possibleMappings.get(iNeedAnotherIntAndImTooTiredToNameIt + 1);
				if(temp != null)
					System.out.println(temp.remove(alResult.get(i)));
			}
		}
	}
	
	@Override
	public String getID() {
		return "G5Guesser";
	}

}
