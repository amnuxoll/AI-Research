 

import java.util.*;

public class RandomAgent extends Agent{	
	
	/**
	 * RandomAgent Constructor.
	 */
	public RandomAgent(){
		super();
	}
	
	/**
	 * RandomAgent Ctor
	 */
	public RandomAgent(StateMachineEnvironment environment){
		super(environment);
	}
	
	
	/**
	 * main
	 * @param args
	 */
	public static void main(String [] args){
		
		RandomAgent test = new RandomAgent();
		test.findRandomPath();
		test.env.printStateMachine();
		test.printPath();
		
		StateMachineEnvironment testEnv = new StateMachineEnvironment(5,2);
		RandomAgent test2 = new RandomAgent(testEnv);
		test2.findRandomPath();
		test2.env.printStateMachine();
		test2.printPath();
		
		
	}
	
}