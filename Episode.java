package passphraseReplacement;

public class Episode { 
	
	private char command;
	private int sensorValue;
	private int stateID;
	
	public Episode(char cmd, int sensor, int state) {
		command = cmd;
		sensorValue = sensor;
		stateID = state;
		
	}
	
	public char getCommand(){
		return command;
	}
	
	public int getSensorValue(){
		return sensorValue;
	}
	
	public int getStateID(){ 
		return stateID;
	}
}