package mapthatset.g5;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.NEW;

import mapthatset.sim.GuesserAction;
import mapthatset.sim.Mapper;
import mapthatset.g5.RandomLayer;

public class G5Mapper extends Mapper {
	
	private int intival = 3;
	//checked with the pair and orthogonal guesser, when n=3, there will be no orthogonal pair.

	@Override
	public ArrayList<Integer> startNewMapping(int intMappingLength) {
		ArrayList<Integer> mapping = new ArrayList<Integer>(intMappingLength);
		Random r = new Random();
		RandomLayer rl = new RandomLayer(intMappingLength);
		
		if(intMappingLength==2){
			mapping.add(r.nextInt(2)+1);
			if(r.nextInt(20)!=1){
				//the Nash Equilibrium is 5% mapping to the same and 95% mapping to different
				//if we always map to different element, a guesser will ask for one and infer the other.
				//the penalty of guessing wrong is 10*n, we can compute the Nash Equilibrium
				//for n>2, computing Nash Equilibrium is hard....
				mapping.add(mapping.get(0)==1?2:1);
			}
			else{
				mapping.add(mapping.get(0));
			}
		}
		
		else{
			int ran = r.nextInt(intival);
			for (int i = 0; i < intMappingLength; i++) {
				mapping.add((i+ran)%intival+1);
			}	
		}
		
		rl.randomize(mapping);
			
		return mapping;
	}

	@Override
	public void updateGuesserAction(GuesserAction gsaGA) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getID() {
		return "G5Mapper";
	}

}
