package stateMachineAgent;

import java.util.ArrayList;
import java.util.Random;

public class Passphrase {
	private ArrayList<Character> passphrase;

	//Debugging Variable
	private boolean debug = true;

	/**
	 * 
	 * @param passLength
	 */
	public Passphrase (int passLength, char[] alphabet) {
		passphrase = new ArrayList<Character>();
		Random random = new Random();
		for (int i = 0; i < passLength; i++) {
			passphrase.add(alphabet[random.nextInt(alphabet.length)]);
		}
	}

	/**
	 * 
	 * @param generated
	 */
	public Passphrase (ArrayList<Character> generated) {
		passphrase = new ArrayList<Character>();
		for (int i = 0; i < generated.size(); i++) {
			passphrase.add(generated.get(i));
		}
	}

	public Passphrase copy() {
		ArrayList<Character> createdCopy = new ArrayList<Character>();
		for (int i = 0; i < passphrase.size(); i++) {
			createdCopy.add(passphrase.get(i));
		}
		return new Passphrase(createdCopy);
	}

	public int size() {
		return passphrase.size();
	}

	public char get(int index) {
		return passphrase.get(index);
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < passphrase.size(); i++) {
			result += passphrase.get(i);
		}
		return result;
	}

	public void printPassphrase() {
		if (debug) {
			System.out.println("Passphrase: " + toString());
		}
	}

	public void remove(int index) {
		passphrase.remove(index);
	}

	public void add(int index, char toAdd) {
		passphrase.add(index, toAdd);
	}
}
