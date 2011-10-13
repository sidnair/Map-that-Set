package mapthatset.g7;

import java.util.ArrayList;
import java.util.HashSet;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class CrossGuesser extends Guesser {

	private int variable_count;

	private int value_count;

	private HashSet <Integer> combined;

	private HashSet <Integer> overlap;

	private HashSet <Integer> active;

	private ArrayList <Integer> query;

	private Combinator engine;

	private boolean guess;

	private int round_phase;

	private int[] round_dividing;

	private HashSet <Integer> round_used;

	public void startNewMapping(int len)
	{
		variable_count = len;
		value_count = 0;
		combined = new HashSet <Integer> ();
		overlap = new HashSet <Integer> ();
		query = new ArrayList <Integer> ();
		engine = new Combinator(variable_count);
		active = new HashSet <Integer> ();
		for (int var_i = 1 ; var_i <= variable_count ; ++var_i)
			active.add(var_i);
		guess = false;
		round_phase = 0;
		round_dividing = new int[1];
		round_dividing[0] = variable_count;
		round_used = new HashSet <Integer> ();
	}

	public GuesserAction nextAction()
	{
		query.clear();
		/* If size is one make the trivial guess */
		if (variable_count == 1) {
			query.add(1);
			guess = true;
			return new GuesserAction("g", query);
		}
		/* If all of them are found already return with guess */
		if (active.size() == 0) {
			for (int var_i = 1 ; var_i <= variable_count ; ++var_i)
				query.add(engine.domain(var_i)[0]);
			guess = true;
			return new GuesserAction("g", query);
		}
		do {
			/* Check round of covering you are currently at */
			if (round_phase == round_dividing.length) {
				int set_size = min((int) Math.sqrt(variable_count), (int) Math.sqrt(value_count * 2));
				round_dividing = divide(variable_count, variable_count / set_size);
				round_phase = 0;
				round_used.clear();
			}
			int limit = round_dividing[round_phase++];
			/* Create a set of possible variables for query */
			do {
				int var = 0;
				next_var:
				for (int var_i : active) {
					if (round_used.contains(var_i))
						continue;
					for (int var_j : query) {
						int mix = var_i * variable_count + var_j;
						if (combined.contains(mix))
							continue next_var;
					}
					var = var_i;
					break;
				}
				if (var == 0) break;
				round_used.add(var);
				query.add(var);
			} while (query.size() != limit);
		} while (query.size() == 0);
		guess = false;
		return new GuesserAction("q", query);
	}

	public void setResult(ArrayList <Integer> answer_par)
	{
		if (guess) return;
		int[] answer = toArray(answer_par);
		boolean first_result = false;
		if (value_count <= 0) {
			first_result = true;
			value_count = answer.length;
		}
		if (query.size() == answer.length)
			active.remove(query.get(0));
		engine.constraint(toArray(query), answer);
//		int[][] real_domain = findAllCombinations(50);
		int[][] real_domain = null;
		for (int var_i = 1 ; var_i <= variable_count ; ++var_i)
			if (real_domain == null ? engine.domain(var_i).length == 1 : real_domain[var_i].length == 1)
				active.remove(var_i);
		if (first_result) return;
		for (int var_i : query)
			for (int var_j : query) {
				if (var_i >= var_j) continue;
				combined.add(var_i * variable_count + var_j);
				combined.add(var_j * variable_count + var_i);
			}
		overlap.clear();
		for (int var_i : active)
			for (int var_j : active) {
				if (var_i >= var_j) continue;
				int count = 0;
				int[] domain_i = (real_domain == null ? engine.domain(var_i) : real_domain[var_i]);
				int[] domain_j = (real_domain == null ? engine.domain(var_j) : real_domain[var_j]);
				HashSet <Integer> vals = new HashSet <Integer> ();
				for (int i = 0 ; i != domain_i.length ; ++i)
					vals.add(domain_i[i]);
				for (int j = 0 ; j != domain_j.length ; ++j)
					if (vals.contains(domain_j[j]))
						count++;
				if (count > 1) {
					overlap.add(var_i * variable_count + var_j);
					overlap.add(var_j * variable_count + var_i);
				}
			}
	}

	private static int[] divide(int num, int sets)
	{
		int sum = 0;
		int[] ret = new int [sets];
		double div = ((double) num) / sets;
		for (int i = 0 ; i != sets ; ++i) {
			ret[i] = closestInt(div * (i + 1)) - sum;
			sum += ret[i];
		}
		return ret;
	}

	public String getID() 
	{
		return "G7: Guesser";
	}

	private static int closestInt(double n)
	{
		int r = (int) n;
		if (n >= 0.5 + r) r++;
		return r;
	}

	private int[] toArray(ArrayList <Integer> al)
	{
		int[] arr = new int [al.size()];
		int i = 0;
		for (int n : al)
			arr[i++] = n;
		return arr;
	}

	private static int min(int a, int b)
	{
		return a < b ? a : b;
	}
}
