package mapthatset.aiplayer.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mapthatset.aiplayer.appliedRules.AppliedDifference;
import mapthatset.aiplayer.util.AppliedRule;
import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.Rule;

public class Difference implements Rule {

	@Override
	public Set<AppliedRule> findApplications(Set<Knowledge> kb) {
		Set<AppliedRule> rules = new HashSet<AppliedRule>();
		
		int i = 0;
		
		for (Knowledge k1 : kb) {
			
			i++;
			
			int j = 0;
			
			for (Knowledge k2 : kb) {
				
				j++;
				
				if (i==j) continue;
				
				
				if (k1.getD() != k2.getD()) continue;
				
				if (!(k1.getPreimage().size() < k2.getPreimage().size())) {
					continue;
				}
				if (!(k2.getPreimage().containsAll(k1.getPreimage()) && k2.getImage().containsAll(k1.getImage()))) {
					continue;
				}
				
				if (k1.getPairings(AppliedDifference.class.getName()).contains(k2.getRecency())) {
					continue;
				}
				
				AppliedRule rule = new AppliedDifference();
				List<Knowledge> ku = new ArrayList<Knowledge>();
				
				ku.add(k1); 
				ku.add(k2);
				
				rule.setKnowledgeUsed(ku);
				
				rules.add(rule);
			}
		}
		
		return rules;
	}

}
