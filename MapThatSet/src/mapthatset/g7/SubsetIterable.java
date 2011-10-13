package mapthatset.g7;

import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;

/* Create a class that iterates through
 * all subsets of given set of elements
 * Each time it returns a new collection
 * which is a different subset of the elements
 */
class SubsetIterable <T> implements Iterable <Vector <T>> {

	private Vector <T> elements;

	public SubsetIterable(Iterable <T> collection)
	{
		elements = new Vector <T> ();
		for (T element : collection)
			elements.add(element);
	}

	public Iterator <Vector <T>> iterator() {
		return new Iterator <Vector <T>> () {

	/* Represents binary number with size
	 * equal to the number of collection
	 */
	boolean[] in = null;

	public void remove() {}

	public boolean hasNext()
	{
		if (elements.size() == 0)
			return false;
		if (in == null)
			return true;
		for (int i = 0 ; i != in.length ; ++i)
			if (!in[i])
				return true;
		return false;
	}

	public Vector <T> next()
	{
		if (!hasNext())
			throw new NoSuchElementException();
		/* First run */
		if (in == null) {
			/* Create the binary 0 */
			in = new boolean [elements.size()];
			for (int i = 0 ; i != in.length ; ++i)
				in[i] = false;
			/* Return empty collection */
			return new Vector <T> ();
		}
		/* Add 1 to the binary number */
		int i = 0;
		while (in[i])
			in[i++] = false;
		in[i] = true;
		/* Create subset of collection */
		Vector <T> subset = new Vector <T> ();
		i = 0;
		for (T element : elements)
			if (in[i++])
				subset.add(element);
		return subset;
	}

		/* End of anonymous class */
		};
	}

	public static void main(String[] args)
	{
		Collection <Integer> a = new Vector <Integer> ();
		for (int i = 0 ; i != 10 ; ++i)
			a.add(i);
		SubsetIterable <Integer> ss = new SubsetIterable <Integer> (a);
		for (Iterable <Integer> s : ss) {
			for (Integer i : s)
				System.out.print(i + " ");
			System.out.println("");
		}
	}
}

