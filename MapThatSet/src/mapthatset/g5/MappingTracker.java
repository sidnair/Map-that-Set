package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MappingTracker {
	
	private Map<ArrayList<Integer>, ArrayList<Integer>> pastQueries;
	private Map<Integer, Set<Integer>> possibleMappings;
	private final int mappingLength;
	private boolean firstReduction;
	
	public MappingTracker(int mappingLength) {
		pastQueries = new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
		this.mappingLength = mappingLength;
		firstReduction = true;
		initPossibleMappings();
	}
	
	public void updateTracker(ArrayList<Integer> result,
			ArrayList<Integer> currentQuery) {
		pastQueries.put(currentQuery, result);
		reducePossibilities(currentQuery, result);
		for (ArrayList<Integer> q : pastQueries.keySet()) {
			reducePossibilities(q, pastQueries.get(q));
		}
	}
	
	private void reducePossibilities(ArrayList<Integer> query,
			ArrayList<Integer> result) {
		// Maps one of the return values to each of the possible numbers that 
		// map to it.
		Map<Integer, Set<Integer>> coverage =
				new HashMap<Integer, Set<Integer>>(); 
		// Initialize coverage.
		for (int possibility : result) {
			Set<Integer> possibleMappingValues = new HashSet<Integer>();
			for (int i : query) {
				if (firstReduction || 
						possibleMappings.get(i).contains(possibility)) {
					possibleMappingValues.add(i);
				}
			}
			coverage.put(possibility, possibleMappingValues);
		}
		
		if (firstReduction) {
			firstReduction = false;
			for (int i : query) {
				updatePossibleValues(i, coverage);
			}
			return;
		}
		
		/*
		 * Next, if any of the integers in the result (the integers which
		 * are mapped to) have only one integer, x, that might map to it in 
		 * the guess, we know that x must be used for this mapping. Thus,
		 * we can remove x from all other possible mappings. We repeat this
		 * n times to make sure that the process of reducing possible
		 * mappings is complete. Then, we update the possible results for
		 * each of the integer. 
		 */
		// Must iterate for |result| times for this logic to be accurate.
		for (int i = 0; i < result.size(); i++) {
			for (int q : query) {
				updatePossibleValues(q, coverage);
				for (int mappedTo : coverage.keySet()) {
					// The integer in the query can only be mapped to this int.
					if (possibleMappings.get(q).size() == 1 && 
							(Integer) possibleMappings.get(q).toArray()[0] == mappedTo) {
						removeMappings(coverage, mappedTo, q);
					}
				}				
			}
			for (int mappedTo : coverage.keySet()) {
				Set<Integer> possibleMappers = coverage.get(mappedTo);
				if (possibleMappers.size() == 1) {
					removeMappings(coverage, mappedTo,
							(Integer) possibleMappers.toArray()[0]);
				}
			}
		}
		for (int i : query) {
			updatePossibleValues(i, coverage);
		}
	}
	
	private void updatePossibleValues(int i, Map<Integer, Set<Integer>> coverage) {
		Set<Integer> possibleValues = new HashSet<Integer>();
		for (int r : coverage.keySet()) {
			if (coverage.get(r).contains(i)) {
				possibleValues.add(r);
			}
		}
		possibleMappings.put(i, possibleValues);
	}

	private void removeMappings(Map<Integer, Set<Integer>> coverage,
			int mappedTo, int mapped) {
		for (int i : coverage.keySet()) {
			if (i != mappedTo) {
				coverage.get(i).remove(mapped);
			}
		}
	}
	
	/**
	 * Modifies possibleMappings so that every integer maps to every other
	 * integer.
	 */
	private void initPossibleMappings() {
		possibleMappings = new HashMap<Integer, Set<Integer>>();
		for (int i = 1; i <= mappingLength; i++) {
			Set<Integer> possibleValues = new HashSet<Integer>();
			for (int j = 1; j <= mappingLength; j++) {
				possibleValues.add(j);
			}
			possibleMappings.put(i, possibleValues);
		}
	}
	
	public ArrayList<Integer> getCorrectMapping() {
		ArrayList<Integer> mapping = new ArrayList<Integer>();
		for (int i = 1; i <= mappingLength; i++) {
			mapping.add((Integer) possibleMappings.get(i).toArray()[0]); 
		}
		return mapping;
	}
	
	public boolean isMappingKnown() {
		for (int i = 1; i <= mappingLength; i++) {
			int possibilities = possibleMappings.get(i).size();
			// 0 before first reduction
			if (possibilities > 1 || possibilities == 0) {
				return false;
			}
		}
		return true;
	}
	
	
	public Map<Integer, Set<Integer>> getPossibleMappings() {
		return possibleMappings;
	}
	
	/* Intersect a mapping with each of a list of other mappings. 
	public void intersectMappings(
			ArrayList<Map<Integer, Set<Integer>>> others) {
		for (Map<Integer, Set<Integer>> m : others) {
			for (int i : m.keySet()) {
				if (possibleMappings.get(i) != null) {
					possibleMappings.get(i).retainAll(m.get(i));
				}
			}
		}
	}
	*/
	
	public Map<Integer, Set<Integer>> getCoverage() {
		Map<Integer, Set<Integer>> coverage =
				new HashMap<Integer, Set<Integer>>();
		for (int i = 1; i <= mappingLength; i++) {
			coverage.put(i, new HashSet<Integer>());
		}
		// Initialize coverage.
		for (int i = 1; i <= mappingLength; i++) {
			Set<Integer> possibleMappingValues = possibleMappings.get(i);
			// j is numbers mapped to
			for (int j : possibleMappingValues) {
				// Updates set in map by reference
				coverage.get(j).add(i);
			}
		}
		return coverage;
	}
	
	public boolean isKnown(int i) {
		return possibleMappings.get(i).size() == 1;
	}
	
	public void solve(Integer mapper, Integer mappedTo) {
		if (!possibleMappings.get(mapper).contains(mappedTo)) {
			System.err.println("\nSanity check failed...\n");
			System.exit(1);
		}
		Set<Integer> answer = new HashSet<Integer>();
		answer.add(mappedTo);
		possibleMappings.put(mapper, answer);
	}

}
