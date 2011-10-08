package mapthatset.g5;

import java.util.ArrayList;
import java.util.Random;

public class RandomLayer {
	private ArrayList<Integer> randomLayer; 
	
	protected RandomLayer(int mappingLength){
		Random r = new Random();
		randomLayer = new ArrayList<Integer>();
		ArrayList<Integer> left = new ArrayList<Integer>();
		for(int i=1;i<=mappingLength;i++){
			left.add(i);
		}
		for (int i=0;i<mappingLength;i++){
			int random = r.nextInt(left.size());
			randomLayer.add(left.get(random));
			left.remove(random);
		}
	}

	protected Integer getMapping(int number) {
		// The mapping has a 1-based index, and the randomLayer ArrayList has a
		// 0-based index, so we must correct for that.
		return randomLayer.get(number - 1);
	}
	
	public static void main(String[] args) {
		int length = 9;
		RandomLayer rl = new RandomLayer(length);
		for(int i=1;i<=length;i++){
			System.out.print(rl.getMapping(i));
			System.out.print(" ");
		}
		System.out.println();
	}

}
