package randomPackage;

import java.util.*;

public class smartAgent extends Agent{
	
	//Variables
	protected int UNKNOWN_TRANSITION = -1;
	protected int UNKNOWN_STATE = -1;
	
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are equal
	protected ArrayList<int[]> equivStates = new ArrayList<int[]>();
	
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are not equal
	protected ArrayList<int[]> diffStates = new ArrayList<int[]>();
	
	// Holds the Agents current transition table (what it thinks the transition table is)
	protected ArrayList<int[]> transitionTable;				// we can know the length of alphabet but don't know how many states there are going to be
	
	// Which state the agent thinks it is in currently
	protected int currentState;
	
	// A list of episodes that the agent thinks it can take to get to the goal state.
	protected ArrayList<Episode> currentPlan = null; //?????????????????????????????????????????????????????
	
	// Which two states the Agent is conjecturing are equal.
	private int[] currentHypothesis = new int[2];
	
	// Does the current state we are in have a path that we have not tried yet? 
	protected boolean hasBestPath = false;
	
	// Keeps the agents current path that it is taking
	protected ArrayList<Episode> currentPath = new ArrayList<Episode>();
	
	
	public smartAgent(){
		super();
		transitionTable = new ArrayList<int[]>();
		currentState = 0;
	}
	
	public smartAgent(StateMachineEnvironment environment){
		super(environment);
		transitionTable = new ArrayList<int[]>();
		currentState = 0;
	}
	
	/*
	 * findNextOpenState()
	 * finds the next state in the transition table that has a move
	 * that hasn't been made yet. 
	 */
	public int findNextOpenState(){
		//Look through it to find the next open STATE (not next move from that state)
		for(int i = 0; i < transitionTable.size();  ++i){
			for(int j = 0; j< transitionTable.get(i).length; ++j){
				if(transitionTable.get(i)[j] == UNKNOWN_TRANSITION){
					//return transitionTable.get(i);
					return i;
				}
			}
		}
		return UNKNOWN_STATE;
	}
	
	/*
	 * addRow()
	 *
	 */
	public void addRow(/*?????*/){
		
	}
	
	
	/*
	 * initTransTable()
	 * initializes the transition table based on the original random path.
	 */
	public void initTransTable(){
		//Makes transition table out of the random path
		for(int i = 0; i< episodicMemory.size(); ++i){
			Episode next = episodicMemory.get(i);
			char command = next.command;
			int index = findIndex(command);
			//now we have index of the command
			//we also have the state ID of the state.
			int state = next.stateID.get();
			int[] myTableEntry = new int[alphabet.length];
			
			//Used to init the array to UNKNOWN_TRANSITION to start 
			for(int j = 0; j<myTableEntry.length; ++j){
				myTableEntry[j] = UNKNOWN_TRANSITION;
			}
			myTableEntry[index] = state;
			transitionTable.add(myTableEntry);
		}
	}
	
	
	/*
	 * makeMove(int next)
	 * given a state, makes a move, from that state that has not been tried before
	 * records move in episodicMemory and edit the transition table
	 */
	public void makeMove(int next){
		char nextMove;
		int index;
		do{
			nextMove = randomChar(alphabet.length);
			index = findIndex(nextMove);
		}while(transitionTable.get(next)[index] != UNKNOWN_TRANSITION);
		//At this point we now know what move to make. Thee nextMove
		
		
		sensor = this.env.tick(nextMove);	//updates sensor
		int encodedSensorValue = encodeSensors(sensor);
		currentPath.add(new Episode(nextMove, encodedSensorValue, ++numStates));  //This copies the move into currentPath "memory"
	}
	
	
	
	/*
	 * findIndex(char command)
	 * maps char in alphabet to a location 
	 */
	private int findIndex(char command){
		int index = UNKNOWN_TRANSITION;
		for(int j = 0; j<alphabet.length; ++j){
			if(alphabet[j] == command){
				index = j;
				return j;
			}
		}
		return index;
	}
	
	
	
	/*
	 * isTransitionTableFull()
	 * this is used to see if there are any unknown transitions in the table
	 * used for conjecturing and filling out the table.
	 * argument to the while loop.
	 */
	public boolean isTransitionTableFull(){
		for(int[] x : transitionTable){
			for(int i = 0; i<x.length; ++i){
				if(x[i] == UNKNOWN_TRANSITION){
					return false;
				}
			}
		}
		return true;
	}
	
