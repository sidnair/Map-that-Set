package mapthatset.g5;

import java.util.ArrayList;
import java.util.Random;
import mapthatset.sim.*;

/*
 * Mapper that easily lets you choose between strategies.
 */
public class G5CustomMapper extends Mapper
{
	
	int intMappingLength;
	String strID = "G5CustomMapper";
	
	// CHANGE THIS TO CHANGE MAPPING STRAT
	private final Mapping mappingStrat = Mapping.RANDOM;
	
	private enum Mapping {
		RANDOM, PERM, BINARY;
	}
	
	private ArrayList< Integer > getNewMapping() {
		Random rdmGenerator = new Random();
		ArrayList< Integer > alNewMapping = new ArrayList< Integer >();
		for ( int intIndex = 0; intIndex < intMappingLength; intIndex ++ )
		{			
			switch(mappingStrat) {
				case RANDOM:
					alNewMapping.add( rdmGenerator.nextInt( intMappingLength ) + 1 );
					break;
				case PERM:
					// Generate numbers until it's not in list...this is very
					// inefficient, but acceptable for our use case.
					int i = 1;
					while (alNewMapping.contains(i)) {
					  i = rdmGenerator.nextInt( intMappingLength ) + 1;
					}
					alNewMapping.add(i);
					break;
				case BINARY:
					alNewMapping.add( (rdmGenerator.nextInt( intMappingLength ) + 1) % 2 + 1 );
					break;
			}
		}
		System.out.println( "The mapping is: " + alNewMapping );
		return alNewMapping;
	}

	@Override
	public void updateGuesserAction(GuesserAction gsaGA) 
	{
		// dumb mapper do nothing here
	}

	@Override
	public ArrayList<Integer> startNewMapping(int intMappingLength) 
	{
		// TODO Auto-generated method stub
		this.intMappingLength = intMappingLength;
		return getNewMapping();
	}

	@Override
	public String getID() 
	{
		// TODO Auto-generated method stub
		return strID;
	}
}