 

import java.util.ArrayList;

public abstract class Agent {
	
	//Instance Variables
		protected static StateMachineEnvironment env;
		protected char[] alphabet;
		protected ArrayList<Episode> episodicMemory;
		protected boolean[] sensor;
		
		protected static final int IS_NEW_STATE = 0;
		protected static final int IS_GOAL = 1;
		
		protected static final int INIT_STATE = 1; //This numbers the first state for the machine as state 1
		protected static final char UNKNOWN_COMMAND = ' ';
		
		
		//sensor Values (encoded for Episode use)
		protected static final int NO_SENSORS_ON = 0;
		protected static final int MYSTERY_SENSOR_ON = 1;
		protected static final int MYSTERY_AND_GOAL_ON = 2;
		
	/**
	 * RandomAgent Constructor.
	 */
	public Agent(){
		//mk new SME for the agent to navigate
		env = new StateMachineEnvironment();
		
		//Copies the alphabet from the SME to the agent
		alphabet = env.getAlphabet();
		
		//Allocate the episodicMemory
		episodicMemory = new ArrayList<Episode>();
		
		//Init the sensor array to being false for goal and new state
		sensor = new boolean[2];
		sensor[IS_NEW_STATE] = false;
		sensor[IS_GOAL] = false;
		episodicMemory.add(new Episode(UNKNOWN_COMMAND, NO_SENSORS_ON, INIT_STATE));
		
	}
	
	/**
	 * RandomAgent Ctor
	 */
	public Agent(StateMachineEnvironment environment){
		env = environment;
		
		//Copies the alphabet from the SME to the agent
		alphabet = env.getAlphabet();
		
		//Allocate the episodicMemory
		episodicMemory = new ArrayList<Episode>();
		
		//Init the sensor array to being false for goal and new state
		sensor = new boolean[2];
		sensor[IS_NEW_STATE] = false;
		sensor[IS_GOAL] = false;
		episodicMemory.add(new Episode(UNKNOWN_COMMAND, NO_SENSORS_ON, INIT_STATE));
		
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
			//encode sensors to make into an episode to store in memory
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
	
	
}
