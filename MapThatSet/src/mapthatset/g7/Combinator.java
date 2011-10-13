package mapthatset.g7;

import java.util.Random;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* Combinator class is a tool that stores a
 * number of variables and constraints for
 * them and can be iterated to give all possible
 * solutions that satisfy the constraints given
 */
class Combinator implements Iterable <int[]> {

	/* Number of variables */
	private int variable_count;

	/* Minimum value */
	private int min_value;

	/* Maximum value */
	private int max_value;

	/* Domains per variable */
	private int[][] gen_domain;

	/* Size of active domain per variable */
	private int[] gen_domain_size;

	/* Is last changes propagated ? */
	private HashSet <Integer> propagation_variables;

	private class Constraint {

		/* Different variables */
		public HashSet <Integer> variables;

		/* Different values on constraint */
		public HashSet <Integer> values;

		/* Initialize constraint between variables */
		public Constraint(int[] vars, int[] vals)
		{
			/* Copy and check variables */
			variables = new HashSet <Integer> ();
			for (int i = 0 ; i != vars.length ; ++i) {
				if (vars[i] <= 0 || vars[i] > variable_count)
					throw new IllegalArgumentException();
				variables.add(vars[i] - 1);
			}

			/* Copy and check values */
			values = new HashSet <Integer> ();
			for (int i = 0 ; i != vals.length ; ++i) {
				if (vals[i] < min_value || vals[i] > max_value) {
					System.out.println(vals[i]);
					throw new IllegalArgumentException();
				}
				values.add(vals[i]);
			}

			/* Values cannot be less than variables */
			if (variables.size() < values.size())
				throw new IllegalArgumentException();
		}
	}

	/* Typedef array of integers to allow array creation */
	private static class IntArray extends Vector <Integer> {
		private static final long serialVersionUID = 1;
	}

	/* Constraints for the problem */
	private Vector <Constraint> constraints;

	/* Constraints for each variable */
	private IntArray[] attach;

	/* Timeout for run iterator */
	private long timeout;

	/* Constructor with default minimum and maximum value */
	public Combinator(int vars)
	{
		this(vars, 1, vars);
	}

	/* Constructor */
	public Combinator(int vars, int min, int max)
	{
		/* Check and copy basic parameters */
		if (vars <= 0 || min > max)
			throw new IllegalArgumentException();
		variable_count = vars;
		min_value = min;
		max_value = max;

		/* Create array for constraints and attachments */
		constraints = new Vector <Constraint> ();
		attach = new IntArray [vars];
		for (int i = 0 ; i != vars ; ++i)
			attach[i] = new IntArray();

		/* Create domains and their sizes */
		int value_count = max_value - min_value + 1;
		gen_domain = new int [variable_count][value_count];
		gen_domain_size = new int [variable_count];
		for (int i = 0 ; i != variable_count ; ++i) {
			gen_domain_size[i] = value_count;
			for (int j = 0 ; j != value_count ; ++j)
				gen_domain[i][j] = j + min_value;
		}
		timeout = 0;
		propagation_variables = new HashSet <Integer> ();
	}

	/* Add a new constraint in the combinator */
	public void constraint(int[] vars, int[] vals)
	{
		/* Create the new constraint and add it in the list */
		Constraint new_constraint = new Constraint(vars, vals);
		int constraint_position = constraints.size();
		constraints.add(new_constraint);

		/* Attach the constraint to affected variables */
		for (int var_i : new_constraint.variables) {
			attach[var_i].add(constraint_position);

			/* Cut the domains of variable to match the new constraint */
			for (int val_i = 0 ; val_i != gen_domain_size[var_i] ; ++val_i)
				if (!new_constraint.values.contains(gen_domain[var_i][val_i])) {
					gen_domain[var_i][val_i--] = gen_domain[var_i][--gen_domain_size[var_i]];
					propagation_variables.add(var_i);
				}
		}
	}

