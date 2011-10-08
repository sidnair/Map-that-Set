package mapthatset.g2;

import java.util.ArrayList;

public class PossibleMappings {

	private int n;
	private boolean[][] mappings;
	private int[] finalMapping;
	
	public PossibleMappings(int n) {
		this.n = n;
		mappings = new boolean[n][n];
		
		// Populate possible mappings
		for (int i=0; i < n; i++) {
			for (int j=0; j < n; j++) {
				mappings[i][j] = true;
			}
		}
		
		// Final mapping
		finalMapping = new int[n];
		for (int i=0;i<n;i++) {
			finalMapping[i] = 0;
		}
	}
	
	public void eliminate(int a, int b) {
		a--; b--;
		try {
			mappings[a][b] = false;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		updateFinalMapping(a+1); // added 1 because updateFinalMapping assumes counting from 1
	}
	
	public boolean isPossible(int a, int b) {
		a--; b--;
		boolean res;
		try {
			res = mappings[a][b];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			res = false;
		}
		return res;
	}
	
	public int getN() {
		return n;
	}
	
	public boolean[] aMapsTo(int a) {
		a--;
		return mappings[a];
	}
	
	public ArrayList<Integer> aMapsToAsArrayList(int a) {
		a--;
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i=0; i<n; i++) {
			if (mappings[a][i]) {
				res.add(i);
			}
		}
		return res;
	}
	
	public boolean[] mapsToB(int b) {
		b--;
		boolean[] res = new boolean[n];
		for (int i=0;i<n;i++) {
			res[i] = mappings[i][b];
		}
		return res;
	}

	public ArrayList<Integer> mapsToBAsArrayList(int b) {
		b--;
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i=0; i<n; i++) {
			if (mappings[i][b]) {
				res.add(i);
			}
		}
		return res;
	}
	
	public ArrayList<Integer> intersection(int a1, int a2) {
		a1--; a2--;
		ArrayList<Integer> inter = new ArrayList<Integer>();
		for (int i=0; i<n; i++) {
			if (mappings[a1][i] == true && 
					mappings[a1][i] == mappings[a2][i]) {
				inter.add(i);
			}
		}
		return inter;
	}
	
	private void updateFinalMapping(int a) {
		a--;
		int numTrue = 0;
		int trueIndex = 0;
		for (int i=0;i<n;i++) {
			if (mappings[a][i]) {
				numTrue++;
				trueIndex = i+1;
			}
		}
		if (numTrue == 1) {
			finalMapping[a] = trueIndex;
		}
	}
	
	public ArrayList<Integer> unknownMappings() {
		ArrayList<Integer> unknowns = new ArrayList<Integer>();
		for (int i=0;i<n;i++) {
			if (finalMapping[i] == 0) {
				unknowns.add(i+1);
			}
		}
		return unknowns;
	}
	
	public void print() {
		String str = "\t1\t2\t3\t4\t5\n";
		str += "\t___\t___\t___\t___\t___\n";
		
		for (int i=0;i < n; i++) {
			str = str + (i+1) + " |\t";
			for (int j=0; j < n; j++) {
				str = str + mappings[i][j] + '\t';
			}
			str += '\n';
		}
		System.out.println(str);
	}
	
	public void printLine(boolean[] line) {
		String str = "";
		for (int j=0; j < n; j++) {
			str = str + line[j] + '\t';
		}
		System.out.println(str);
	}
	
	public ArrayList<Integer> getFinalMappingArrayList() {
		ArrayList<Integer> mapping = new ArrayList<Integer>();
		for (int i:finalMapping) {
			mapping.add(i);
		}
		return mapping;
	}
}
