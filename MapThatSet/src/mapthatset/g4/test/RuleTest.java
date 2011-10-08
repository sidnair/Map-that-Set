package mapthatset.aiplayer.test;

import java.util.HashSet;
import java.util.Set;

import mapthatset.aiplayer.util.Inferrer;
import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.SetUtil;

public class RuleTest {
	public static void main(String[] args) {
		Set<Knowledge> kb = new HashSet<Knowledge>();
		
		kb.add(new Knowledge(SetUtil.parseIntSet("1,2,3,4,5"), SetUtil.parseIntSet("1,2,3,4")));
		kb.add(new Knowledge(SetUtil.parseIntSet("1,2,3"), SetUtil.parseIntSet("1,2")));
		kb.add(new Knowledge(SetUtil.parseIntSet("1,4,5"), SetUtil.parseIntSet("1,3,4")));
		
		System.out.println("*** original knowledge base ***");
		for (Knowledge k : kb) {
			System.out.println(k);
		}
		
		for (int i = 1; i <= 10; i++) {
		
			System.out.println("*** inference after " + i + " round(s) ***");
			
			Inferrer.infer(kb);
			
			for (Knowledge k : kb) {
				System.out.println(k);
			}
		
		}
		
	}
}