	/* Find out cases where there the same number
	 * of free variables and unused values
	 * So each one of the free variables can
	 * only pick from those values
	 */
	private void propagate()
	{
		/* Check propagation between variables */
		while (propagation_variables.size() != 0) {
			int var = propagation_variables.iterator().next();
			propagation_variables.remove(var);
			for (int con_pos : attach[var]) {
				Constraint con = constraints.get(con_pos);

				/* Get unassigned variables and values */
				HashSet <Integer> unassigned_variables = new HashSet <Integer> ();
				HashSet <Integer> used_values = new HashSet <Integer> ();
				for (int var_i : con.variables)
					if (gen_domain_size[var_i] != 1)
						unassigned_variables.add(var_i);
					else
						used_values.add(gen_domain[var_i][0]);
				HashSet <Integer> unused_values = new HashSet <Integer> ();
				for (int value : con.values)
					if (!used_values.contains(value))
						unused_values.add(value);

				/* Cut domains if number of free variables is same
				 * as number of unused values in the constraint
				 */
				if (unassigned_variables.size() != unused_values.size())
					continue;
				for (int var_i : unassigned_variables)
					for (int val_i = 0 ; val_i != gen_domain_size[var_i] ; ++val_i) {
						if (!unused_values.contains(gen_domain[var_i][val_i])) {
							gen_domain[var_i][val_i--] = gen_domain[var_i][--gen_domain_size[var_i]];
							propagation_variables.add(var_i);
						}
						if (gen_domain_size[var_i] == 0)
							throw new RuntimeException();
					}
			}
		}
	}

	/* Size of one variable's domain
	 * WITHOUT running the search
	 */
	public int[] domain(int var_i)
	{
		propagate();
		if (var_i <= 0 || var_i-- > variable_count)
			throw new IllegalArgumentException();
		return copy(gen_domain[var_i], gen_domain_size[var_i]);
	}

	/* Returns unique solution else null
	 * Throws exception if unsolvable
	 */
	public int[] unique()
	{
		Iterator <int[]> it = iterator();
		if (!it.hasNext())
			throw new NoSuchElementException();
		int[] solution = it.next();
		if (it.hasNext())
			return null;
		return solution;
	}

