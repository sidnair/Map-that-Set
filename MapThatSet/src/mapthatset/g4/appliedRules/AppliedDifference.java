package mapthatset.aiplayer.appliedRules;

import java.util.HashSet;
import java.util.Set;

import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.SetUtil;

public class AppliedDifference extends AbstractAppliedRule {
	
	@Override
	public Set<Knowledge> apply() {
		Set<Knowledge> result = new HashSet<Knowledge>();
		result.addAll(ku);
		
		ku.get(0).getPairings(this.getClass().getName()).add(ku.get(1).getRecency());
		ku.get(1).getPairings(this.getClass().getName()).add(ku.get(0).getRecency());
		
		SetUtil<Integer> util = new SetUtil<Integer>();
		
		Knowledge diff = new Knowledge(util.difference(ku.get(1).getPreimage(), ku.get(0).getPreimage()), 
				util.difference(ku.get(1).getImage(), ku.get(0).getImage()));
		
		ku.get(1).getPairings(this.getClass().getName()).add(diff.getRecency());
		diff.getPairings(this.getClass().getName()).add(ku.get(1).getRecency());
		
		result.add(diff);
		return result;
	}
	
	@Override
	public double getPriorityPenalty() {
		// TODO Auto-generated method stub
		return 20;
	}

}
