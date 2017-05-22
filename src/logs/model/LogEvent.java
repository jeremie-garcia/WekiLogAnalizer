package logs.model;

import java.util.ArrayList;

import javafx.scene.Node;

/**
 * This is the basic class that represent a log event If duration is set to
 * zero, the event is considered as a point event
 *
 * @author jeremiegarcia
 *
 *with the participation of marie, clement and charlelie
 */
public class LogEvent implements Comparable<LogEvent>{

	protected String label = "";
	protected String source = "";
	protected long timeStamp = 0;
	protected long duration = 0;
	protected ArrayList<String> args;

	public LogEvent(String label, long timeStamps, long duration, ArrayList<String> args, String source) {
		this.label = label;
		this.source = source;
		this.timeStamp = timeStamps;
		this.duration = duration;
		this.args = args;
	}

	public String getLabel() {
		return label;
	}

	public String getSource() {
		return source;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<String> getArgs() {
		return args;
	}

	public long getDuration(){
		return duration;
	}
	
	/**
	 * This means that the event has no duration
	 *
	 * @return
	 */
	public boolean isPointEvent() {
		return duration == 0;
	}

	/**
	 * This methods has to be overidden to have a node displayed in the
	 * inspector for specific types of LogEvents
	 *
	 * @return
	 */
	public boolean hasInspectorNode() {
		return false;
	}

	/**
	 * This methods need to be overidden to give the Node
	 *
	 * @return
	 */
	public Node getInspectorNode() {
		// TODO Auto-generated method stub
		return null;
	}

	//Fait pour le projet SITA
	@Override
	public int compareTo(LogEvent o) {
		if(this.timeStamp>o.getTimeStamp()){
			return 1;
		}
		else if(this.timeStamp==o.getTimeStamp()){
			return 0;
		}
		else{
			return -1;
		}
	}

}
