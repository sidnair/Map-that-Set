package mapthatset.aiplayer.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Knowledge implements Comparable<Knowledge> {
	private Set<Integer> preimage;
	private Set<Integer> image;
	private int D;
	private int recency;
	
	private Map<String, Set<Integer>> pairings = new HashMap<String, Set<Integer>>();
	
	private static int recencyCounter = 0;
	
	public Knowledge(Set<Integer> pi, Set<Integer> i) {
		preimage = pi;
		image = i;
		D = image.size() - preimage.size();
		recency = recencyCounter++;
	}
	
	public Set<Integer> getPreimage() {
		return preimage;
	}
	
	public Set<Integer> getImage() {
		return image;
	}
	
	public int getD() {
		return D;
	}
	
	public int getRecency() {
		return recency;
	}
	
	public Set<Integer> getPairings(String c) {
		if (!pairings.containsKey(c)) {
			pairings.put(c, new HashSet<Integer>());
		}
		
		return pairings.get(c);
	}
	
	public int compareTo(Knowledge other) {
		
		if (preimage.containsAll(other.preimage) && preimage.size() == other.preimage.size() 
				&& image.containsAll(other.image) && image.size() == other.image.size()) {
			return 0;
		}
		return (new Integer(recency)).compareTo(other.getRecency());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) return false;
		if (this.compareTo((Knowledge) obj) == 0) return true;
		return false;
	}
	
	public String toString() {
		String s = "[";
		for (int p : preimage) {
			s += p + " ";
		}
		s+= "] -> [";
		for (int i : image) {
			s += i + " ";
		}
		s += "]";
		return s;
	}
}
