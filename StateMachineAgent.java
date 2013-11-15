package passphraseReplacement;

import java.util.ArrayList;
import java.util.Random;


public class StateMachineAgent {

	// Instance variables
	private Path best = null;  //best path from init to goal the agent knows atm
	private StateMachineEnvironment env;
	private char[] alphabet;
	private ArrayList<Episode> episodicMemory;

	//These are used as indexes into the the sensor array
	private static final int IS_NEW_STATE = 0;
	private static final int IS_GOAL = 1;

	//Sensor values
	public static final int NO_TRANSITION = 0;
	public static final int TRANSITION_ONLY = 1;
	public static final int GOAL = 2;

	//Global state data
	private ArrayList<int[]> equivalentStates;
	private ArrayList<int[]> nonEquivalentStates;
	private ArrayList<int[]> agentTransitionTable;
	public static final int UNKNOWN_TRANSITION = -1; //Used to represent an unknown transition in the transition table
	public static final int GOAL_STATE = 0;
	public static final int INIT_STATE = 1;
    public static final char UNKNOWN_COMMAND = ' '; //a character guaranteed not
                                                   //to be in the alphabet

	// The state the agent is in based off it's own version of the state machine
	private int currentState = 1;
	// A path which the agent expects will take it to the goal
	// In other words, a method of testing it's hypothesis about two states being the same
	private ArrayList<Episode> currentPlan;
    //next command to execute in the current plan
    private int planIndex = 0;
	//The numerical id to assign to the next new state we see
	private int nextStateNumber = 2;
	// A variable to indicate whether or not the agent has a current plan to try
	private boolean hasPlan = false;
	// The hypothesis that the agent is currently testing
	// The agent believes currentHypothesis[0] == currentHypothesis[1] where each entry is a state in the FSM
	private int[] currentHypothesis;
	// As the agent adds states to it's personal mapping of the environment, it has to number them
	// accordingly. This variable keeps track of the next stateID it has not yet used
	private int currentStateID = 1;
	
	
	//Reset limit
	public static final int MAX_RESETS = 1;

	//Tells the agent whether or not to use the reorientation reset
	private boolean reorientation = true;

	// Turns debug printing on and off
	boolean debug = true;

	//DEBUG
	int reorientFailures = 0;
	int resetCount = 0;

	/**
	 * The constructor for the agent simply initializes it's instance variables
	 */
	public StateMachineAgent() {
		env = new StateMachineEnvironment();
		alphabet = env.getAlphabet();
		episodicMemory = new ArrayList<Episode>();
		equivalentStates = new ArrayList<int[]>();
		nonEquivalentStates = new ArrayList<int[]>();
		agentTransitionTable = new ArrayList<int[]>();
		int[] zeroRow = new int[alphabet.length];
		int[] firstState = new int[alphabet.length];
		for (int i = 0; i < zeroRow.length; i++) {
			zeroRow[i] = UNKNOWN_TRANSITION;
			firstState[i] = UNKNOWN_TRANSITION;
		}
		agentTransitionTable.add(zeroRow);
		agentTransitionTable.add(firstState);
	}

	/**
	 * Runs through the "Brute Force" algorithm for the agent, setting the
	 * "best" passphrase to the result
	 */
	public void bruteForce() {
		// Generate an initial path
		generatePath();

		// DEBUG: try the path that was successful (sanity check)
		//tryPath(best);
		//best.printpath();

		// Trim moves off the successful path until we only have the
		// necessary moves remaining. Make this the new best path
		best = trimPath(best);

		// // DEBUG: Print out what the agent has determined the shortests path is
		//best.printpath();
	}

