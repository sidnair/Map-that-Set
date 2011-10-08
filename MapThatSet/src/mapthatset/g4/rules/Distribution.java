package mapthatset.aiplayer.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mapthatset.aiplayer.appliedRules.AppliedDistribution;
import mapthatset.aiplayer.util.AppliedRule;
import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.Rule;

public class Distribution implements Rule {

	@Override
	public Set<AppliedRule> findApplications(Set<Knowledge> kb) {
		Set<AppliedRule> rules = new HashSet<AppliedRule>();
		
		for (Knowledge k : kb) {
			if (k.getPreimage().size()>1 && k.getImage().size()==1) {
				AppliedRule r = new AppliedDistribution();
				List<Knowledge> ku = new ArrayList<Knowledge>();
				ku.add(k);
				r.setKnowledgeUsed(ku);
				rules.add(r);
			}
		}
		
		return rules;
	}

}
