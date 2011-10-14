package mapthatset.g7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Vector;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G7Guesser extends Guesser {

	public String getID()
	{
		return "G7: Guesser";
	}

	private int variable_count;
	private int value_count;
	private int round;
	private boolean guess;
	private ArrayList <Integer> query;
	private Combinator engine;
	private Random random;
	private Vector <Pair <ArrayList <Integer>, ArrayList <Integer>>> history;

	public void startNewMapping(int len)
	{
		variable_count = len;
		value_count = 0;
		round = 0;
		guess = false;
		binary = false;
		distinct = false;
		tree = false;
		cross = false;
		random = new Random();
		engine = new Combinator(len);
		history = new Vector <Pair <ArrayList <Integer>, ArrayList <Integer>>> ();
	}

	public GuesserAction nextAction()
	{
		round++;
		query = new ArrayList <Integer> ();
		for (int i = 1 ; i <= variable_count && query != null ; ++i) {
			int[] domain = engine.domain(i);
			if (domain.length == 1)
				query.add(domain[0]);
			else
				query = null;
		}
		if (query != null) {
			guess = true;
			return new GuesserAction("g", query);
		}
		if (value_count == 0) {
			query = firstQuery();
			System.out.println("first");
		} else if (binary) {
			query = binaryQuery();
			System.out.println("binary");
		} else if (distinct) {
			query = distinctQuery();
			System.out.println("distinct");
		} else if (tree) {
			System.out.println("tree");
			query = treeQuery();
		} else if (cross) {
			System.out.println("cross");
			query = crossQuery();
		}
		guess = false;
		return new GuesserAction("q", query);
	}

	public void setResult(ArrayList <Integer> result)
	{
		if (guess) return;
		history.add(new Pair <ArrayList <Integer>, ArrayList <Integer>> (query, result));
		engine.constraint(toArray(query), toArray(result));
		if (round == 1) {
			value_count = result.size();
			if (value_count == 2)
				binary = true;
			else if (value_count == variable_count)
				distinct = true;
		//	else
		//		tree = true;
			else
				cross = true;
			return;
		}
		if (binary)
			binaryResult(result);
		else if (distinct)
			distinctResult(result);
		else if (tree)
			treeResult(result);
		else if (cross)
			crossResult(result);
	}

	/* Ask for all variables */
	private ArrayList <Integer> firstQuery()
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		for (int i = 1 ; i <= variable_count ; ++i)
			list.add(i);
		return list;
	}

	/* Optimal strategy for binary mapper */
	private boolean binary;

	private HashSet <Integer> active_variables;

	private ArrayList <Integer> binaryQuery()
	{
		if (round == 2) {
			active_variables = new HashSet <Integer> ();
			for (int i = 1 ; i <= variable_count ; ++i)
				active_variables.add(i);
		}
		ArrayList <Integer> list = new ArrayList <Integer> ();
		for (int i = 0 ; i != 2 && active_variables.size() != 0 ; ++i) {	
			int x = active_variables.iterator().next();
			active_variables.remove(x);
			list.add(x);
		}
		return list;
	}

	private void binaryResult(ArrayList <Integer> result)
	{
		if (result.size() == 2)
			active_variables.add(query.get(0));
		if (active_variables.size() == 0)
			engine.findAll();
	}

	/* Optimal strategy for distinct mapper */
	private boolean distinct;

	private ArrayList <Integer> distinctQuery()
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		int limit = ceilLog(variable_count);
		int set_size = 1 << (limit - round + 1);
		int var = 1;
		do {
			for (int i = 0 ; i != set_size ; ++i) {
				if (var > variable_count)
					return list;
				list.add(var++);
			}
			var += set_size;
		} while (var <= variable_count);
		return list;
	}

	private void distinctResult(ArrayList <Integer> result)
	{
		ArrayList <Integer> rest_vars = new ArrayList <Integer> ();
		ArrayList <Integer> rest_vals = new ArrayList <Integer> ();
		for (int i = 1 ; i <= variable_count ; ++i) {
			if (!query.contains(i))
				rest_vars.add(i);
			if (!result.contains(i))
				rest_vals.add(i);
		}
		engine.constraint(toArray(rest_vars), toArray(rest_vals));
	}

	/* Tree strategy
	 * Optimal for distinct mappings
	 * Does not seem to work for other cases
	 */
	private boolean tree;

	private QueryNode root;

	private Vector <QueryNode> query_nodes;

	private ArrayList <Integer> treeQuery()
	{
		/* Set the root of the tree */
		if (round == 2) {
			root = QueryNode.root(history.get(0).fst);
			root.set(history.get(0).snd, engine);
		}
		/* Get all open query nodes */
		Vector <QueryNode> open = QueryNode.open(root);
		/* Keep best subset of query nodes */
		query_nodes = new Vector <QueryNode> ();
		int best_value = 0;
		/* If not many open nodes */
		if (open.size() <= 16) {
			/* Iterate through all subsets */
			SubsetIterable <QueryNode> open_subsets = new SubsetIterable <QueryNode> (open);
			next_subset:
			for (Vector <QueryNode> open_subset : open_subsets) {
				int value = 0;
				for (int i = 0 ; i != open_subset.size() ; ++i)
					value += open_subset.get(i).variables().size();
				if (value <= best_value)
					continue;
				/* Check the subset has independent parts */
				for (int i = 0 ; i != open_subset.size() ; ++i)
					for (int j = 0 ; j != i ; ++j)
						if (!independent(open_subset.get(i).values(), open_subset.get(j).values()))
							continue next_subset;
				/* Set as best so far */
				best_value = value;
				query_nodes = open_subset;
			}
		} else {
			int repeats = 1000;
			/* Start from a random query */
			for (int repeat = 0 ; repeat != repeats ; ++repeat) {
				shuffle(open);
				Vector <QueryNode> open_subset = new Vector <QueryNode> ();
				open_subset.add(open.get(0));
				next_query:
				for (int i = 1 ; i != open.size() ; ++i) {
					for (QueryNode q : open_subset)
						if (!independent(q.values(), open.get(i).values()))
							continue next_query;
					open_subset.add(open.get(i));
				}
				int value = 0;
				for (int i = 0 ; i != open_subset.size() ; ++i)
					value += open_subset.get(i).variables().size();
				if (value > best_value) {
					query_nodes = open_subset;
					best_value = value;
				}
			}
		}
		/* Get next queries by assembling best independent subset */
		boolean first = true;
		ArrayList <Integer> list = new ArrayList <Integer> ();
		for (QueryNode node : query_nodes) {
			if (!first)
				System.out.print(" + ");
			else
				first = false;
			System.out.print("[" + toString(node.variables()) + "](" + node.values().size() + ")");
			list.addAll(node.variables());
		}
		System.out.println("");
		return list;
	}

	private void treeResult(ArrayList <Integer> answer)
	{
		for (QueryNode q : query_nodes) {
			ArrayList <Integer> values = new ArrayList <Integer> ();
			for (int value : answer)
				if (q.values().contains(value))
					values.add(value);
			q.set(values, engine);
		}
		engine.findAllTimeout(500);
		try {
			engine.findAll();
		} catch (NoSuchElementException e) {}
	}

	private boolean cross;

	private int[] uses;

	private HashSet <Integer> combined;

	private class UseCompare implements Comparator <Integer> {

		public int compare(Integer var_i, Integer var_j)
		{
			return uses[var_i - 1] - uses[var_j - 1];
		}
	}

	private ArrayList <Integer> crossQuery()
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		if (round == 2) {
			uses = new int [variable_count];
			for (int i = 0 ; i != variable_count ; ++i)
				uses[i] = 0;
			combined = new HashSet <Integer> ();
		}
		int limit = (int) Math.ceil(Math.sqrt(variable_count));
		int active = 0;
		Integer[] variables = new Integer [variable_count];
		for (int var = 1 ; var <= variable_count ; ++var)
			if (engine.domain(var).length != 1)
				variables[active++] = var;
		Arrays.sort(variables, 0, active, new UseCompare());
		next_var:
		for (int i = 0 ; i != active && list.size() != limit ; ++i) {
			int var_i = variables[i];
			for (int var_j : list)
				if (combined.contains(var_i * variable_count + var_j))
					continue next_var;
			list.add(var_i);
			uses[var_i - 1]++;
		}
		return list;
	}

	private void crossResult(ArrayList <Integer> answer)
	{
		for (int var_i : query)
			for (int var_j : query) {
				if (var_i >= var_j) continue;
				combined.add(var_i * variable_count + var_j);
				combined.add(var_j * variable_count + var_i);
			}
		engine.findAllTimeout(500);
		try {
			engine.findAll();
		} catch (NoSuchElementException e) {}
	}

	private static boolean independent(Collection <?> c1, Collection <?> c2)
	{
		for (Object o : c1)
			if (c2.contains(o))
				return false;
		return true;
	}

	private static int ceilLog(int n)
	{
		int i = 0;
		while ((1 << i) < n)
			i++;
		return i;
	}

	private <E> void shuffle(Vector <E> arr)
	{
		int s = arr.size();
		for (int i = 0 ; i != s ; ++i) {
			int r = random.nextInt(s - i) + i;
			E t = arr.get(r);
			arr.set(r, arr.get(i));
			arr.set(i, t);
		}
	}

	private static int[] toArray(Collection <Integer> al)
	{
		int[] arr = new int [al.size()];
		int i = 0;
		for (int n : al)
			arr[i++] = n;
		return arr;
	}

	private static String toString(Collection <?> a)
	{
		if (a.size() == 0)
			return "";
		boolean first = true;
		StringBuffer buf = new StringBuffer();
		for (Object o : a) {
			if (!first)
				buf.append(',');
			else
				first = false;
			buf.append(o.toString());
		}
		return buf.toString();
	}
}
