package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import mapthatset.sim.GuesserAction;


/**
 *  TODO - give an overview of this strategy. What are the pros and cons, and
 *  what needs to be implemented?
 */
public class BadPairsStrategy extends Strategy {

	private int mappingLength;
	private int lastQueryIndex;

	// I'll figure out what this int actually does later, for now it just is.
	private int iNeedAnotherIntAndImTooTiredToNameIt;

	// Mapping from the integers 1-n to the set of possibilities that number can be mapped to
	private Map<Integer, TreeSet<Integer>> possibleMappings;
	
	// The set of integers 1-n that we do not have a concrete mapping for
	private TreeSet<Integer> notSolvedYet;
	
	// The arraylist we are using to submit our guess
	private ArrayList<Integer> guess;
	
	public BadPairsStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		// Initializing variables
		this.mappingLength = mappingLength;
		guess = new ArrayList<Integer>();
		possibleMappings = new HashMap<Integer, TreeSet<Integer>> ();
		notSolvedYet = new TreeSet<Integer>();
		
		// Set up the possible mappings hash map and add everything to the notSolvedYet set.
		// Also, initialize guess to all 0's, so we don't have to figure out the mappings in order.
		for(int i = 1; i <= mappingLength; ++i){
			possibleMappings.put(i, new TreeSet<Integer>());
			notSolvedYet.add(i);
			guess.add(0);
		}
	}

	@Override
	public GuesserAction nextAction() {
		// If we haven't already gone through the entire list once, go through the next two
		if (lastQueryIndex < mappingLength) {
			// Create an arraylist to submit our query
			ArrayList <Integer> currentQuery = new ArrayList <Integer>();
			// Add the next integer to the queue
			currentQuery.add(++lastQueryIndex);
			// Check to see if we can add another integer, or if we have an odd number
			if (lastQueryIndex < mappingLength) {
				currentQuery.add(++lastQueryIndex);
			} else{
				// This probably isnt important now, but I don't have time right now to check it
				++lastQueryIndex;
			}
			// Same as above comment
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
		// Create a temp treeSet so that I can do destructive polling to the original
		TreeSet<Integer> tempTreeSet = new TreeSet <Integer>();
		// If there are Integers in the notSolvedYet set, check to see if we can take any of them out
		while (!notSolvedYet.isEmpty()) {
			// Get the first integer and check if the set of possible mappings for it contains only one option
			//		If it doesn't, add the integer to the temp treeSet
			int current = notSolvedYet.pollFirst();
			if(possibleMappings.get(current).size() == 1){
				// Remove the placeholder 0 and add the mapping into our guess arraylist
				guess.remove(current - 1);
				guess.add(current - 1, possibleMappings.get(current).first());
			} else {
				tempTreeSet.add(current);
			}
		}
		// notSolvedYet is empty, so lets point it at the right thing
		notSolvedYet = tempTreeSet;
		// If its still empty, lets celebrate!  I'll grab the beers
		if (notSolvedYet.isEmpty()) {
			return new GuesserAction("g", guess);
		}
		// Use the fun int to tell us which integer we are looking at next
		iNeedAnotherIntAndImTooTiredToNameIt = notSolvedYet.first();
		// Probably obsolete...
		lastQueryIndex += 10;
		// Query for that integer
		ArrayList<Integer> currentQuery = new ArrayList<Integer>();	
		currentQuery.add(iNeedAnotherIntAndImTooTiredToNameIt);
		return new GuesserAction("q", currentQuery);
	}

	@Override
	public void setResult(ArrayList<Integer> result) {
		// If this gets executed, then we are still querying for the initial pairs.
		//		Sets the possible mappings to be equal to the result arraylist.
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
		// This part handles the updating of the possible mappings set.
		// Since at this point any integer we are querying for will have two 
		// possibilities in its set, I just clear them both and put in the on
		// that we want.  At the same time, I remove that possibility from the
		// next integer.  ie: [1, 2] -> [3, 4], if we then query [1] -> [4] 
		// we know that [2]->[3]. This will have to change if we get more 
		// complicated, but its not too hard to do.
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

}