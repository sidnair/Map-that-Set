package mapthatset.g5;

import java.util.ArrayList;
import java.util.Random;

import mapthatset.sim.GuesserAction;
import mapthatset.sim.Mapper;

public class G5Mapper extends Mapper {

	@Override
	public ArrayList<Integer> startNewMapping(int intMappingLength) {
		ArrayList<Integer> mapping = new ArrayList<Integer>();
		Random r = new Random();
		for (int i = 0; i < intMappingLength; i++) {
			mapping.add(r.nextInt(intMappingLength) + 1);
		}
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
