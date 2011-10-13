package mapthatset.g7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class TreeGuesser extends Guesser {

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
		if (value_count == 0)
			query = firstQuery();
		else 
			query = treeQuery();
		guess = false;
		return new GuesserAction("q", query);
	}

	public void setResult(ArrayList <Integer> result)
	{
		if (guess) return;
		history.add(new Pair <ArrayList <Integer>, ArrayList <Integer>> (query, result));
		engine.constraint(toArray(query), toArray(result));
		if (round == 1)
			value_count = result.size();
		else
			treeResult(result);
	}

	private ArrayList <Integer> firstQuery()
	{
		ArrayList <Integer> list = new ArrayList <Integer> ();
		for (int i = 1 ; i <= variable_count ; ++i)
			list.add(i);
		return list;
	}

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
		engine.findAll();
	}

	private static boolean independent(Collection <?> c1, Collection <?> c2)
	{
		for (Object o : c1)
			if (c2.contains(o))
				return false;
		return true;
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
