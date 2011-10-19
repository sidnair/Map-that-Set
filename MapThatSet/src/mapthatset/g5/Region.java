package mapthatset.g5;

import java.util.ArrayList;
import java.util.Set;

public class Region {
	protected ArrayList<Integer> domain;
	protected ArrayList<Integer> range;
	
	public Region(Set<Integer> domainSet, Set<Integer> rangeSet) {
		this.domain = new ArrayList<Integer>(domainSet);
		this.range = new ArrayList<Integer>(rangeSet); 
	}
}