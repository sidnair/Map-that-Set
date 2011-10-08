package mapthatset.aiplayer.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtil<T> {
	public Set<T> intersect(Set<T> s1, Set<T> s2) {
		Set<T> copy = new HashSet<T>();
		copy.addAll(s1);
		
		copy.retainAll(s2);
		
		return copy;
	}
	
	public Set<T> difference(Set<T> s1, Set<T> s2) {
		Set<T> copy = new HashSet<T>();
		copy.addAll(s1);
		
		copy.removeAll(s2);
		
		return copy;
	}
	
	public static Set<Integer> parseIntSet(String s) {
		Set<Integer> ints = new HashSet<Integer>();
		for (String c : s.split(",")) {
			ints.add(Integer.parseInt(c));
		}
		
		return ints;
	}
	
	public static Knowledge unionKnowledge(Knowledge k1, Knowledge k2) {
		Set<Integer> tmp_p = new HashSet<Integer>();
		Set<Integer> tmp_i = new HashSet<Integer>();
		tmp_p.addAll(k1.getPreimage());
		tmp_p.addAll(k2.getPreimage());
		tmp_i.addAll(k1.getImage());
		tmp_i.addAll(k2.getImage());
		
		return new Knowledge(tmp_p, tmp_i);

	}
}
