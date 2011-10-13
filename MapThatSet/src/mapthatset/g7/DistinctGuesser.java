package mapthatset.g7;

import java.util.ArrayList;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class DistinctGuesser extends Guesser {

	private int variable_count;

	private Combinator engine;

	private int round;

	private ArrayList <Integer> query;

	private boolean guess;

	public String getID()
	{
		return "G7: Distinct Guesser";
	}

	public GuesserAction nextAction()
	{
		query.clear();
		int limit = 1 + (int) Math.ceil(Math.log(variable_count));
		if (round > limit) {
			for (int var_i = 1 ; var_i <= variable_count ; ++var_i)
				query.add(engine.domain(var_i)[0]);
			guess = true;
			return new GuesserAction("g", query);
		}
		int set_size = 1 << (limit - round++);
		int var = 1;
		both:
		do {
			for (int i = 0 ; i != set_size ; ++i) {
				if (var > variable_count)
					break both;
				query.add(var++);
			}
			var += set_size;
		} while (var <= variable_count);
		guess = false;
		return new GuesserAction("q", query);
	}

	public void setResult(ArrayList<Integer> answer)
	{
		if (!guess)
			engine.constraint(toArray(query), toArray(answer));
	}

	public void startNewMapping(int intMappingLength)
	{
		variable_count = intMappingLength;
		engine = new Combinator(variable_count);
		query = new ArrayList <Integer> ();
		round = 0;
		guess = false;
	}

	private int[] toArray(ArrayList <Integer> al)
	{
		int[] arr = new int [al.size()];
		int i = 0;
		for (int n : al)
			arr[i++] = n;
		return arr;
	}
}
