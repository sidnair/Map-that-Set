package mapthatset.g5;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import mapthatset.sim.GuesserAction;

public class LowNStrategy extends Strategy {
	
	private int mappingSize;
	private int counter;
	private Vector<QueriesResponses> previousResults;
	private ArrayList<Integer> guess;
	private boolean guessed;
	
	private class QueriesResponses {
		private TreeSet<Integer> query = new TreeSet<Integer>();
		private TreeSet<Integer> response = new TreeSet<Integer>();
		
		
		public void setQuery(ArrayList<Integer> input){
			query.clear();
			query.addAll(input);
		}
		public void setResponse(ArrayList<Integer> input){
			response.clear();
			response.addAll(input);
		}
		
		@SuppressWarnings("unchecked")
		public ArrayList<Integer> TStoAL(char c){
			TreeSet<Integer> temp;
			ArrayList<Integer> result = new ArrayList<Integer>();
			if(c == 'q'){
				temp = (TreeSet<Integer>) query.clone();
			}else{
				temp = (TreeSet<Integer>) response.clone();
			}
			Integer i = temp.pollFirst();
			while(i != null){
				result.add(i);
				i = temp.pollFirst();
			}
			
			return result;
		}
	}
	
	public LowNStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		mappingSize = mappingLength;
		previousResults = new Vector<QueriesResponses>();
		guess = new ArrayList<Integer>();
		guessed = false;
		counter = 0;
		
		for(int i = 1; i <= mappingLength; ++i){
			guess.add(0);
		}
		
	}
	
	@Override
	protected void startNewMapping(int mappingLength, ArrayList<Integer> query,
			ArrayList<Integer> result) {
		// Ignore passed in results - we already know what this will be
		// since it's a perm.
		startNewMapping(mappingLength);
	}

	@Override
	public GuesserAction nextAction() {
		ArrayList<Integer> query = new ArrayList<Integer>();
		// If there are still items left to query, add them to the query arraylist
		while(counter < mappingSize){
			query.add(counter + 1);
			++counter;
			// If we have 2 numbers to query, dont add any more
			if(query.size() > 1){
				break;
			}
		}
		// If there is something to query, query it and log the query
		if(query.size() > 0){
			QueriesResponses qr = new QueriesResponses();
			qr.setQuery(query);
			previousResults.add(qr);
			return (new GuesserAction("q",query));
		}
		
		guessed = true;
		return (new GuesserAction("g",guess));
	}

	// A function to set the guess array to the correct result, index/indices given by the toSet arraylist
	public void setGuess(ArrayList<Integer> toSet, ArrayList<Integer> result) {
		guess.remove(toSet.get(0) - 1);
		guess.add(toSet.get(0) - 1, result.get(0));
		if (toSet.size() > 1) {
			guess.remove(toSet.get(1) - 1);
			guess.add(toSet.get(1) - 1, result.get(0));
		}
	}
	
	@Override
	public void setResult(ArrayList<Integer> result) {
		if(guessed){
			return;
		}
		// Log the response with the correct query
		QueriesResponses qr = previousResults.lastElement();
		qr.setResponse(result);
		// If we only got one result, we have some work to do.
		if(result.size() == 1){
			ArrayList<Integer> toSet = previousResults.lastElement().TStoAL('q');
			// Set the indices of guess of the previous query to our one result
			this.setGuess(toSet, result);
			// Now, go backwards to figure out the previous numbers of guess
			while(!previousResults.isEmpty()){
				// Fully because I am lazy
				if(toSet.size() == 1){
					toSet.add(toSet.get(0));
				}
				
				QueriesResponses lookIn = previousResults.lastElement();
				ArrayList<Integer> lookInQueries = lookIn.TStoAL('q');
				// If the query was of one element, we've already modified guess to include its value
				if(lookInQueries.size() == 1){
					previousResults.remove(lookIn);
					continue;
				}
				// If the result was of one element, we've already modified guess to include its value
				ArrayList<Integer> lookInResults = lookIn.TStoAL('r');
				if(lookInResults.size() == 1){
					previousResults.remove(lookIn);
					continue;
				}
				// Remove the right element, and modify the other
				if(lookInQueries.contains(toSet.get(0))){
					lookInQueries.remove(toSet.get(0));
					lookInResults.remove(guess.get(toSet.get(0) - 1));
					this.setGuess(lookInQueries, lookInResults);
					previousResults.remove(lookIn);
					toSet = lookInQueries;
				} else if(lookInQueries.contains(toSet.get(1))){
					lookInQueries.remove(toSet.get(1));
					lookInResults.remove(guess.get(toSet.get(1) - 1));
					this.setGuess(lookInQueries, lookInResults);
					previousResults.remove(lookIn);
					toSet = lookInQueries;
				} else {
					break;
				}
			}
		} else {
			--counter;
		}
	}

}
