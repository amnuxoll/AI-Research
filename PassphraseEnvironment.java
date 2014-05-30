package stateMachineAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * <!-- class PassphraseEnvironment -->
 * 
 * An environment in which the agent can guess characters
 * until it finds the "correct" password. The password
 * may contain equivalenices meaning that the agent will be 
 * told it is correct even if it may not have the optimal password.
 * Characters for the password must be entered in the correct
 * order but can have any number of other characters in between.
 * 
 * @author Hailee Kenney
 * @author Preben Ingvaldsen
 * 
 * @version September 23, 2013
 *
 */
public class PassphraseEnvironment {
	
	// Constants
	private final int MIN_LENGTH = 6; // Minimum passphrase length
	private final int MAX_LENGTH = 6; // Maximum passphrase length
	private final int NUM_EQUIVS_PER_CHAR = 2; // Number of Equivalencies contained in this environment
	private final int EQUIV_LHS_MIN_LENGTH = 2; //Minimum number of characters in the LHS of the equivalencies
	private final int EQUIV_LHS_MAX_LENGTH = 2; //Maximum number of characters in the LHS of the equivalencies
	
	// Instance variables
	private Passphrase passphrase; // The correct passphrase
	private int passLength; // The length of the passphrase
	private int currChar; // Index of the next character that must be entered
	private boolean lastLetter; //Whether or not the last letter seen was a match
	ArrayList<Equiv> equivs; // The list of Equivalencies contained in this environment
	ArrayList<Character> soFar; // The characters the agent as entered so far
	char[] alphabet;
	
	// Variables for data collection purposes
	public int lettersGuessed = 0;
	public int equivsInEnvironment = 0;
	
	//DEBUG
	public boolean debug = true;
	
	/**
	 * The constructor for the environment which sets a random password length within
	 * the range defined by MIN_LENGTH and MAX_LENGTH and generates a random passphrase
	 * of that length (consisting of only lower case letters). Additionally, the constructor
	 * prints out the generated passphrase to the console.
	 */
	public PassphraseEnvironment(char[] alpha) {
		Random random = new Random();
		lastLetter = false;
		
		//Set up the alphabet
		alphabet = alpha;
		// Generate random passphrase length between 3-10 characters
		int range = MAX_LENGTH - MIN_LENGTH;
		passLength = MIN_LENGTH + random.nextInt(range + 1);
		
		// Randomly generate a new passphrase of the given size
		passphrase = new Passphrase(passLength, alphabet);
		passphrase.printPassphrase();
		
		// Set the index into the passphrase to zero
		currChar = 0;
		
		// Initialize the ArrayList to keep track of characters the agent has entered
		soFar = new ArrayList<Character>();		
		
		// Generate the equivalencies using characters in the password
		generateEquivs();
	}
	
	/** 
	 * The reset method is called when the agent wants to attempt a new passphrase.
	 * A reset results in the environment resetting the ArrayList of characters it has
	 * seen and resetting the current index into the correct passphrase (the index of 
	 * which letter needs to be entered next).
	 */
	public void reset() {
		soFar = new ArrayList<Character>();
		currChar = 0;
	}
	
	/**
	 * The tick method accepts and documents characters from the agent and determines
	 * whether or not the character entered is the next letter in the passphrase
	 * 
	 * @param letter 
	 * 		The character the agent wishes to enter next in the passphrase
	 * 
	 * @return boolean
	 * 		Whether or not the entire passphrase has been entered correctly
	 */
	public boolean tick(char letter) {
		lettersGuessed++;
		
		// Add the character to the list of what we've seen so far
		soFar.add(letter);
		int size = soFar.size();
		
		// If the character is the next entry in the pass phrase, increase our index
		// into the passphrase, print out the correct character that was entered, and check
		// if the entire passphrase was entered
		if (letter == passphrase.get(currChar) || 
				(currChar > 1 && isEquiv(soFar.get(size - 2), soFar.get(size - 1)) && !lastLetter)) {
			currChar++;
			//System.out.println("Character Match: " + letter);
			lastLetter = true;
			// If we've seen the entire passphrase, reset and return true
			if (currChar == passLength) {
				reset();
				return true;
			}
		}
		else {
			lastLetter = false;
		}
		
		// If we haven't seen the entire passphrase yet, return false
		return false;
	}
	
	private boolean isEquiv(char last, char current) {
		ArrayList<Character> rhs = new ArrayList<Character>();
		rhs.add(passphrase.get(currChar));
		ArrayList<Character> lhs = new ArrayList<Character>();
		lhs.add(last);
		lhs.add(current);
		
		for(Equiv e : equivs) {
			if(e.isEqual(lhs, rhs)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void generateEquivs() {
		Equiv[][] equivsForPicking = new Equiv[passLength][(alphabet.length)*(alphabet.length)];
		Random random = new Random();
		equivs = new ArrayList<Equiv>();
		char lhs1;
		char lhs2;
		char rhs1;
		ArrayList<Character> rhs;
		ArrayList<Character> lhs;
		int depth = 0;
		
		//Generate all possible equivs for all letters in the passphrase
		for (int i = 0; i < passphrase.size(); i++) {
			depth = 0;
			rhs1 = passphrase.get(i);
			rhs = new ArrayList<Character>();
			rhs.add(rhs1);

			for (int j = 0; j < alphabet.length; j++) {
				lhs1 = alphabet[j];
				
				//If the first lhs letter is equal to the rhs, we should skip that whole group
				if (lhs1 == rhs1) {
					continue;
				}
				
				for (int k = 0; k < alphabet.length; k++) {
					lhs2 = alphabet[k];
					
					//If the second lhs letter is equal to the rhs, skip that particular equiv
					if (lhs2 == rhs1) {
						continue;
					}
					
					//Otherwise, create the equiv and increment the depth
					lhs = new ArrayList<Character>();
					lhs.add(lhs1);
					lhs.add(lhs2);
					
					equivsForPicking[i][depth] = new Equiv(lhs, rhs);
					depth++;
				}
			}
		}
		
		boolean keepGoing = false;
		for (int i = 0; i < NUM_EQUIVS_PER_CHAR * passphrase.size(); i++) {
			keepGoing = false;
			Equiv[] equivsForLetter = equivsForPicking[i/NUM_EQUIVS_PER_CHAR];
			Equiv randomEquiv = equivsForLetter[random.nextInt(equivsForLetter.length)];
			if (randomEquiv == null) {
				i--;
				continue;
			}
			for (Equiv equiv : equivs) {
				if (equiv.lhsEquals(randomEquiv.replacable)) {
					i--;
					keepGoing = true;
					break;
				}
			}
			if (keepGoing) {
				continue;
			}
			equivs.add(randomEquiv);	
		}
		
		//DEBUG
		if (!debug) {
			return;
		}
		
		System.out.println("EQUIVS: ");
		for (Equiv equiv : equivs) {
			equiv.print();
		}
	}
}
