
import java.util.*;

public class smartAgent extends Agent{
	
	//Variables
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are equal
	protected ArrayList<int[]> equivStates = new ArrayList<int[2]>;
	
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are not equal
	protected ArrayList<int[]> diffStates = new ArrayList<int[2]>;
	
	// Holds the Agents current transition table (what it thinks the transition table is)
	protected ArrayList<int[]> transitionTable;				// we can know the length of alphabet but don't know how many states there are going to be
	
	// Which state the agent thinks it is in currently
	protected int currentState;
	
	// A list of episodes that the agent thinks it can take to get to the goal state.
	protected ArrayList<Episode> currentPlan = null;
	
	// Which two states the Agent is conjecturing are equal.
	private int[] currentHypothesis = new int[2];
	
	// Does the current state we are in have a path that we have not tried yet? 
	protected boolean hasBestPath = false;
	
	
	
	
	
	
	public smartAgent(){
		super();
		transitionTable = new ArrayList<int[alphabet.length]>;
		currentState = 0;
	}
	
	public smartAgent(StateMachineEnvironment environment){
		super(environment);
		transitionTable = new ArrayList<int[alphabet.length]>;
		currentState = 0;
	}
	
	
	
	
	/*
	 * findBestPath()
	 * This method finds the best path for the agent from the start to finish
	 * and fills out the entire state table for the graph to the best of it's ability
	 */
	public void run(){
		// step1
		this.findRandomPath();
		
		// step2
		this.findNextOpenState();			// open meaning has unfilled transition table entries.
		
		// step 3
		this.makeMove();					// makes the move from the found open state
		
		while(/* transition table not fulllllllllllll */){
			
			// step 4
			this.analyzeMove(/*Step 5*/);		// Checks to see if the state we land in is one we recognize
			
			// step 6
			this.moveRandom( /*boolean analysis */); // analysis will be returned from analyzeMove()
			
			// step 7
			this.testConjecture();
		}		
		// step 8 b loop
		// step 9 beq 
		
		// use equiv states to replace transition table values.
		this.optimizeTransTable();
		
		
	}
	
	
}

















