package mapthatset.g7;

import java.util.ArrayList;
import java.util.Random;

import mapthatset.sim.GuesserAction;
import mapthatset.sim.Mapper;

public class RandomMapper extends Mapper {

	public String getID()
	{
		return "G7: Random Mapper";
	}

	private static Random random = new Random();

	public ArrayList<Integer> startNewMapping(int len)
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		for (int i = 0 ; i != len ; ++i)
			list.add(random.nextInt(len) + 1);
		return list;
	}

	public void updateGuesserAction(GuesserAction gsaGA) {}

}