	/**
	 * Guesses randomly until a path to the goal is generated
	 * 
	 * @return
	 * 		The path to the goal that was found
	 */
	public Path generatePath() {
		ArrayList<Character> randomPath = new ArrayList<Character>();
		
		//Use our reset method to make random actions until we reach the goal
		reset();
		resetCount++;
		
		//Pull the episodes we've just created out of memory and parse them into
		//a path
		for (int i = 0; i < episodicMemory.size(); i++){
			randomPath.add(i, episodicMemory.get(i).command);
		}

		best = new Path(randomPath);
		return best;
	}

	/**
	 * Given a full string of moves, tryPath will enter the moves
	 * one by one and determine if the entered path is successful
	 *
	 * CAVEAT:  This method returns 'true' even if the goal is reached
	 * prematurely (before the path has been passed)
	 *
	 * @param best
	 * 		An ArrayList of Characters representing the path to try
	 * 
	 * @return
	 * 		A boolean which is true if the path was reached the goal and
	 * 		false if it did not
	 */
	public boolean tryPath(Path best) {
		boolean[] sensors;
		// Enter each character in the path
		for (int i = 0; i < best.size(); i++) {
			sensors = env.tick(best.get(i));
			int encodedSensorResult = encodeSensors(sensors);
			episodicMemory.add(new Episode(best.get(i), encodedSensorResult, 0));

			if (sensors[IS_GOAL]) {
				//DEBUG
				//System.out.println("Given path works");

				// If we successfully find the goal, return true
				return true;
			}
		}

		//DEBUG
		//System.out.println("Given path fails");

		// If we make it through the entire loop, the path was unsuccessful
		return false;
	}

	/**
	 * trimPassphrase takes in a passphrase (which has been confirmed as
	 * successful) and removes one character at a time until it is able to
	 * determine the shortest version of the passphrase that is still
	 * successful
	 * 
	 * @param toTrim
	 * 		The passphrase to trim characters from
	 * @return
	 * 		toTrim reduced to the least amount of characters possible (not including equivalencies)
	 */
	public Path trimPath(Path toTrim) {
		// Make a copy of the passed-in passphrase so as not to modify it
		Path trimmed = toTrim.copy();
		char removed; //Allows us to keep track of the removed character and add it back in if necessary

		for (int i = 0; i < trimmed.size(); i++) {
			// Trim the current character from the passphrase and test the
			// result
			removed = trimmed.get(i);
			trimmed.remove(i); 
			if (tryPath(trimmed)) {
				// If the result is successful, decrement the index, as we
				// have now no longer seen the element at index i
				i--;
			}
			else {
				// If the result is unsuccessful, the removed element is an
				// important character and must be added back in to the
				// passphrase
				smartReset();
				resetCount++;
				trimmed.add(i, removed);

				//Set the best path equal to the reset path if the reset path is shorter
				Path maybeBest = getMostRecentPath();
				if (maybeBest.size() < best.size()) {
					best = maybeBest;
				}
			}
		}
		return trimmed;
	}

	/**
	 * getMostRecentPath
	 * 
	 * Gets the most recent path present in Episodic Memory
	 * @return The most recent path in episodic memory
	 */
	public Path getMostRecentPath() {
		int lastGoal = findLastGoal(episodicMemory.size() - 2) + 1;
		ArrayList<Character> pathChars = new ArrayList<Character>();
		for (int i = lastGoal; i < episodicMemory.size(); i++) {
			pathChars.add(episodicMemory.get(i).command);
		}
		return new Path(pathChars);
	}

	/**
	 * Resets the agent by having it act randomly until it reaches the goal.
	 * This will be changed to a more intelligent scheme later on
	 */
	public void reset() {
		char toCheck;
		Random random = new Random();
		boolean[] sensors;
		int encodedSensorResult;

		//Currently, the agent will just move randomly until it reaches the goal
		//and magically resets itself
		do {
			toCheck = generateRandomAction();
			sensors = env.tick(toCheck);
			encodedSensorResult = encodeSensors(sensors);
			episodicMemory.add(new Episode(toCheck, encodedSensorResult, 0));
			/*if (episodicMemory.size() > 500000000) {
				System.exit(0);
			}*/

		} while (!sensors[IS_GOAL]); // Keep going until we've found the goal
	}
	
