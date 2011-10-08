package mapthatset.aiplayer.util;

import java.util.ArrayList;
import java.util.List;

import mapthatset.aiplayer.rules.Difference;
import mapthatset.aiplayer.rules.Distribution;
import mapthatset.aiplayer.rules.Intersection;
import mapthatset.aiplayer.rules.IntersectionWithUnequalD;
import mapthatset.aiplayer.rules.Union;

public class RuleUtil {
	private static List<Rule> rules = null;
	
	public static List<Rule> getRules() {
		if (rules != null) return rules;
		
		rules = new ArrayList<Rule>();
		
		//TODO order in which rules are added can be tuned for better performance
		rules.add(new Distribution());
		rules.add(new Difference());
		rules.add(new Intersection());
		rules.add(new IntersectionWithUnequalD());
		rules.add(new Union());
		
		return rules;
	}
}
