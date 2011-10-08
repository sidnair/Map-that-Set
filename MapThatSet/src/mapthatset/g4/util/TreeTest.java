package mapthatset.aiplayer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class TreeTest
{
	public int mapLength=0;
	public static int  count  = 1;
	public static int median_counter = 0;  
	private static Node  root;
	private LinkedList<ArrayList<Integer>> list = new LinkedList<ArrayList<Integer>>();
	private Queue<Node> queue = new LinkedList<Node>();
	
	public LinkedList<ArrayList<Integer>> getList() {
		return list;
	}

	public static Node getRoot() {
		return root;
	}

	public static void setRoot(Node root) {
		TreeTest.root = root;
	}
	
	
	public ArrayList<Integer> top(){
		return list.getFirst();
	}
	public ArrayList<Integer> pop(){
		if(!list.isEmpty()){
			return list.removeLast();	
		}
		return null;
	}


	static class Node 
	{
	    Node left;
	    Node right;
	    Set<Integer >value;
	 
	    public Node(Set<Integer> value)
	    { this.value = value; }
	}
	
	public void initialize(int intMapLength)
	{
		this.mapLength= intMapLength;
		SortedSet<Integer> inputSet = new TreeSet<Integer>();
		for(int  i=1; i<=this.mapLength; i++)
		{
			inputSet.add(new Integer(i));
		}
		
		root = new Node(inputSet);
		new TreeTest().createSet(root,inputSet);
		this.buildList();
	}
	
	public void createSet(Node root,SortedSet<Integer> rootSet)
	{
		SortedSet<Integer> firstSet = new  TreeSet<Integer>();
		SortedSet<Integer> secondSet = new  TreeSet<Integer>();
		
		Iterator< Integer> it = rootSet.iterator();
		int subtractTerm =0;
		if(count==2)
		{
			subtractTerm =1;
		}
		
		int[] rootArray = new  int[rootSet.size()-subtractTerm];
		
		int k=0;
		while(it.hasNext())
		{
			if(count==2)
			{
				it.next();
				count=1;
			}
			
		rootArray[k++]= (Integer)it.next();
		}
	
		count++;
		
		int median=0;
		if(rootArray.length % 2 == 0)
		{
			median= (rootArray.length/2)-1; 
		}
		else
		{
			median=(int)Math.floor(rootArray.length/2.0);	
		}
		
		
		for(int j=0; j<=median; j++)
		{
				
			firstSet.add(new Integer(rootArray[j]));
		}
		for(int  k1=median+1; k1<rootArray.length; k1++)
		{
			secondSet.add(new Integer(rootArray[k1]));
		}

		if(!secondSet.isEmpty() && rootArray.length >2)
		{
			secondSet.add(new Integer(rootArray[0]));	
		}
		
		if(!firstSet.isEmpty())
		{
			insert(root,firstSet,rootArray[0]);
		}
		
		if(!secondSet.isEmpty())
		{
			insert(root,secondSet,rootArray[median+1]);
		}
		
		if(firstSet.size() >1)
		{
			this.createSet(root,firstSet);	
		}
		
		if(secondSet.size() >1)
		{
			this.createSet(root,secondSet);	
		}
			
		
	}
	
	
	
	public void insert(Node node, Set<Integer> value,int firstElement) {
	   
	      Iterator<Integer> it = node.value.iterator();
	      int nodeValue = it.next();
	      int nodeLastValue=0;
	      while(it.hasNext()){
				nodeLastValue = (Integer)it.next();
			}
	      
	      
	      if(firstElement <= nodeValue || firstElement <= nodeLastValue){

		      if(!(firstElement <= nodeValue) && firstElement <= nodeLastValue){
		    	  if(node.left == null){
		    		  //System.out.println("  Inserted " + value + " to left of "+ node.value);
		    		  node.left = new Node(value);
		    		  return;
		    	  } if(node.right == null){
		    		  //System.out.println("  Inserted " + value + " to right of " + node.value);
		    		  node.right = new Node(value);
		    		  return;
		    	  }
		      }
		      int nodeLastLeftValue=10000;
		      if(node.left != null){
		    	  Iterator<Integer> it1 = node.left.value.iterator();
			      
			      while(it1.hasNext()){
						nodeLastLeftValue = (Integer)it1.next();
					}
		      }
		      		     
	    	  if (node.left != null && firstElement >nodeLastLeftValue) {
			        insert(node.right, value,firstElement);
			      } else if(node.left != null ) {
			    	  insert(node.left, value,firstElement);
			      }else{
				        //System.out.println("  Inserted " + value + " to left of "+ node.value);
			        node.left = new Node(value);
			        return;
			      }
		    
		      }else if (firstElement >= nodeValue) {
			      if (node.right != null) {
				        insert(node.right, value,firstElement);
				      } else {
				        //System.out.println("  Inserted " + value + " to right of "+ node.value);
				        node.right = new Node(value);
				        return;
				      }
				    }
		   
	  }
	
	public void buildList()
	{
		ArrayList<Integer> listInt  = new ArrayList<Integer>();
		listInt = convertNodeToList(root);
		//list.push(listInt);
		//queue.add(root);

		if (root.left != null)
		{	
			//list.push(convertNodeToList(root.left));
			queue.add(root.left);
		}
		

		if (root.right != null)
		{
			//list.push(convertNodeToList(root.right));
			queue.add(root.right);
		}
	
		traverse();
		return;
	}
	
	public ArrayList<Integer> convertNodeToList(Node tempNode){
		Set<Integer> temp = new TreeSet<Integer>();
		temp=tempNode.value;
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		Iterator<Integer> it = temp.iterator();
		while(it.hasNext()){
			tempList.add(new Integer(it.next()));
		}
		return tempList;
		
	}
	

	
	private void traverse()
	{
		while (!queue.isEmpty())
		{
			Node p = queue.remove();
		
			if (p.left != null)
			{
				queue.add(p.left);
			}
		
			if (p.right != null)
			{
				queue.add(p.right);
			}
		
			list.push(convertNodeToList(p));
			traverse();
		}
		return;
	}

}