	/* Iterator over valid combinations */
	public Iterator <int[]> iterator()
	{
		/* Anonymous class for iterator */
		return new Iterator <int[]> () {

	/* Indicates if we have finished the search for combinations
	 * Also used as a trick to call a "constructor" function
	 */
	boolean finished = init();

	/* "Constructor" for the anonymous iterator */
	private boolean init()
	{
		/* Initialize variables and order */
		variables_fixed = 0;
		variable = new int [variable_count];
		order = new int [variable_count];
		for (int i = 0 ; i != variable_count ; ++i) {
			variable[i] = min_value - 1;
			order[i] = i;
		}

		/* Initialize domains and copy them */
		propagate();
		int value_count = max_value - min_value + 1;
		constraint_count = constraints.size();
		domain = new int [variable_count][value_count];
		domain_size = new int [variable_count];
		domain_offset = new int [variable_count];
		for (int i = 0 ; i != variable_count ; ++i) {
			domain_size[i] = gen_domain_size[i];
			for (int j = 0 ; j != domain_size[i] ; ++j)
				domain[i][j] = gen_domain[i][j];
		}

		/* Cut variable stack and counters */
		cut_values = new int [variable_count];
		cut_stack = new int [variable_count * value_count];
		cut_stack_top = 0;

		/* Initialize values */
		unused_value = new int [constraint_count];
		unknown_variable = new int [constraint_count];
		for (int i = 0 ; i != unknown_variable.length ; ++i) {
			unused_value[i] = constraints.get(i).values.size();
			unknown_variable[i] = constraints.get(i).variables.size();
		}

		/* Initialize used values */
		value_uses = new int [constraint_count][value_count];
		for (int i = 0 ; i != constraint_count ; ++i)
			for (int j = 0 ; j != value_count ; ++j)
				value_uses[i][j] = 0;

		/* Haven't found first yet and not finished */
		return next_found = false;
	}

	/* The backtracking algorithm
	 * Stops when it finds next solution
	 * Will resume to find next one if called again
	 */
	private boolean go()
	{
		boolean go_on = (variables_fixed == 0);
		int var_i;
		next_variable:
		do {
			if (!go_on) {

				/* Clear effects of last value of top variable */
				var_i = order[variables_fixed - 1];
				int value = variable[var_i];
				variable[var_i] = min_value - 1;

				/* Clear modifications on constraints */
				for (int con_pos_i = 0 ; con_pos_i != attach[var_i].size() ; ++con_pos_i) {
					int con_pos = attach[var_i].get(con_pos_i);
					if (con_pos >= constraint_count)
						break;
					if (--value_uses[con_pos][value - min_value] == 0)
						unused_value[con_pos]++;
					unknown_variable[con_pos]++;
				}

				/* Restore cut values */
				for (int cut = 0 ; cut != cut_values[var_i] ; ++cut) {
					int cut_var_i = cut_stack[--cut_stack_top];
					domain_size[cut_var_i]++;
				}
				cut_values[var_i] = 0;
			} else {

				/* Next solution found */
				if (variables_fixed == variable_count)
					return true;

				/* Add new variable in the stack
				 * Pick the one with the smallest
				 * domain (MRV in AI literature)
				 */
				int min_order_i = variables_fixed;
				for (int order_i = variables_fixed + 1 ;
				     order_i != variable_count ; ++order_i)
					if (domain_size[order[order_i]] < domain_size[order[min_order_i]])
						min_order_i = order_i;

				/* Fix the order array and initialize stuff */
				swap(order, min_order_i, variables_fixed);
				var_i = order[variables_fixed++];
				domain_offset[var_i] = -1;
				cut_values[var_i] = 0;
			}

			/* Try next value in domain for variable */
			next_value:
			while (++domain_offset[var_i] != domain_size[var_i]) {

				/* Set new value to variable */
				int value = domain[var_i][domain_offset[var_i]];
				variable[var_i] = value;

				/* Check all constraints */
				for (int con_pos_i = 0 ; con_pos_i != attach[var_i].size() ; ++con_pos_i) {
					int con_pos = attach[var_i].get(con_pos_i);
					if (con_pos >= constraint_count)
						break;

					/* If free variables now less than
					 * unused values try next value
					 */
					if (value_uses[con_pos][value - min_value] != 0 &&
					    unused_value[con_pos] == unknown_variable[con_pos]) {

						/* Undo partial changes to constraints */
						while (con_pos_i-- != 0) {
							con_pos = attach[var_i].get(con_pos_i);
							if (con_pos >= constraint_count)
								break;
							if (--value_uses[con_pos][value - min_value] == 0)
								unused_value[con_pos]++;
							unknown_variable[con_pos]++;
						}

						/* Undo partial changes to domains */
						for (int cut = 0 ; cut != cut_values[var_i] ; ++cut) {
							int cut_var_i = cut_stack[--cut_stack_top];
							domain_size[cut_var_i]++;
						}
						cut_values[var_i] = 0;

						/* Try next value */
						continue next_value;
					}

					/* Update constraints */
					if (value_uses[con_pos][value - min_value]++ == 0)
						unused_value[con_pos]--;
					unknown_variable[con_pos]--;

					/* If exactly as many unknown variables as unused 
					 * values you will need to shorten domains for every
					 * other variable attached to the constraint
					 */
					if (unused_value[con_pos] == unknown_variable[con_pos])
						for (int aff_var_i : constraints.get(con_pos).variables) {

							/* Ignore already set variables */
							if (variable[aff_var_i] != min_value - 1)
								continue;

							/* Cut used values of constraint from the domains of unknown variables */
							for (int val_i = 0 ; val_i != domain_size[aff_var_i] ; ++val_i)
								if (value_uses[con_pos][domain[aff_var_i][val_i] - min_value] != 0) {
									cut_values[var_i]++;
									swap(domain[aff_var_i], val_i--, --domain_size[aff_var_i]);
									cut_stack[cut_stack_top++] = aff_var_i;
								}

							/* If domain size becomes zero you have to undo domain cuts */
							if (domain_size[aff_var_i] == 0) {

								/* Undo partial changes to constraints */
								while (con_pos_i-- != 0) {
									con_pos = attach[aff_var_i].get(con_pos_i);
									if (con_pos >= constraint_count)
										break;
									if (--value_uses[con_pos][value - min_value] == 0)
										unused_value[con_pos]++;
									unknown_variable[con_pos]++;
								}

								/* Undo partial changes to domains */
								for (int cut = 0 ; cut != cut_values[var_i] ; ++cut) {
									int cut_var_i = cut_stack[--cut_stack_top];
									domain_size[cut_var_i]++;
								}
								cut_values[var_i] = 0;

								/* Try next value */
								continue next_value;
							}
						}
				}
				/* Value was set successfully
				 * Go to try for the next variable
				 */
				go_on = true;
				continue next_variable;
			}
			/* No values matching - Backtrack */
			go_on = false;
			variables_fixed--;
		/* Failed when backtracking required for 1st variable */
		} while (variables_fixed != 0);
		return false;
	}

	/* Check if has next iterator function */
	public boolean hasNext()
	{
		if (finished)
			return false;
		if (next_found || go())
			return next_found = true;
		finished = true;
		return false;
	}

	/* Get next iterator */
	public int[] next()
	{
		if (finished)
			throw new NoSuchElementException();
		if (!next_found && !go()) {
			finished = true;
			throw new NoSuchElementException();
		}
		next_found = false;
		return copy(variable, variable_count);
	}

	/* Remove iterator function */
	public void remove() {}

	/* Variable values */
	int[] variable;

	/* Order of variable usage */
	int[] order;

	/* Number of constraints used */
	int constraint_count;

	/* Domains per variable */
	int[][] domain;

	/* Size of active domain per variable */
	int[] domain_size;

	/* Number of unused values per constraint */
	int[] unused_value;

	/* Number of unknown variables per constraint */
	int[] unknown_variable;

	/* Number of uses per value per constraint */
	int[][] value_uses;

	/* Variables_fixed */
	int variables_fixed;

	/* Domains used */
	int[] domain_offset;

	/* Number of values cut from each domain */
	int[] cut_values;

	/* Domain cuts while running */
	int[] cut_stack;

	/* Number of elements in cut array */
	int cut_stack_top;

	/* Indicates if next element found */
	boolean next_found;

		/* End of anonymous class for iterator */
		};
	}

