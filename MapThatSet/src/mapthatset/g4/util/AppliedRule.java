package mapthatset.aiplayer.util;

import java.util.List;
import java.util.Set;

public interface AppliedRule extends Comparable<AppliedRule> {
	
	public void setKnowledgeUsed(List<Knowledge> ku);
	
	public List<Knowledge> getKnowledgeUsed();
	
	//Remove subset knowledgeUsed from knowledge base, apply this rule, then add resulting knowledge pieces
	//   If want to preserve previous knowledge pieces used in the rule, implementation
	//   should return a set that includes the original knowledge (i.e., in ku) as well.
	public Set<Knowledge> apply();
	
	public double getRecency();
	public double getSpecificity();
	
	public double getPriorityPenalty();
	
	public int compareTo(AppliedRule other);
}