	/**
	 * Generates a random action for the Agent to take
	 * 
	 * @return A random action for the Agent to take
	 */
	public char generateRandomAction() {
		Random random = new Random();
		return alphabet[random.nextInt(alphabet.length)];
	}


	/**
	 * A helper method that takes in a string of action/result pairs and checks
	 * if that string exists in the episodic memory
	 * @param actionString The string to match
	 * @return True if the string was matched in the episodic memory, otherwise false
	 */
	private boolean matchString(ArrayList<String> actionString) {
		int elementToMatchIndex = 0; //The index of the element in actionString we are trying to match
		for(int i = 0; i < episodicMemory.size(); i++)
		{
			if (elementToMatchIndex < actionString.size() - 1) {

				//If the element i in the episodic memory equals the current action in actionString,
				//attempt to match the next element in actionString on the next iteration of the loop
				if (episodicMemory.get(i).equals(actionString.get(elementToMatchIndex))) {
					elementToMatchIndex++;
				}

				//Otherwise, reset the index of the element in actionString we are trying to match, as
				//we must now return to the beginning of the actionString (since our current string
				//of matches was interrupted before the whole actionString was matched)
				else {
					elementToMatchIndex = 0;
				}
			}
			else {

				//If we are on the last element in the actionString and we get a match, the whole string
				//was matched
				if (episodicMemory.get(i).command == actionString.get(elementToMatchIndex).charAt(0)){
					return true;
				}

				else {
					elementToMatchIndex = 0;
				}
			}
		}
		return false;
	}

	/**
	 * A more intelligent reset for the agent that will cause the agent to try to find a path to the goal
	 * by examining its episodic memory
	 */
	public void smartReset() {
		if (reorientation) {
			boolean successCode;
			for(int i = 0; i < MAX_RESETS; i++) {
				successCode = smartResetHelper();
				if (successCode) {
					return;
				}
			}
			reorientFailures++;
			reset();
		}
		else {
			reset();
		}
	}

	/**
	 * An intelligent reset method for the agent that resets by searching its previous moves
	 */
	public boolean smartResetHelper() {
		int matchedStringEndIndex = maxMatchedStringIndex();
		char transitionCharacter;
		boolean[] sensors;
		int sensorEncoding;
		int lastGoal = findLastGoal(episodicMemory.size()) + 1;
		String action;
		if (matchedStringEndIndex == -1) {
			return false;
		}
		for (int i = matchedStringEndIndex + 1; i < lastGoal; i++) {
			transitionCharacter = episodicMemory.get(i).command;
			sensors = env.tick(transitionCharacter);
			sensorEncoding = encodeSensors(sensors);
			action = "" + transitionCharacter + sensorEncoding;
			episodicMemory.add(new Episode(transitionCharacter, sensorEncoding, 0));
			if (sensorEncoding == GOAL) {
				return true;
			}

			//System.err.println(episodicMemory.get(i) + " " + action);
			if (!episodicMemory.get(i).equals(action)) {
				//We're lost, so attempt another reset
				return false;
			}

			//if (episodicMemory.size() > 5000000) {
			//	System.exit(0);
			//}
		}

		return false;
	}

	/**
	 * Finds the ending index of the longest substring in episodic memory before
	 * the previous goal matching the final string of actions the agent has
	 * taken
     *
	 * @return The ending index of the longest substring matching the final string of actions
	 *         the agent has taken
	 */
	private int maxMatchedStringIndex() {
		int lastGoalIndex = findLastGoal(episodicMemory.size());
		int maxStringIndex = -1;
		int maxStringLength = 0;
		int currStringLength;
		boolean actionMatched;
		for (int i = lastGoalIndex; i >= 0; i--) {
			actionMatched = episodicMemory.get(i).equals(episodicMemory.get(episodicMemory.size() - 1));
			if (actionMatched) {
				currStringLength = matchedMemoryStringLength(i);
				if (currStringLength > maxStringLength) {
					maxStringLength = currStringLength;
					maxStringIndex = i;
				}
			}
		}

		return maxStringIndex;
	}