	//
	public boolean analyzeMove(){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		Episode currentState = currentPath.get(currentPath.size()-1);
		int currentMatchedPathLength = 0;
		
		indexList = checkIfEpisodeOccured(currentState);
		if(indexList.size() > 0){
			currentMatchedPathLength++;
			currentState = currentPath.get(currentPath.size()-(currentMatchedPathLength));
		}
		else{
			return false;
		}
		//we have now updated our state and now we can call our second check episode thing
		
		ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
		
		while(indexList.size() > 0){
			System.out.println(indexList.get(0));
			if(indexList.size() == 1){
				break;
			}
			
			System.out.println(indexList.get(0));  //THIS IS FOR TESTING TAKE ME OUT!!!! Later of course
			
			indexListTemp = indexList; 
			
			indexList = checkIfEpisodeOccured(decrementArrayList(indexList,currentMatchedPathLength), currentState);
			
			
			
			if(indexList.size() > 0){
				currentMatchedPathLength++;
				currentState = currentPath.get(currentPath.size()-(currentMatchedPathLength+1));
			}
		}
		
		//what Happens if it cannot find just one match to the path? 
		//it will use the most recent one. 
		
		
		
		if(!(indexList.size() > 0)){
			indexList = indexListTemp;
		}
		int index = indexList.size()-1;
		//call build Conjecture path on the last index of indexlist (this will always call the last one)
		conjecturePath = buildConjecturePath(indexList.get(index));
		
		//test the conjectured Path
		boolean theyAreTheSame = testConjecture(conjecturePath);
		
		if(theyAreTheSame){
			//add these to the sameStates Table thingy.....yyyyyyyyyyyy
			int[] same = new int[2];
			same[0] = currentState.stateID.get();
			same[1] = episodicMemory.get(indexList.get(index)).stateID.get();
			equivStates.add(same);
		}
		else{
			//add these to the not sateStates table thingy....yyyyyyyyyyy
			int[] diff = new int[2];
			diff[0] = currentState.stateID.get();
			diff[1] = episodicMemory.get(indexList.get(index)).stateID.get();
			diffStates.add(diff);
		}
		
		
		//Now we know if the two states are the same or different.
		//We now need to update the transition table. 
		updateTransitionTable();
		
		
		
		//FOR TESTING
		if(indexList.get(indexList.size() -1) > -1 ){
			return true;
		}
		
		//update transition table.... probably
		return false;
	}
	
	
	//New 2/13/14
	//This method will update the transition table for as long as there are same states
	public void updateTransitionTable(){
		if(equivStates.size() <= 0){
			return;
		}
		
		
		
	}
	
	
	
	
	
	//
	public ArrayList<Integer> decrementArrayList(ArrayList<Integer> list, int decrementAmount){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp = list;
		for(int x : temp){
			x-=decrementAmount;
		}
		return temp;
	}
	
	
	
	//
	public ArrayList<Integer> checkIfEpisodeOccured(ArrayList<Integer> indexList, Episode episode){
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for(int i = 0; i < indexList.size(); ++i){
			if(episode.equals(episodicMemory.get(indexList.get(i)))){
				tempList.add(indexList.get(i));									// this adds the index in episodic memory held in indexList
			}
		}
		return tempList;
	}
	
	
	/*
	 * this is the initial check to see if this state has ever been seen before
	 * in episodicMemory
	 */
	public ArrayList<Integer> checkIfEpisodeOccured(Episode episode){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i = 0; i < episodicMemory.size(); ++i){
			if(episodicMemory.get(i).equals(episode)){
				indexList.add(i);
			}
		}
		return indexList;
	}
	
	// Builds Path from conjectured same state to goal.
	public ArrayList<Episode> buildConjecturePath(int index){
		
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		while(episodicMemory.get(index).sensorValue != MYSTERY_AND_GOAL_ON){
			conjecturePath.add(new Episode(episodicMemory.get(index)));
			++index;
		}
		//workaround for now add the final state to the conjecture path
		conjecturePath.add(new Episode(episodicMemory.get(index)));
				
		return conjecturePath;
	}
	
	
	
	/*
	 *
	 */
	public boolean testConjecture(ArrayList<Episode> conjecturePath){
		boolean[] tempSensor = new boolean[2];
		for(Episode episode: conjecturePath) {
			tempSensor = this.env.tick(episode.command);
		}
		
		return tempSensor[IS_GOAL]; //conjecture was correct....probably
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
		this.initTransTable();			// open meaning has unfilled transition table entries.

		while(!isTransitionTableFull()){
			
			// step 2b
			int next = findNextOpenState();
			
			// step 3
			this.makeMove(next);						// makes the move from the found open state
			
			// step 4
			this.analyzeMove(/*Step 5*/);				// Checks to see if the state we land in is one we recognize
			
			// step 6
			//this.moveRandom( /*boolean analysis */); 	// analysis will be returned from analyzeMove()
			
			// step 7 == step 5 ish
			//this.testConjecture();
		}		
		// step 8 b loop
		// step 9 beq 
		
		// use equiv states to replace transition table values.
		//this.optimizeTransTable();
		
		
	}
}

















