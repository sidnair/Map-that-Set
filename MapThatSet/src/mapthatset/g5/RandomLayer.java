package mapthatset.g5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RandomLayer {
	private ArrayList<Integer> randomLayer; 
	
	protected RandomLayer(int mappingLength){
		Random r = new Random();
		randomLayer = new ArrayList<Integer>();
		ArrayList<Integer> left = new ArrayList<Integer>();
		for(int i=0;i<mappingLength;i++){
			left.add(i);
		}
		for (int i=0;i<mappingLength;i++){
			int random = r.nextInt(left.size());
			randomLayer.add(left.get(random));
			left.remove(random);
		}		
	}

	protected Integer mapTo(int index) {
		return randomLayer.get(index);
	}
	
	protected int findIndex(Integer mapTo) {
		for(int index=0;index<randomLayer.size();index++){
			if(randomLayer.get(index)==mapTo){
				return index;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		int length = 9;
		RandomLayer rl = new RandomLayer(length);
		for(int i=0;i<length;i++){
			System.out.print(i);
			System.out.print(" ");
		}
		System.out.println();
		for(int i=0;i<length;i++){
			System.out.print(rl.mapTo(i));
			System.out.print(" ");
		}
		System.out.println();
		for(int i=0;i<length;i++){
			System.out.print(rl.findIndex(i));
			System.out.print(" ");
		}
	}

}
