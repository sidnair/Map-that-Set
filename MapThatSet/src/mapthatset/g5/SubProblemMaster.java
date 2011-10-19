package mapthatset.g5;

import java.util.ArrayList;
import java.util.Set;

public interface SubProblemMaster {
	
	void setResult(ArrayList<Integer> result, ArrayList<Integer> query);

	public void restrictDomain(Set<Integer> domain);

	boolean isSolved(SubProblem sp);
	
}
