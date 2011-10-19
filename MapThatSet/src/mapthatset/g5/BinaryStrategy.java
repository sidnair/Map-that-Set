package mapthatset.g5;

import java.util.ArrayList;
import mapthatset.sim.GuesserAction;

/**
 * This strategy solves binary mapping in 2/3n queries. 
 * For small n, special strategy (nash equilibrium) should be considered.
 */
public class BinaryStrategy extends Strategy {
	
	private int mappingLength;
	private ArrayList<Integer> result;
	private ArrayList<Integer> lastQuery;
	private int x=0,y=0; //binary mapping only. TODO:accept outlier 

	protected BinaryStrategy(boolean debug) {
		super(debug);
	}

	/* 
	 * For unqueried elements, set result[i]=0
	 * For queried but not sure elements, set result[i]=-1
	 * else, set result[i]=x or y
	 */
	@Override
	protected void startNewMapping(int mappingLength) {
		this.mappingLength = mappingLength;
		result = new ArrayList<Integer>();
		for(int i=0;i<this.mappingLength;i++)
			result.add(0);
	}
	
	protected void startNewMapping(int mappingLength,
			ArrayList<Integer> query, ArrayList<Integer> result) {
		// Ignore initial query - doesn't provide us with any extra info.
		startNewMapping(mappingLength);
	}

	
	@Override
	protected GuesserAction nextAction() {
		if(!result.contains(0)&&!result.contains(-1)){
			return new GuesserAction("g",result);
		}
		else{
			ArrayList<Integer> query = new ArrayList<Integer>();
			/* 
			 * when every element is queried, but we don't know the result,
			 * one extra query is needed
			 * e.g.result = [-1,-1,-1,-1]
			 * we query for [4], if returns [1]
			 * then result = [2,1,2,1]
			 */
			if(result.lastIndexOf(-1)>0 && result.lastIndexOf(0)==-1){
				query.add(result.lastIndexOf(-1));
			}
			/* 
			 * query the first unqueried with last queried-but-not-sure 
			 * e.g.result = [-1,-1,0,0]
			 * we query for [2,3]
			 */
			else if(result.lastIndexOf(-1)>0 && result.lastIndexOf(0)>0){
				query.add(result.lastIndexOf(-1));
				query.add(result.lastIndexOf(-1)+1);
				
			}
			/* 
			 * only one element unqueried
			 * e.g.result = [2,1,1,0]
			 * we query for [4]
			 */
			else if(result.lastIndexOf(-1)==-1 && result.indexOf(0)==result.lastIndexOf(0)){
				query.add(result.indexOf(0));
			}
			/* 
			 * more than one elements last unqueried
			 * e.g.result = [2,1,0,0]
			 * we query for [3,4]
			 */
			else {
				query.add(result.indexOf(0));
				query.add(result.indexOf(0)+1);
			}
			this.lastQuery = new ArrayList<Integer>(query);
			for(int i=0;i<query.size();i++){
				query.set(i, query.get(i)+1);
			}
			
			if(DEBUG){
				for(int i=0;i<this.result.size();i++){
					System.out.print(this.result.get(i));
					System.out.print(" ");
				}
				System.out.println();
			}

			return new GuesserAction("q", query);
		}
	}

	
	@Override
	protected void setResult(ArrayList<Integer> result) {
		if(lastQuery.size()==2 && result.size()==1){
			this.result.set(lastQuery.get(0), result.get(0));
			this.result.set(lastQuery.get(1),result.get(0));
			lookBack();
		}
		else if(lastQuery.size()==2 && result.size()==2){
			this.result.set(lastQuery.get(0), -1);
			this.result.set(lastQuery.get(1), -1);
			if(x==0 && y==0){
				x=result.get(0);
				y=result.get(1);
			}
		}
		else if(lastQuery.size()==1){
			this.result.set(lastQuery.get(0), result.get(0));
			lookBack();
		}

	}

	/* 
	 * when we know the result of some element, we look back to check if privious ones can be determined.
	 * for a series of -1 (queried-but-not-sure), the adjacent ones map to different values
	 */
	private void lookBack() {
		if(this.result.lastIndexOf(-1)!=-1){
			int m = result.get(result.lastIndexOf(-1)+1);
			int n = m==x?y:x;
			int lastIndex = result.indexOf(-1);
			for(int i = result.lastIndexOf(-1);i>=lastIndex;i=i-2){
				result.set(i, n);
			}
			for(int i = result.lastIndexOf(-1);i>=lastIndex;i=i-2){
				result.set(i, m);
			}
			
		}
		return;
	}

	@Override
	protected boolean supportsSubProblems() {
		return false;
	}

}