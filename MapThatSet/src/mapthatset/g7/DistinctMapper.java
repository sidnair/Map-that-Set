package mapthatset.g7;

import java.util.ArrayList;
import java.util.Random;

import mapthatset.sim.GuesserAction;
import mapthatset.sim.Mapper;

public class DistinctMapper extends Mapper {

	public String getID()
	{
		return "G7: Distinct Mapper";
	}

	private Random gen = new Random();

	public ArrayList<Integer> startNewMapping(int len)
	{
		int[] vals = new int [len];
		for (int i = 0 ; i != len ; ++i)
			vals[i] = i + 1;
		for (int i = 0 ; i != len ; ++i) {
			int ran = gen.nextInt(len - i) + i;
			swap(vals, i, ran);
		}
		ArrayList <Integer> ret = new ArrayList <Integer> ();
		for (int i = 0 ; i != len ; ++i)
			ret.add(vals[i]);
		return ret;
	}

	private static void swap(int[] a, int i, int j)
	{
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	public void updateGuesserAction(GuesserAction gsaGA) {}
}
