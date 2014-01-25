
import java.util.*;

public class smartAgent extends Agent{
	
	//Variables
	protected Hashtable<int,int> equivStates = new Hashtable<int,int>();
	protected Hashtable<int,int> diffStates = new Hashtable<int,int>();
	protected ArrayList<int>[] transitionTable;								// we can know the length of alphabet but don't know how many states there are going to be
	
	
	
	public smartAgent(){
		super();
		transitionTable = new ArrayList<int>[alphabet.length];
	}
	
	public smartAgent(StateMachineEnvironment environment){
		super(environment);
		transitionTable = new ArrayList<int>[alphabet.length];
	}
	
	/**
	 * this method finds the random path to the goal
	 */
	public void findRandomPath(){
		
		int length = this.alphabet.length;  // # char in alpha
		char letter;
		int count = 2;  //starting at 2 because this is the next state in the memory for the episodes 
		
		do{
			letter = randomChar(length);
			sensor = this.env.tick(letter);	//updates sensor
			//encode sensors to make into an episode to store in memory\
			int encodedSensorValue = encodeSensors(sensor);
			episodicMemory.add(new Episode(letter, encodedSensorValue, count));
			
		}while(!sensor[IS_GOAL]);
	}
	
	/**
	 *  
	 */
	private int encodeSensors(boolean[] sensors){
		
		int encodedResult;
		if(sensors[IS_GOAL]) encodedResult = MYSTERY_AND_GOAL_ON;
		else if(sensors[IS_NEW_STATE]) encodedResult = MYSTERY_SENSOR_ON;
		else encodedResult = NO_SENSORS_ON;
		
		return encodedResult;
	}
	
	
	/**
	 * returns a random char that is in the alphabet that the agent is using
	 */
	private char randomChar(int length){
		
		int randomLetter = (int)(Math.random()*length);
		char letter = this.alphabet[randomLetter];
		return letter; 
	}
	
	/**
	 * printPath
	 */
	public void printPath(){
		for(int i = 0; i<this.episodicMemory.size();i++){
			System.out.print(this.episodicMemory.get(i).command);
		}
		System.out.println();
		System.out.println(this.episodicMemory.size()-1);
	}
	
	/**
	 *
	 */
	public void findBestPath(){
		this.findRandomPath();
		
		
	}
	
	
}

















