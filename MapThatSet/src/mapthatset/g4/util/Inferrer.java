package mapthatset.aiplayer.util;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * This class infers new knowledge from knowledge base
 * @author yufeiliu
 *
 */
public class Inferrer {
	private static boolean converged = false;
	
	public static boolean hasConverged() { return converged; }
	
	public static void infer(Set<Knowledge> kb) {
		PriorityQueue<AppliedRule> applicables = new PriorityQueue<AppliedRule>();
		
		for (Rule rule : RuleUtil.getRules()) {
			applicables.addAll(rule.findApplications(kb));
		}
		
		if (applicables.size() == 0) {
			converged = true;
			System.out.println("Cannot select a rule, converged!");
			return;
		}
		
		while (true) {
			
			if (applicables.size() == 0) {
				converged = true;
				System.out.println("Can't find a rule that produces no duplicates, converged!");
				return;
			}
			
			AppliedRule best = applicables.remove();
			
			System.out.println("Applying rule - " + best);
			
			converged = false;
			
			int beforeSize = kb.size();
			
			kb.removeAll(best.getKnowledgeUsed());
			
			kb.addAll(best.apply());
			
			//Remove duplicate loop
			while (true){
				int i = 0;
				Set<Integer> js = new HashSet<Integer>();
				Knowledge toRemove = null;
				outer:
				for (Knowledge k1 : kb) {
					i++;
					
					if (js.contains(i)) continue;
					
					int j = 0;
					for (Knowledge k2 : kb) {
						j++;
						if (i==j) continue;
						if (k1.equals(k2)) {
							toRemove = k2;
							break outer;
						}
					}
				}
				
				if (toRemove == null) {
					break;
				} 
				
				kb.remove(toRemove);
			}
			
			if (kb.size() != beforeSize) break;
			
		}
	}
}
