package mapthatset.g2;

import java.util.ArrayList;
import mapthatset.sim.*;
import mapthatset.g2.PossibleMappings;

public class AwesomeGuesser extends Guesser
{
	int n;
	int qNum = 0;
	ArrayList< Integer > alGuess = new ArrayList< Integer >();
	ArrayList< Integer > alQuery = new ArrayList< Integer >();
	String strID = "AwesomeGuesser";
	PossibleMappings pm;
	
	public void startNewMapping( int n )
	{
		this.n = n;
		pm = new PossibleMappings(n);
		qNum = 0;
		alGuess = new ArrayList< Integer >();
	}
	
	@Override
	public GuesserAction nextAction()
	{
		qNum ++;
		alQuery = new ArrayList<Integer>();
		
		// Edge cases
		if (pm.unknownMappings().isEmpty()) { // We have the answer
			alGuess = pm.getFinalMappingArrayList();
			return new GuesserAction( "g", alGuess);
		}
		
		if (pm.unknownMappings().size() == 2) { // Only 2 mappings are unknown
			alQuery.add(pm.unknownMappings().get(0));
			return new GuesserAction("q",alQuery);
		}
		
		if (pm.unknownMappings().size() == 1) { // Only 1 mapping  unknown XXX Fix this !!!
			alQuery.add(pm.unknownMappings().get(0));
			return new GuesserAction("q",alQuery);
		}
		
		if (qNum == 1) { // First round
			int start = 1;
			int end = (int) n/2;
			for (int i=start;i<=end;i++) {
				alQuery.add(i);
			}
		}
		else if (qNum == 2) { // Second round
			int start = (int) n/2;
			int end = n;
			for (int i= start + 1;i<=end;i++) {
				alQuery.add(i);
			}
		}
		else { // Further rounds
			alQuery = chooseDisjointRows();
		}
		System.out.println("");
		return new GuesserAction("q", alQuery);
	}
	
	private ArrayList<Integer> chooseDisjointRows() {
		int[] rows = new int[2];
		int minLen = n+1;
		ArrayList<Integer> al;
		ArrayList<Integer> unknowns = pm.unknownMappings();
		int unlen = unknowns.size();
		
		for (int i=0; i<unlen; i++) {
			for (int j=i+1; j<unlen; j++) {
				al = pm.intersection(unknowns.get(i), unknowns.get(j));
				if (al.size() < minLen) {
					rows[0] = unknowns.get(i);
					rows[1] = unknowns.get(j);
					minLen = al.size();
				}
			}			
		}
		ArrayList<Integer> alRows = new ArrayList<Integer>();
		alRows.add(rows[0]);
		alRows.add(rows[1]);
		return alRows;
	}

	@Override
	public void setResult( ArrayList<Integer> alResult ) {
		
		for (int i: alQuery) {
			for (int j=1; j<=n; j++) {
				if (alResult.indexOf(j) == -1) {
					pm.eliminate(i,j);
				}
			}				
		}
	}

	@Override
	public String getID() 
	{
		return strID;
	}
}
