package stateMachineAgent;

import java.util.ArrayList;

/**
 * <!-- class Equiv -->
 * 
 * TODO: Add class description
 * 
 * @author Hailee Kenney
 * @author Preben Ingvaldsen
 * 
 * @version September 6, 2013
 *
 */
public class Equiv {
	public ArrayList<Character> replacable;
	public ArrayList<Character> replaceWith;
	
	/**
	 * Constructor creates an equivalency with a left-hand side (the characters that can be replaced)
	 * and a right-hand side (the character(s) that replace it)
	 * 
	 * @param lhs
	 * 		The series of characters that can be replaced
	 * @param rhs
	 * 		The corresponding character (or series of characters) that can replace lhs
	 */
	public Equiv(ArrayList<Character> lhs, ArrayList<Character> rhs) {
		replacable = lhs;
		replaceWith = rhs;
	}
	
	public boolean isEqual(ArrayList<Character> lhs, ArrayList<Character> rhs) {
		return lhs.equals(replacable) && rhs.equals(replaceWith);
	}
	
	public boolean lhsEquals(ArrayList<Character> lhs) {
		return lhs.equals(replacable);
	}
	
	public void print() {
		for (int i = 0; i < replacable.size(); i++) {
			System.out.print(replacable.get(i));
		}
		System.out.print(" -> ");
		for (int i = 0; i < replaceWith.size(); i++) {
			System.out.print(replaceWith.get(i));
		}
		System.out.println();
	}
}
