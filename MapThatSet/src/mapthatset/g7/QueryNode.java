package mapthatset.g7;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

class QueryNode {

	private static Random random = new Random();

	private QueryNode up;
	private QueryNode left;
	private QueryNode right;

	private HashSet <Integer> variables;
	private HashSet <Integer> values;

	private int traversal_state;

	private QueryNode(QueryNode father, Collection <Integer> vars)
	{
		up = father;
		left = null;
		right = null;
		variables = new HashSet <Integer> (vars);
		values = null;
	}

	public static QueryNode root(Collection <Integer> vars)
	{
		return new QueryNode(null, vars);
	}

	public void set(Collection <Integer> vals, Combinator engine) {

		/* Set values */
		if (values != null)
			return;
		values = new HashSet <Integer> (vals);
		if (engine != null)
			engine.constraint(toArray(variables), toArray(values));

		/* Set sibling */
		if (up != null) {
			QueryNode sib = up.left == this ? up.right : up.left;
			if (sib.values == null) {
				HashSet <Integer> unused_values = new HashSet <Integer> (up.values);
				for (int val : values)
					unused_values.remove(val);
				if (variables.size() == unused_values.size())
					sib.set(unused_values, engine);
			}
		}

		/* Create children */
		if (values.size() > 1) {
			Vector <Integer> vars = new Vector <Integer> ();
			for (int var : variables)
				if (engine == null || engine.domain(var).length != 1)
					vars.add(var);
			shuffle(vars);
			int half = vars.size() >> 1;
			HashSet <Integer> left_vars = new HashSet <Integer> ();
			HashSet <Integer> right_vars = new HashSet <Integer> ();
			for (int i = 0 ; i != half ; ++i)
				left_vars.add(vars.get(i));
			for (int i = half ; i != vars.size() ; ++i)
				right_vars.add(vars.get(i));
			left = new QueryNode(this, left_vars);
			right = new QueryNode(this, right_vars);
		}
	}

	public HashSet <Integer> variables()
	{
		return new HashSet <Integer> (variables);
	}

	public HashSet <Integer> values()
	{
		return new HashSet <Integer> (values == null ? up.values : values);
	}

	public QueryNode up()
	{
		return up;
	}

	public QueryNode left()
	{
		return left;
	}

	public QueryNode right()
	{
		return right;
	}

	public static Vector <QueryNode> open(QueryNode root)
	{
		Vector <QueryNode> res = new Vector <QueryNode> ();
		root.traversal_state = 0;
		QueryNode p = root;
		do {
			/* Leaf */
			if (p.left == null) {
				if (p.values == null)
					res.add(p);
				p.traversal_state = 2;
			}
			/* Checked both children */
			if (p.traversal_state == 2)
				p = p.up;
			/* Checked one or no children */
			else {
				p = (p.traversal_state++ == 0 ? p.left : p.right);
				p.traversal_state = 0;
			}
		} while (p != null);
		return res;
	}

	private void shuffle(Vector <Integer> arr)
	{
		int s = arr.size();
		for (int i = 0 ; i != s ; ++i) {
			int r = random.nextInt(s - i) + i;
			int t = arr.get(r);
			arr.set(r, arr.get(i));
			arr.set(i, t);
		}
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

	public static void main(String[] args)
	{
		int size = 16;
		int depth = 4;
		Vector <Integer> v = new Vector <Integer> ();
		for (int i = 1 ; i <= size ; ++i)
			v.add(i);
		QueryNode r = root(v);
		System.out.println("Root: [" + toString(r.variables) + "]");
		Vector <QueryNode> open;
		for (int i = 0 ; i != depth ; ++i) {
			open = open(r);
			for (QueryNode q : open)
				q.set(q.variables, null);
		}
		open = open(r);
		for (QueryNode q : open)
			System.out.println("[" + toString(q.variables) + "]");
	}

	private static int[] toArray(Collection <Integer> al)
	{
		int[] arr = new int [al.size()];
		int i = 0;
		for (int n : al)
			arr[i++] = n;
		return arr;
	}
}
