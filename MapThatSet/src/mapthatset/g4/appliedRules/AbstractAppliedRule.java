package mapthatset.aiplayer.appliedRules;

import java.util.List;
import mapthatset.aiplayer.util.AppliedRule;
import mapthatset.aiplayer.util.Knowledge;

public abstract class AbstractAppliedRule implements AppliedRule {
	protected List<Knowledge> ku;
	
	@Override
	public void setKnowledgeUsed(List<Knowledge> ku) {
		this.ku = ku;
	}

	@Override
	public List<Knowledge> getKnowledgeUsed() {
		return ku;
	}
	
	@Override
	public double getPriorityPenalty() {
		return 0;
	}
	
	@Override
	public double getRecency() {
		double total = 0;
		
		for (Knowledge k : ku) {
			total += k.getRecency();
		}
		
		return total / ku.size();
	}

	@Override
	public double getSpecificity() {
		
		double total = 0;
		for (Knowledge k : ku) {
			total += k.getPreimage().size() + k.getImage().size();
		}
		
		total += this.getPriorityPenalty();
		return total;
	}

	@Override
	public int compareTo(AppliedRule other) {
		if (other.getSpecificity() == this.getSpecificity()) {
			return Double.compare(other.getRecency(), this.getRecency());
		} else {
			return Double.compare(other.getSpecificity(), this.getSpecificity());
		}
	}
	
	public String toString() {
		
		String s = "";
		
		//String[] tmp = this.getClass().getName().split(".");
		s += this.getClass().getName() + ": ";
		
		for (Knowledge k : ku) {
			s += k + ", ";
		}
		
		return s;
	}
}
