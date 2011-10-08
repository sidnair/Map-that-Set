package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

/**
 *  TODO - give an overview of this strategy. What are the pros and cons, and
 *  what needs to be implemented?
 *  
 *  Also, change the name..."G5StrategyNo2" is not informative.
 */
public class G5StrategyNo2 extends Strategy {

	int intMappingLength;
	int intLastQueryIndex = 0;
	ArrayList< Integer > alGuess = new ArrayList< Integer >();
	int[][] matrix;
	ArrayList<Integer> pair;
	int guessNumber;
	int state = -1;
	
	public G5StrategyNo2(boolean debug) {
		super(debug);
	}
	
	public void startNewMapping( int intMappingLength )
	{
		if(intMappingLength<=0){
			System.out.println("kidding me?");
			return;
		}
		this.intMappingLength = intMappingLength;
		alGuess = new ArrayList< Integer >();
		matrix = new int[intMappingLength][2];
		for(int i = 0;i<intMappingLength;i++){
			alGuess.add(-1);
			for(int j=0;j<2;j++){
				matrix[i][j]=-1;
			}
		}
		pair = new ArrayList<Integer>();
		guessNumber=0;
	}
	
	@Override
	public GuesserAction nextAction()
	{
		GuesserAction gscReturn = null;
		if ( done() )
		{
			gscReturn = new GuesserAction( "g", alGuess );
		}
		else
		{
			if(state>=0){
				ArrayList< Integer > alQueryContent = new ArrayList< Integer >();
				alQueryContent.add(state);
				gscReturn = new GuesserAction( "q", alQueryContent );
			}
			
			if(pair.size()==2){
				gscReturn = pairGuess();				
			}
			else{
				ArrayList< Integer > alQueryContent = new ArrayList< Integer >();
				alQueryContent.add(guessNumber++);
				alQueryContent.add(guessNumber++);
				gscReturn = new GuesserAction( "q", alQueryContent );				
			}

		}
		return gscReturn;
	}
	


	private GuesserAction pairGuess() {
		int p1=pair.get(0),p2=p1+1,q1=pair.get(1),q2=q1+1;
		pair.clear();
		HashSet<Integer> possible = new HashSet<Integer>();
		possible.add(matrix[p1][0]);
		possible.add(matrix[p1][1]);
		possible.add(matrix[q1][0]);
		possible.add(matrix[q1][1]);
		if(possible.size()==4 || possible.size()==3){
			ArrayList< Integer > alQueryContent = new ArrayList< Integer >();
			alQueryContent.add(p1);
			alQueryContent.add(q1);
			return new GuesserAction( "q", alQueryContent );	
		}
		else{
			ArrayList< Integer > alQueryContent = new ArrayList< Integer >();
			alQueryContent.add(p1);
			state = q1;
			return new GuesserAction( "q", alQueryContent );
		}
		
	}

	private boolean done() {
		for(Integer i:alGuess){
			if(i==-1)
				return false;
		}
		return true;
	}

	@Override
	public void setResult( ArrayList< Integer > alResult )
	{
		if(state>=0){
			alResult.add(state, alResult.get(0));
			int result =matrix[state][0];
			if(matrix[state][0]==alResult.get(0)){
				result =matrix[state][1];
			}
			alResult.add(state+1, result);
			state=-1;
		}
		
		else if(pair.size()==2){
			int p1=pair.get(0),p2=p1+1,q1=pair.get(1),q2=q1+1;
			
		}
		
		if(alResult.size()==1){
			alGuess.set(guessNumber-1, alResult.get(0));
			alGuess.set(guessNumber-2, alResult.get(0));
		}
		else{
			pair.add(guessNumber-2);
			matrix[guessNumber-2][0]=alResult.get(0);
			matrix[guessNumber-2][1]=alResult.get(1);
			matrix[guessNumber-1][0]=alResult.get(0);
			matrix[guessNumber-1][1]=alResult.get(1);
		}
	}

}