	/**
	 * Starts from a given index and the end of the Agent's episodic memory and moves backwards, returning
	 * the number of consecutive matching characters
	 * @param endOfStringIndex The index from which to start the backwards search
	 * @return the number of consecutive matching characters
	 */
	private int matchedMemoryStringLength(int endOfStringIndex) {
		int length = 0;
		int indexOfMatchingAction = episodicMemory.size() - 1;
		boolean match;
		for (int i = endOfStringIndex; i >= 0; i--) {
			match = episodicMemory.get(i).equals(episodicMemory.get(indexOfMatchingAction));

			if (match) {
				length++;
				indexOfMatchingAction--;
			}
			else {
				return length;
			}
		}

		return length;
	}


	/**
	 * Searches backwards through the list of move-result pairs from the given index
	 * @param toStart The index from which to start the backwards search
	 * @return The index of the previous goal
	 */
	private int findLastGoal(int toStart) {
		for (int i = toStart - 1; i > 0; i --) {
			if (episodicMemory.get(i).sensorValue == IS_GOAL) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Takes in an agent's sensor data and encodes it into an integer
	 * @param sensors The agent's sensor data
	 * @return the integer encoding of that sensor data
	 */
	private int encodeSensors(boolean[] sensors) {
		int encodedSensorResult;

		if (sensors[IS_GOAL]) {
			encodedSensorResult = GOAL;
		}

		else if (sensors[IS_NEW_STATE]) {
			encodedSensorResult = TRANSITION_ONLY;
		}

		else {
			encodedSensorResult = NO_TRANSITION;
		}

		return encodedSensorResult;
	}

    /**
     * makePlanToState
     *
     * creates a new plan to reach a given state (see {@link #currentPlan}) from
     * a given state
     *
     * @param startID   id of the state to start at
     * @param targetID  id of the state we want to reach
     */
    private void makePlanToState(int startID, int targetID) {
        //H & P will write this XD
    }
     
    

    /**
     * getFirstUnkown
     *
     * Given an index into the transition table, this method discovers the first
     * unknwon transition in that row in the table and
     *
     * @param rowIndex  index of the row in the transition table
     *
     * @return the letter in the alphabet that corresponds to that entry or
     * UNKNOWN_COMMAND if it was not found
     */
    private char getFirstUnknown(int rowIndex) {
        int[] row = agentTransitionTable.get(rowIndex);
        for(int i = 0; i < row.length; ++i) {
            if (row[i] == UNKNOWN_TRANSITION) {
                return alphabet[i];
            }
        }

        return UNKNOWN_COMMAND;  // no unknown transition
    }//getFirstUnknown

    
    /**
     * selectNextCommand
     *
     * returns the command the agent should take next depending upon its current
     * state, progress, plan, knowledge etc.
     *
     * SIDE EFFECTS:  a new plan may be created or the current plan advanced
     *
     * @return the command to take
     *
     */
	private char selectNextCommand() {
        char cmd = ' '; //the command to return
        
        //If I've never found a path to the goal I can only act randomly until I
        //find the goal
		if (best == null) {
			return generateRandomAction();
		}

        //If I have an active plan, extract the next action from that plan
        else if (currentPlan != null) {
            Episode currEp = currentPlan.get(planIndex);
            return currEp.command;
        }

        //If there is no plan, then select an action that I've never done before
        //from the state that I believe I'm in (explore)
		else {
            cmd = getFirstUnknown(currentState);
            if (cmd != UNKNOWN_COMMAND) return cmd;
        }
            
        //if we reach this point there is no unknown transition from the current
        //state.  Find the lowest numbered state that has an unknown transition
        //and make a plan to get there
        int state = -1;
        while (cmd == UNKNOWN_COMMAND) {
            state++;
            cmd = getFirstUnknown(state);
        }

        //make a plan to reach that unknown state
        this.currentPlan = null;
        makePlanToState(this.currentState, state);

        //if something went wrong just act randomly
        //(I don't think this should ever happen.)
        return generateRandomAction();
	}
	
	/**
	 * Returns the index of the given character in the alphabet array
	 * @param toCheck the character to find the index of
	 * @return the index of toCheck
	 */
	private int indexOfCharacter(char toCheck) {
		for (int i = 0; i < alphabet.length; i++) {
			if (alphabet[i] == toCheck) {
				return i;
			}
		}
		
		return -1;
	}

    /**
     * acceptCurrentHypothesis
     *
     * the current hypothetic equivlency is not believed to be true.  Update hte
     * list of known equivalents and also update the transition table

     From the journal:
     If were testing a state equivalency hypothesis, then your hypothesis
     becomes "fact" and update your equivalencies list and the transition table
     appropriately.  As part of this merging you should check to see if there
     any two rows that match exactly (unknown transitions don't match).  If
     there are, then they are the same state, merge them.
     */
    private void acceptCurrentHypothesis() {
        //%%%TBD
    }
       
    /**
     * cleanupFailedPlan
     *
     * if a plan fails, then the current hypotheses are assumed to be
     * incorrect.  The two states are added the {@link #nonEquivalentStates}
     * list.  A new state is added to the transition table and a new epmem is
     * added to the episodic memory that indicates we transitioned to that
     * state. this.currentState is also updated.
                 - if you were testing an equivalency then your
                   hypothesis becomes false.  Record the non-equivalency
                   appropriately

                 - otherwise you must have been trying to get to a
                   state as per 7d above.  I'm not sure what to do
                   here as this indicates that a previous equivalency
                   is actually false.  Ignore for now.     
     */
    private void cleanupFailedPlan() {
        //%%%TBD
    }


    /**
     * isCompatibleRow
     *
     * given two rows in the transition table, this method verifies that they
     * are "compatible" i.e., all corresponding entries that are both not
     * unknown are the same value.
     *
     */
    private boolean isCompatibleRow(int[] row1, int[] row2) {
        //%%%TBD

        return false;
    }

    /**
     * makeMove
     *
     * issues a given command and updates the episodic memory, transition table,
     * current plan, etc. as a result.
     *
     * @param cmd the command to issue
     */
    private void makeMove(char cmd) {
		boolean[] sensors = env.tick(cmd);
        int mergedSensors = encodeSensors(sensors);

        //Complete the current episode with the given command
        Episode currEp = this.episodicMemory.get(this.episodicMemory.size() - 1);
        currEp.command = cmd;

        //if we're in the middle of a plan it needs to be updated
        if (this.currentPlan != null) {

            //Advance the plan and verify that the sensors match
            this.planIndex++;
            Episode currPlanEp = this.currentPlan.get(this.planIndex);
            if (currPlanEp.sensorValue != mergedSensors) {
                //Plan has failed
                cleanupFailedPlan();
            }

            //Add an episode that reflects our belief that this hypothesis is
            //correct
            Episode now = new Episode(UNKNOWN_COMMAND, mergedSensors, currPlanEp.stateID);
            episodicMemory.add(now);
            this.currentState = currPlanEp.stateID;
            

            //If we've reached the goal episode for the plan the remove it
            //And verify all hypotheses
            if (this.planIndex == this.currentPlan.size() - 1) {
                this.currentPlan = null;
                //if this was a plan to reach the goal then any hypothetic
                //equivalencies need to be accepted
                if (currPlanEp.stateID == INIT_STATE) {
                    acceptCurrentHypothesis();
                }
            }
        }

        //This 'else' covers the the case where there was no plan.  The agent
        //has just taken a random or semi-random action
        else {
            //Examine the transition to extract what state I believe I'm in
            Episode prev = episodicMemory.get(episodicMemory.size() - 1);
            int[] row = agentTransitionTable.get(prev.stateID);
            this.currentState = row[cmd];

            //If I don't know where I am create a new state and update the table
            if (this.currentState == UNKNOWN_TRANSITION) {
                //if I just reached the goal then I know that I'm at the init
                //state now
                if (mergedSensors == GOAL) {
                    this.currentState = INIT_STATE;
                    row[cmd] = GOAL_STATE;
                }
                //Otherwise create a new state for this new circumstance
                else {
                    currentStateID++;
                    row[cmd] = currentStateID;
                }
            }
            
            //Add an episode to reflect what just happened
            Episode now = new Episode(UNKNOWN_COMMAND, mergedSensors, this.currentState);
            episodicMemory.add(now);
            this.currentState = currentStateID;

            //Find data about previous state that may be the same as the current
            //state
            int equivIndex = maxMatchedStringIndex();
            Episode equivEpisode = episodicMemory.get(equivIndex);
            int[] equivRow = agentTransitionTable.get(equivEpisode.stateID);

            //verify this equiv state has a compatible transition table entry to
            //current state
            if (!isCompatibleRow(row, equivRow)) return;

            //%%%TBD: verify that we haven't already discovered that these
            //states aren't equal

            //hypothesize that equiv state equals the current state
            currentHypothesis = new int[2];
            currentHypothesis[0] = equivEpisode.stateID;
            currentHypothesis[1] = this.currentState;

            //Make a plan to reach the goal based upon the hypothesis
            makePlanToState(equivEpisode.stateID, GOAL_STATE);
            
        }//else

                
        
    }//makeMove
     
    
	/**
	 * Makes the Agent take a random move, record it in episodic memory, and update its transition
	 * table
	 * @return The sensor encoding from the action the Agent has taken
	 */
	private int makeRandomMove() { 
		char action = generateRandomAction();
		boolean[] sensors = env.tick(action);
		int sensorEncoding = encodeSensors(sensors);
		int characterIndex = indexOfCharacter(action);
		if(sensorEncoding != IS_GOAL) {
			agentTransitionTable.get(currentState)[characterIndex] = nextStateNumber;
			currentStateID = nextStateNumber;
			nextStateNumber++;
			int[] row = new int[alphabet.length];
			for (int i = 0; i < row.length; i++) {
				row[i] = UNKNOWN_TRANSITION;
			}
			agentTransitionTable.add(row);
		}
		else {
			agentTransitionTable.get(currentState)[characterIndex] = GOAL_STATE;
			currentStateID = GOAL_STATE;
		}
		episodicMemory.add(new Episode(action, sensorEncoding, currentStateID));
		return sensorEncoding;
	}
	
	public void addNewState()  { 
		
	}

	/**
	 * runs multiple trials wherein a random state machine is solved and the
	 * resulting 'best passphrase' for each is analyzed
	 */
	public static void main(String [ ] args)
	{
		StateMachineEnvironment.NUM_STATES = 10;
		StateMachineEnvironment.GOAL_STATE = 10 - 1;
		StateMachineEnvironment.ALPHABET_SIZE = 8;
		StateMachineEnvironment.NUM_TRANSITIONS = 5;

		for(int i= 0; i < 10; ++i) {
			StateMachineAgent ofSPECTRE;
			ofSPECTRE = new StateMachineAgent();
			System.out.println("ENVIRONMENT INFO:");
			ofSPECTRE.env.printStateMachine();
			ofSPECTRE.env.printPaths();

			System.out.println("AGENT PATHS:");
			for (int j = 0; j < 9; ++j){
				ofSPECTRE.bruteForce();
				ofSPECTRE.best.printpath();
				ofSPECTRE.episodicMemory = new ArrayList<Episode>();
			}

			System.out.println("----------------------------");
		}
	}
}
