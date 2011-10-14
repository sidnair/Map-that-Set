package mapthatset.g5;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import mapthatset.sim.GuesserAction;

public class PermStrategy extends Strategy {
	
	private int mappingSize;
	private int arraySplitSpacing;
	private int counter;
	private Vector<QueriesResponses> previousResults;
	private ArrayList<Integer> guess;
	private int[] placeKeeperArray;
	
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
		public TreeSet<Integer> getQuery() {
			return query;
		}
		public TreeSet<Integer> getResponse() {
			return response;
		}
	}
	
	private static ArrayList<Integer> intersection (ArrayList<Integer> first, ArrayList<Integer> second){
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.addAll(first);
		
		for(int i = result.size() - 1; i >= 0; --i){
			if(!second.contains(result.get(i))){
				result.remove(i);
			}
		}
		
		return result;
	}
	
	private static ArrayList<Integer> union (ArrayList<Integer> first, ArrayList<Integer> second){
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.addAll(first);
		
		for(Integer i : second){
			if(!result.contains(i)){
				result.add(i);
			}
		}
		
		return result;
	}
	
	public PermStrategy(boolean debug) {
		super(debug);
	}

	@Override
	public void startNewMapping(int mappingLength) {
		mappingSize = mappingLength;
		previousResults = new Vector<QueriesResponses>();
		guess = new ArrayList<Integer>();
		arraySplitSpacing = (int) Math.ceil(mappingLength/2.0);
		placeKeeperArray = new int[mappingLength];
		counter = 0;
		
		for(int i = 1; i <= mappingLength; ++i){
			guess.add(0);
			placeKeeperArray[i-1] = i;
		}
		
	}

	@Override
	public GuesserAction nextAction() {
		System.out.println("nextGuess!");
		while(arraySplitSpacing > 0){
			counter = 0;
			ArrayList<Integer> query = new ArrayList<Integer>();
			boolean add = true;
			while((counter+arraySplitSpacing) < mappingSize){
				if(add){
					for(int i = counter; i < arraySplitSpacing+counter; ++i){
						query.add(placeKeeperArray[i]);
					}
				}
				counter += arraySplitSpacing;
				add = !add;
			}
			arraySplitSpacing /= 2;
			QueriesResponses qr = new QueriesResponses();
			qr.setQuery(query);
			previousResults.add(qr);
			return (new GuesserAction("q", query));
		}
		
		for(int i = 1; i <= mappingSize; ++i){
			ArrayList<Integer> intersect = new ArrayList<Integer>();
			QueriesResponses qr = null;
			for(int j = 0; j < previousResults.size(); ++j){
				if(previousResults.get(j).getQuery().contains(i)){
					qr = previousResults.get(j);
					intersect = qr.TStoAL('r');
					break;
				}
			}
			for(int j = 0; j < previousResults.size(); ++j){
				if(previousResults.get(j).getQuery().contains(i)){
					intersect = intersection(intersect, previousResults.get(j).TStoAL('r'));
				}
			}
			if(qr == null){
				for(int j = 1; j <= mappingSize; ++j){
					if(!guess.contains(new Integer(j))){
						intersect.add(new Integer(j));
						break;
					}
				}
			}
			if(intersect.size() == 1){
				guess.remove(i-1);
				guess.add(i-1, intersect.get(0));
			} else {
				for(int j = intersect.size() - 1; j >= 0; --j){
					if(guess.contains(intersect.get(j))){
						intersect.remove(j);
					}
				}
				guess.remove(i-1);
				guess.add(i-1, intersect.get(0));
			}
			
		}
		return (new GuesserAction("g",guess));
	}

	@Override
	public void setResult(ArrayList<Integer> result) {
		QueriesResponses qr = previousResults.lastElement();
		qr.setResponse(result);
	}

}
