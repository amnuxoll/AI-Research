package randomPackage;

public class Episode { 
	
	public char command;   //The command that it took to get there.
	public int sensorValue;
	public StateID stateID;
	
	public Episode(char cmd, int sensor, int state) {
		command = cmd;
		sensorValue = sensor;
		stateID = new StateID(state);
		
	}
	
	public boolean equals(Episode episode){
		if(this.command == episode.command && this.sensorValue == episode.sensorValue){
			return true;
		}
		return false;
	}
	
	/*
	 * copy constructure
	 */
	public Episode(Episode episode){
		this.command = episode.command;
		this.sensorValue = episode.sensorValue;
		this.stateID = new StateID(episode.stateID.get());
	}
    
}
