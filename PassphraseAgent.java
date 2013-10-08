package passphraseReplacement;

import java.util.ArrayList;
import java.util.Random;

/**
 * <!-- class Agent -->
 * 
 * TODO: Add class description
 * 
 * @author Hailee Kenney
 * @author Preben Ingvaldsen
 * 
 * @version September 6, 2013
 *
 */
public class PassphraseAgent {

	//Constants
	private final static int BRUTE_FORCE = 0;
	private final static int BRUTE_NUX = 1;

	// Instance variables
	private Passphrase best;
	private ArrayList<Character> bestPassphrase; //The shortest correct passphrase the agent has found so far
	private PassphraseEnvironment env; // The agent's copy of the environment which accepts passphrase attempts
	private ArrayList<Character> important;
	private char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f'};

	//Data collection variables
	private int numEquivsInPassphrase = 0;
	private int pass1Equivs = 0;
	private int pass2Equivs = 0;
	private int returned;
	private static int algorithm = BRUTE_FORCE;
	private int optimalTicks = 0;

	//Debug Variable
	boolean debug = true;

	/**
	 * The constructor for the agent simply initializes it's instance variables
	 */
	public PassphraseAgent() {
		env = new PassphraseEnvironment(alphabet);
		bestPassphrase = new ArrayList<Character>();
		important = new ArrayList<Character>();
	}

	/**
	 * Runs through the "Brute Force" algorithm for the agent, setting the "best" passphrase to the result
	 */
	public void bruteForce() {
		// Generate an initial passphrase
		generateFirstPassphrase();

		// Try the passphrase that was successful
		tryPassphrase(best);

		// Trim letters off the successful passphrase until we only have the necessary
		// characters remaining. Make this the new best passphrase
		best = trimPassphrase(best);
		
		if (best.size() == 6 && optimalTicks == 0) {
			optimalTicks = env.lettersGuessed;
		}
		
		// Print out what the agent has determined the passphrase is
		best.printPassphrase();

		best = replaceEquivs(best);

		//Print out the final passphrase
		best.printPassphrase();
	}

	/**
	 * Runs through the "Brute Nux" algorithm for the agent, setting the "best" passphrase to the result
	 */
	public void bruteNux() {
		//Generate and trim the two passphrases to compare
		Passphrase pass1 = generatePassphrase();
		Passphrase pass2 = generatePassphrase();
		pass1 = trimPassphrase(pass1);
		pass1.printPassphrase();
		pass2 = trimPassphrase(pass2);
		pass2.printPassphrase();

		pass1Equivs = pass1.size() - 6;
		pass2Equivs = pass2.size() - 6;

		if ((pass1.size() == 6 || pass2.size() == 6) && optimalTicks == 0) {
			optimalTicks = env.lettersGuessed;
		}
		
		//Compare the two passphrases
		best = compare(pass1, pass2);

		best.printPassphrase();
		//Brute force what remains
		best = replaceEquivs(best);

		numEquivsInPassphrase = pass1Equivs + pass2Equivs;

		//Print out the final passphrase
		best.printPassphrase();
	}

	private Passphrase compare(Passphrase pass1, Passphrase pass2) {
		//Create two indices and set the two indices equal to the last
		//character of each passphrase
		int index1 = pass1.size() - 1;
		int index2 = pass2.size() - 1;

		Passphrase pass1Copy;
		Passphrase pass2Copy;

		//Continue to apply the algorithm while at least one index is greater than 0;
		//if both indices are equal to zero, then we have reached the end of this part of the
		//algorithm, so the loop terminates
		while (index1 != 0 || index2 != 0) {

			//Decrement both indices if the characters at each index are equal
			if (pass1.get(index1) == pass2.get(index2)) {
				index1--;
				index2--;
				if (index1 < 0) {
					index1 = 0;
				}
				if (index2 < 0) {
					index2 = 0;
				}
			}
			else {

				//If only one index is zero, there is only one possible equivalency that can be
				//generated
				if (index2 != 0) {
					//Perform the replacement on the second passphrase
					pass2Copy = pass2.copy();
					index2--;
					pass2.remove(index2);
					pass2.remove(index2);
					pass2.add(index2, pass1.get(index1));

					//If the passphrase failed, reset it to how it was before the replacement
					//occurred
					if (!tryPassphrase(pass2)) {
						pass2 = pass2Copy;
						index2++;
					}
					else {
						//pass2Equivs++;
						if (pass2.size() == 6 && optimalTicks == 0) {
							optimalTicks = env.lettersGuessed;
						}
						continue;
					}
				}
				else if (index1 != 0) {
					//Perform the replacement on the second passphrase
					pass1Copy = pass1.copy();
					index1--;
					pass1.remove(index1);
					pass1.remove(index1);
					pass1.add(index1, pass2.get(index2));

					//If the passphrase failed, reset it to how it was before the replacement
					//occurred
					if (!tryPassphrase(pass1)) {
						pass1 = pass1Copy;
						index1++;
					}
					else {
						//pass1Equivs++;
						if (pass1.size() == 6 && optimalTicks == 0) {
							optimalTicks = env.lettersGuessed;
						}
						continue;
					}
				}

				//If neither replacement worked, decrement the larger of the two indices
				if (index1 > index2) {
					index1--;
				}
				else {
					index2--;
				}
			}
		}

		//Return the shorter of the two passphrases
		if (pass1.size() < pass2.size()) {
			returned = 1;
			return pass1;
		}
		returned = 2;
		return pass2;
	}

