package mapthatset.g7;

import java.util.ArrayList;
import java.util.Random;

import mapthatset.sim.GuesserAction;
import mapthatset.sim.Mapper;

public class BinaryMapper extends Mapper {

	public String getID()
	{
		return "G7: Binary Mapper";
	}

	private Random gen = new Random();

	public ArrayList <Integer> startNewMapping(int len)
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		if (len == 1)
			list.add(1);
		else {
			int v1, v2 = gen.nextInt(len) + 1;
			do {
				v1 = gen.nextInt(len) + 1;
			} while (v1 == v2);
			for (int i = 0 ; i != len ; ++i)
				list.add(gen.nextInt(2) == 0 ? v1 : v2);
		}
		return list;
	}

	public void updateGuesserAction(GuesserAction g) {}
}