	/* Typedef set of integers to allow array creation */
	private static class IntSet extends HashSet <Integer> {
		private static final long serialVersionUID = 2;
	}

	/* Set timeout for the run function
	 * Zero or negative means infinity
	 */
	public void findAllTimeout(long millis)
	{
		timeout = millis;
	}

	/* Find all solutions and refine domains
	 * Returns the number of values cut
	 * If timeout then return -1
	 * If problem cannot be solved throw exception
	 */
	public int findAll()
	{
		propagate();
		Iterator <int[]> it = iterator();
		IntSet [] real_domain = new IntSet [variable_count];
		for (int i = 0 ; i != variable_count ; ++i)
			real_domain[i] = new IntSet();
		long start_time = System.currentTimeMillis();
		int solutions = 0;
		int cut = 0;
		for (;;) {
			if (timeout > 0 && System.currentTimeMillis() - start_time > timeout)
				return -1;
			if (!it.hasNext()) {
				if (solutions == 0)
					throw new NoSuchElementException();
				break;
			}
			solutions++;
			int[] combination = it.next();
			for (int val_i = 0 ; val_i != variable_count ; ++val_i)
				real_domain[val_i].add(combination[val_i]);
		}
		/* Cut domains to contain only values found in solutions */
		for (int var_i = 0 ; var_i != variable_count ; ++var_i)
			for (int val_i = 0 ; val_i != gen_domain_size[var_i] ; ++val_i)
				if (!real_domain[var_i].contains(gen_domain[var_i][val_i])) {
					gen_domain[var_i][val_i--] = gen_domain[var_i][--gen_domain_size[var_i]];
					propagation_variables.add(var_i);
					cut++;
				}
		return cut;
	}

