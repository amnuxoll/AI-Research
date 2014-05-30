package stateMachineAgent;

public class Episode { 
	
	public char command;
	public int sensorValue;
	public int stateID;
	
	public Episode(char cmd, int sensor, int state) {
		command = cmd;
		sensorValue = sensor;
		stateID = state;
		
	}
	
    
}
