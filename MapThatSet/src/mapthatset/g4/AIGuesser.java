package mapthatset.aiplayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mapthatset.aiplayer.util.Inferrer;
import mapthatset.aiplayer.util.Knowledge;
import mapthatset.aiplayer.util.TreeTest;
import mapthatset.sim.*;

public class AIGuesser extends Guesser
{
	int intMappingLength;
	ArrayList< Integer > alGuess = new ArrayList< Integer >();
	String strID = "AIGuesser";
	
	ArrayList< Integer > alQueryContent;
	
	private Set<Knowledge> kb;
	
	TreeTest guesserList = new  TreeTest();
	
	public void startNewMapping( int intMappingLength ) {
		kb = new HashSet<Knowledge>();
		this.intMappingLength = intMappingLength;
		alGuess = new ArrayList< Integer >();
		guesserList.initialize(intMappingLength);
	}
	
	@Override
	public GuesserAction nextAction() {
		
		
		GuesserAction gscReturn = null;
		alQueryContent = guesserList.pop();
		
		boolean done = true;
		ArrayList<Integer> answers = new ArrayList<Integer>();
		
		for (int i = 1; i <= intMappingLength; i++) {
			
			boolean found = false;
			
			for (Knowledge k : kb) {
				if (k.getPreimage().size() == 1 && k.getPreimage().contains(i) && k.getImage().size()==1) {
					found = true;
					answers.add(k.getImage().iterator().next());
					break;
				}
			}
			
			if (!found) {
				done = false;
				break;
			}
		}
		
		if(alQueryContent != null && !done){
			gscReturn = new GuesserAction( "q", alQueryContent );
		}
		else{
			gscReturn = new GuesserAction( "g", answers );	
		}
		
		//System.out.println(guessList);
		
		return gscReturn;
	}
	
	@Override
	public void setResult( ArrayList< Integer > alResult ) {
		
		if (alQueryContent==null) return;
		if (alResult == null) return;
		
		alGuess.add( alResult.get( 0 ) );
		
		Set<Integer> pi = new HashSet<Integer>();
		Set<Integer> i = new HashSet<Integer>();
		
		pi.addAll(alQueryContent);
		i.addAll(alResult);
		
		Knowledge gained = new Knowledge(pi, i);
		kb.add(gained);
		
		this.infer();
	}

	/**
	 * stub
	 */
	private void infer() {
		do {
			Inferrer.infer(kb);
		} while (!Inferrer.hasConverged());
	}
	
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return strID;
	}
}
