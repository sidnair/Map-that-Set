package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G5Guesser extends Guesser {

	private int mappingLength;
	private int intLastQueryIndex = 0;
	private Map <Integer, Set<Integer>> possibleMappings;
	private Set <Integer> notSolvedYet;
	private ArrayList<Integer> guess = new ArrayList<Integer>();
		
	public void startNewMapping(int intMappingLength) {
		this.mappingLength = intMappingLength;
		intLastQueryIndex = 0;
		guess = new ArrayList<Integer>();
		possibleMappings = new HashMap <Integer, Set<Integer>> ();
		notSolvedYet = new HashSet  <Integer> ();
		
		// I dont like this, but its good for now...
		for(int i = 1; i <= intMappingLength; ++i){
			possibleMappings.put(i, new HashSet<Integer>());
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
			currentQuery.add(++intLastQueryIndex);
			return new GuesserAction("q", currentQuery);
		}
		for(int i : possibleMappings.keySet()){
			System.out.print(i + ": ");
			for(int j : possibleMappings.get(i)){
				System.out.print(j + " ");
			}
			System.out.println();
		}
		Iterator <Integer> iter = notSolvedYet.iterator();
		while(iter.hasNext()){
			int current = iter.next();
			Object[] arr = possibleMappings.get(current).toArray();
			if(arr.length == 1){
				guess.add(current - 1, (Integer) arr[0]);
				notSolvedYet.remove(current);
			}
		}
		if(notSolvedYet.isEmpty()){
			return new GuesserAction("g", guess);
		}
		
		
		
		return null;
	}
	
	@Override
	public void setResult( ArrayList< Integer > alResult ) {
		for(int i=0; i < alResult.size(); ++i){
			HashSet <Integer> temp = (HashSet<Integer>) possibleMappings.get(intLastQueryIndex - 1);
			HashSet <Integer> temp2 = (HashSet<Integer>) possibleMappings.get(intLastQueryIndex);
			temp.add(alResult.get(i));
			temp2.add(alResult.get(i));
		}
	}
	
	@Override
	public String getID() {
		return "G5Guesser";
	}

}
