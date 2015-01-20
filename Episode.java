
public class Episode { 
	
	public char command;     //what the agent did
	public int sensorValue;  //what the agent sensed
	public int stateID;      //the state that the agent was in
	
	public Episode(char cmd, int sensor, int state) {
		command = cmd;
		sensorValue = sensor;
		stateID = state;
		
	}

    public String toString() {
        return "["+stateID+"."+sensorValue+":"+command+"]";
    }
	
    
}
