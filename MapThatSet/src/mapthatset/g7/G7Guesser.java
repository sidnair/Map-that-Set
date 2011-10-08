package mapthatset.g7;

import java.util.ArrayList;
import java.util.Random;

import mapthatset.g7.Combinator.Values;
import mapthatset.sim.Guesser;
import mapthatset.sim.GuesserAction;

public class G7Guesser extends Guesser {

	int intMappingLength;
	double guessSize;

	ArrayList< Integer > alGuess = new ArrayList< Integer >();
	ArrayList< Integer > alResponse = new ArrayList< Integer >();
	ArrayList< Integer > unmapped = new ArrayList< Integer >();
	ArrayList< Integer > unguessed = new ArrayList< Integer >();

	Combinator engine;
	Random random = new Random();
	
	public void startNewMapping( int intMappingLength )
	{
		this.intMappingLength = intMappingLength;
		guessSize = Math.ceil((double) intMappingLength / 2.0);
		engine = new Combinator(intMappingLength);
		unmapped = new ArrayList< Integer >();
		for (int i = 1; i <= intMappingLength; ++i)
		{
			unmapped.add(i);
		}
		unguessed = new ArrayList<Integer>(unmapped);
	}
	
	@Override
	public GuesserAction nextAction()
	{
		GuesserAction gscReturn = null;
		if (unguessed.size() == 0)
		{
			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			/* Remove numbers from unmapped that have domain size 1 */
			for (int um: unmapped)
			{
				if (engine.domain(um).length == 1)
				{
					toRemove.add(um);
				}
			}
			unmapped.removeAll(toRemove);
			unguessed = new ArrayList<Integer>(unmapped);
			guessSize = Math.ceil(guessSize / 2.0);
		}

		if (unmapped.size() > 0)
		{
			alGuess = new ArrayList< Integer >();
			System.out.println();
			for (int j = 0; j < guessSize && unguessed.size() > 0; ++j)
			{
				int next = unguessed.remove(random.nextInt(unguessed.size()));
				alGuess.add(next);
			}
			gscReturn = new GuesserAction("q", alGuess);
		}
		else
		{
			alGuess = new ArrayList< Integer >();
			Values finalValues = engine.values();
			/* loop through all variables and add their single domain value */
			for(int m = 1; m <= intMappingLength; ++m)
			{
				alGuess.add(finalValues.values(m)[0]);
			}
			gscReturn = new GuesserAction("g", alGuess);
		}
		return gscReturn;
	}
	
	@Override
	public void setResult( ArrayList< Integer > alResult )
	{
		alResponse = alResult;
		if (alResult.get(0) != 0)
			engine.constraint(convertToArray(alGuess), convertToArray(alResponse));
	}

	@Override
	public String getID() 
	{
		return "G7: Guesser";
	}
	
	private int[] convertToArray(ArrayList <Integer> al)
	{
		int[] arr = new int [al.size()];
		int i = 0;
		for (int n : al)
			arr[i++] = n;
		return arr;
	}
}
