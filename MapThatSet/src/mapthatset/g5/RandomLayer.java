package mapthatset.g5;

import java.util.ArrayList;
import java.util.Random;

public class RandomLayer {
	private ArrayList<Integer> randomLayer; 
	private int length;
	
	protected RandomLayer(int mappingLength){
		this.length = mappingLength;
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

	protected Integer getMapping(int number) {
		// The mapping has a 1-based index, and the randomLayer ArrayList has a
		// 0-based index, so we must correct for that.
		return randomLayer.get(number);
	}
	
	protected Integer indexOf(int number) {
		return randomLayer.indexOf(number);
	}
	
	
	//this function randomize the original array, and return the original one.
	//my original idea of this class was do randomly map array 
	protected ArrayList<Integer> randomize(ArrayList<Integer> ori){
		if(ori.size()!=length){
			System.out.println("wrong");
			return null;
		}
		
		ArrayList<Integer> original  = new ArrayList<Integer>(ori);
		for(int i=0;i<length;i++){
			ori.set(this.getMapping(i),original.get(i));
		}
		
		return original;		
	}
	
	//this function randomize the original array, and return the original one.
	//my original idea of this class was do randomly map array 
	protected ArrayList<Integer> deRandomize(ArrayList<Integer> ori){
			if(ori.size()!=length){
				System.out.println("wrong");
				return null;
			}
			
			ArrayList<Integer> original  = new ArrayList<Integer>(ori);
			for(int i=0;i<length;i++){
				ori.set(this.indexOf(i),original.get(i));
			}
			
			return original;		
		}
	
	
	public static void main(String[] args) {
		int length = 10;
		RandomLayer rl = new RandomLayer(length);
		for(int i=0;i<length;i++){
			System.out.print(rl.getMapping(i));
			System.out.print(" ");
		}
		System.out.println();
		for(int i=0;i<length;i++){
			System.out.print(rl.indexOf(i));
			System.out.print(" ");
		}
		System.out.println();
		ArrayList<Integer> test = new ArrayList<Integer>();
		
		for (int i=0;i<length;i++){
			test.add(i);
		}
		rl.randomize(test);
		for(int i=0;i<length;i++){
			System.out.print(test.get(i));
			System.out.print(" ");
		}
		System.out.println();
		
		rl.deRandomize(test);
		for(int i=0;i<length;i++){
			System.out.print(test.get(i));
			System.out.print(" ");
		}
		System.out.println();
		
	}

}
