package randomPackage;

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
		
		/*RandomAgent test = new RandomAgent();
		test.findRandomPath();
		test.env.printStateMachine();
		test.printPath();
		
		StateMachineEnvironment testEnv = new StateMachineEnvironment(5,2);
		RandomAgent test2 = new RandomAgent(testEnv);
		test2.findRandomPath();
		test2.env.printStateMachine();
		test2.printPath();*/
		
		smartAgent tester = new smartAgent();
		
		//this is going to be used to test the path matching algorithm
		ArrayList<Episode> testMem = new ArrayList<Episode>();
		testMem.add(new Episode('b', 0, 1));
		testMem.add(new Episode('a', 1, 2));
		testMem.add(new Episode('b', 1, 3));
		testMem.add(new Episode('b', 2, 4));
		
		ArrayList<Episode> testCurrent = new ArrayList<Episode>();
		testCurrent.add(new Episode('a', 1, 9));
		
		tester.episodicMemory = testMem;
		tester.currentPath = testCurrent;
		
		if(tester.analyzeMove()){
			System.out.println("SUCCESS!!!!");
		}
		
		
		//TEST 2
		
		StateMachineEnvironment testEnv5 = new StateMachineEnvironment(5,4);
		smartAgent tester2 = new smartAgent(testEnv5);
		
		ArrayList<Episode> testMem2 = new ArrayList<Episode>();
		testMem2.add(new Episode('a', 0, 1));
		testMem2.add(new Episode('b', 0, 2));
		testMem2.add(new Episode('c', 0, 3));
		testMem2.add(new Episode('b', 0, 4));
		testMem2.add(new Episode('c', 0, 5));
		testMem2.add(new Episode('d', 2, 6));
		
		ArrayList<Episode> testCurrent2 = new ArrayList<Episode>();
		testCurrent2.add(new Episode('a', 0, 100));
		testCurrent2.add(new Episode('b', 0, 200));
		testCurrent2.add(new Episode('c', 0, 300));
		
		tester2.episodicMemory = testMem2;
		tester2.currentPath = testCurrent2;
		
		if(tester2.analyzeMove()){
			System.out.println("SUCCESS!!!!");
		}
		
		
	}
	
}