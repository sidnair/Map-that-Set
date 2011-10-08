package mapthatset.aiplayer.appliedRules;

import java.util.HashSet;
import java.util.Set;

import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.SetUtil;

public class AppliedUnion extends AbstractAppliedRule {

	@Override
	public Set<Knowledge> apply() {
		Set<Knowledge> result = new HashSet<Knowledge>();
		result.addAll(ku);
		
		ku.get(0).getPairings(this.getClass().getName()).add(ku.get(1).getRecency());
		ku.get(1).getPairings(this.getClass().getName()).add(ku.get(0).getRecency());
		
		Knowledge union = SetUtil.unionKnowledge(ku.get(0), ku.get(1));
		
		result.add(union);
		return result;
	}
	
	@Override
	public double getPriorityPenalty() {
		// TODO Auto-generated method stub
		return -100;
	}

}
