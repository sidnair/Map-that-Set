package mapthatset.g5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisjointGraphFinder {
	
	public static ArrayList<Region> findDisjointGraphs(int mappingLength,
			MappingTracker mappingTracker) {
		int[] mappeeIndeces = new int[mappingLength];
		ArrayList<Set<Integer>> mapperToMappees = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> mapperToGroupedMappers =
				new ArrayList<Set<Integer>>();
		for (int i = 0 ; i < mappeeIndeces.length; i++) {
			mappeeIndeces[i] = -1;
		}
		Map<Integer, Set<Integer>> coverage = mappingTracker.getCoverage();
		List<Integer> keySet = new ArrayList<Integer>(coverage.keySet());
		Collections.sort(keySet);
		for (Integer mappedTo: keySet) {
			mapperToMappees.add(new HashSet<Integer>());
			mapperToGroupedMappers.add(new HashSet<Integer>());
			Set<Integer> possibleMappees = coverage.get(mappedTo);
			for (int i : possibleMappees) {
				mapperToMappees.get(mappedTo - 1).add(i);
				if (mappeeIndeces[i-1] == -1) {
					mappeeIndeces[i-1] = mappedTo;
				} else {
					mapperToGroupedMappers.get(mappedTo - 1)
						.add(mappeeIndeces[i-1]);
				}
			}
		}
				
		ArrayList<Set<Integer>> mapperGroups = new ArrayList<Set<Integer>>();
		for (int i = 0; i < mapperToGroupedMappers.size(); i++) {
			if (mapperToMappees.get(i).size() == 0) {
				mapperToGroupedMappers.set(i, null);
				continue;
			}
			if (mapperToGroupedMappers.get(i).size() == 0) {
				Set<Integer> singleton = new HashSet<Integer>();
				singleton.add(i + 1);
				mapperGroups.add(singleton);
			} else {
				Iterator<Set<Integer>> iter = mapperGroups.iterator();
				Set<Integer> fullGroup = new HashSet<Integer>();
				fullGroup.add(i+1);
				while(iter.hasNext()) {
					Set<Integer> group = iter.next();
					for (int same : mapperToGroupedMappers.get(i)) {
						if (group.contains(same)) {
							fullGroup.addAll(group);
							iter.remove();
							break;
						}	
					}
				}
				mapperGroups.add(fullGroup);
			}
		}
		
		
		ArrayList<Region> regions = new ArrayList<Region>();
		for (Set<Integer> group : mapperGroups) {
			if (group.size() == 1) {
				continue;
			}
			Set<Integer> mappeeSet = new HashSet<Integer>();
			for (int mappedTo : group) {
				mappeeSet.addAll(mapperToMappees.get(mappedTo -1));
			}
			
			regions.add(new Region(mappeeSet, group));
		}
		return regions;

	}

	
}
