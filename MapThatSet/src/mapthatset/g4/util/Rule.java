package mapthatset.aiplayer.util;

import java.util.Set;

public interface Rule {
	public Set<AppliedRule> findApplications(Set<Knowledge> kb);
}