	/**
	 * generateFirstPassphrase passes random characters to the environment until it successfuly
	 * enters a string of characters that contains the passphrase
	 */
	public void generateFirstPassphrase() {
		best = generatePassphrase();
		best.printPassphrase();
	}

	public Passphrase generatePassphrase() {
		Random random = new Random();
		char toCheck;
		ArrayList<Character> pass = new ArrayList<Character>();
		// Begin iterating through randomly generated characters 
		do {
			toCheck = alphabet[random.nextInt(alphabet.length)];
			pass.add(toCheck); // Add current char to the string of characters that defines the passphrase
		} while (!env.tick(toCheck)); // Keep going until we've entered the whole passphrase

		return new Passphrase(pass);
	}

	/**
	 * Given a full string of characters, tryPassphrase will enter the characters
	 * one by one and determine if the entered passphrase is successful
	 * 
	 * @param toTry
	 * 		An ArrayList of Characters representing the passphrase to try
	 * 
	 * @return
	 * 		A boolean which is true if the passphrase was successful and false
	 * 		if it was not
	 */
	public boolean tryPassphrase(Passphrase toTry) {

		// Enter each character in the passphrase
		for (int i = 0; i < toTry.size(); i++) {
			if (env.tick(toTry.get(i))) {
				// If we successfully enter the passphrase, return true
				//System.out.println("Given passphrase works");
				return true;
			}
		}

		// If we make it through the entire loop, the passphrase was unsuccessful
		env.reset();
		//System.out.println("Given passphrase fails");
		return false;
	}

	/**
	 * trimPassphrase takes in a passphrase (which has been confirmed as successful) and removes
	 * one character at a time until it is able to determine the shortest version of the passphrase
	 * that is still successful
	 * 
	 * @param toTrim
	 * 		The passphrase to trim characters from
	 * @return
	 * 		toTrim reduced to the least amount of characters possible (not including equivalencies)
	 */
	public Passphrase trimPassphrase(Passphrase toTrim) {
		// Make a copy of the passed-in passphrase so as not to modify it
		Passphrase trimmed = toTrim.copy();
		char removed; //Allows us to keep track of the removed character and add it back in if necessary

		for (int i = 0; i < trimmed.size(); i++) {
			// Trim the current character from the passphrase and test the result
			removed = trimmed.get(i);
			trimmed.remove(i);
			if (tryPassphrase(trimmed)) {
				// If the result is successful, decrement the index, as we have now no longer seen
				// the element at index i
				i--;
			}
			else {
				// If the result is unsuccessful, the removed element is an important character and must
				// be added back in to the passphrase
				trimmed.add(i, removed);
				important.add(removed);
			}
		}
		//env.lettersGuessed = 0;
		return trimmed;
	}

	public Passphrase replaceEquivs(Passphrase toReplace) {
		Passphrase bestSoFar = best.copy();
		Passphrase toTry;

		for (int i = 0; i < bestSoFar.size() - 1; i++) {
			for (int j = 0; j < alphabet.length; j++) {
				toTry = bestSoFar.copy();
				toTry.remove(i);
				toTry.remove(i);
				toTry.add(i, alphabet[j]);

				if(tryPassphrase(toTry)) {
					if (toTry.size() == 6 && optimalTicks == 0) {
						optimalTicks = env.lettersGuessed;
					}
					numEquivsInPassphrase++;
					bestSoFar = toTry;
					i--;
					break;
				}
			}
		}

		return bestSoFar;
	}

	/*public void passphraseMain(String [ ] args)
	{
		int trials = 1;
		PassphraseAgent ofSPECTRE;
		System.out.print("Ticks, ");
		System.out.print("Equivs(Env), ");
		if (PassphraseAgent.algorithm == PassphraseAgent.BRUTE_NUX){
			System.out.print("Equivs(Pass1), ");
			System.out.print("Equivs(Pass2), ");
		}
		System.out.print("Equivs(Passphrase), ");
		System.out.println();
		
		for (int i = 0; i < trials; i++) {
			// Create a new agent (lololol James Bond)
			ofSPECTRE = new PassphraseAgent();

			if (ofSPECTRE.algorithm == ofSPECTRE.BRUTE_NUX) { 
				ofSPECTRE.bruteNux();
			}
			else if (ofSPECTRE.algorithm == ofSPECTRE.BRUTE_FORCE) {
				ofSPECTRE.bruteForce();
			}

			System.out.print(ofSPECTRE.optimalTicks + ", ");
			System.out.print(ofSPECTRE.env.equivsInEnvironment + ", ");
			if (ofSPECTRE.algorithm == ofSPECTRE.BRUTE_NUX){
				System.out.print(ofSPECTRE.pass1Equivs + ", ");
				System.out.print(ofSPECTRE.pass2Equivs + ", ");
			}
			System.out.print(ofSPECTRE.numEquivsInPassphrase);
			System.out.println();
		}

	}*/
}
