package mapthatset.g7;

/* Unique pair */
public class Pair <F, S> {

	/* First element */
	public final F fst;

	/* Second element */
	public final S snd;

	public Pair(F f, S s) {
		fst = f;
		snd = s;
	}

	public int hashCode() {
		long f = fst.hashCode();
		long s = snd.hashCode();
		/* Cantor's enumeration */
		return new Long(((f + s) * (f + s) + f - s) >> 1).hashCode();
	}

	public boolean equals(Object obj) {
		/* The only use of Java 6's generic-s is here */
		if (!(obj instanceof Pair <?, ?>))
			return false;
		Pair <?, ?> p = (Pair <?, ?>) obj;
		return fst.equals(p.fst) && snd.equals(p.snd);
	}
}