	/* Copy equal to 2nd argument first elements of array */
	private static int[] copy(int[] arr, int len)
	{
		int[] copy = new int [len];
		for (int i = 0 ; i != len ; ++i)
			copy[i] = arr[i];
		return copy;
	}

	/* Swap elements of array */
	private static void swap(int[] a, int i, int j)
	{
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	/* Testing main */
	public static void main(String[] args)
	{
		int size = 32;
		int[] mapping = randomMapping(size);
		mapping = distinctMapping(size);
		System.out.println("Mapping:  " + toString(mapping));
		HashSet <Integer> query = new HashSet <Integer> ();
		HashSet <Integer> result = new HashSet <Integer> ();
		int qsize = (int) Math.ceil(Math.sqrt(size));
		int dsize, turn = 1;
		Combinator engine = new Combinator(size);
		engine.findAllTimeout(100);
		int reds = 0;
		int limit = qsize;
		do {
			query.clear();
			result.clear();
		//	for (int i = 0 ; i != (turn == 1 ? size * 100 : qsize) ; ++i)
		//		query.add(gen.nextInt(size) + 1);
			int set_size = 1 << (limit - turn);
			int var = 1;
			both:
			do {
				for (int i = 0 ; i != set_size ; ++i) {
					if (var > size)
						break both;
					query.add(var++);
				}
				var += set_size;
			} while (var <= size);
			for (int x : query)
				result.add(mapping[x - 1]);
			System.out.println("\nTurn: " + turn);
			engine.constraint(toArray(query), toArray(result));
			System.out.println("Query:   " + toString(toArray(query)));
			System.out.println("Result:  " + toString(toArray(result)));
			int bsize = 0;
			for (int v = 1 ; v <= size ; ++v)
				bsize += engine.domain(v).length;
			long time = System.currentTimeMillis();
			System.out.print("Searching...");
			boolean timeout = true;
			if (engine.findAll() < 0)
				System.out.println("   Timeout.  :(");
			else {
				timeout = false;
				time = System.currentTimeMillis() - time;
				System.out.println("   Done!  :D    (" + time + " ms)");
			}
			dsize = 0;
			System.out.println("Domains:");
			for (int v = 1 ; v <= size ; ++v) {
				dsize += engine.domain(v).length;
				System.out.println("Domain " + v + ": {" + toString(engine.domain(v)) + "}");
			}
			if (bsize == dsize)
				System.out.print("No reduction...");
			else {
				System.out.print("Reduction!");
				reds++;
			}
			if (timeout)
				System.out.println("   (timeout)");
			else
				System.out.println("   (no timeout)");
			turn++;
		} while (dsize != size);
		System.out.println("\nMapping:  " + toString(mapping));
		System.out.println("\nReductions:  " + reds);
	}

	private static int[] randomMapping(int size)
	{
		Random gen = new Random();
		int[] mapping = new int [size];
		for (int i = 0 ; i != size ; ++i)
			mapping[i] = gen.nextInt(size) + 1;
		return mapping;
	}

	private static int[] distinctMapping(int size)
	{
		Random gen = new Random();
		int[] mapping = new int [size];
		for (int i = 0 ; i != size ; ++i)
			mapping[i] = i + 1;
		for (int i = 0 ; i != size ; ++i)
			swap(mapping, i, gen.nextInt(size - i) + i);
		return mapping;
	}

	private static String toString(int[] a) {
		if (a.length == 0)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.append(a[0]);
		for (int i = 1 ; i != a.length ; ++i) {
			buf.append(',');
			buf.append(a[i]);
		}
		return buf.toString();
	}

	private static int[] toArray(HashSet <Integer> s)
	{
		int[] r = new int [s.size()];
		int i = 0;
		for (int n : s)
			r[i++] = n;
		return r;
	}
}
