package logs.model;

import java.util.ArrayList;

/**
 * This is the basic class that represents discrete log event
 *
 * @author jeremiegarcia
 *
 */
public class DiscreteLogEvent extends LogEvent {

	private long timeStamp = 0;
	private ArrayList<String> args;

	public DiscreteLogEvent(String label, long timeStamps, ArrayList args, String source) {
		super(label, source);
		this.timeStamp = timeStamps;
		this.args = args;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<String> getArgs() {
		return args;
	}

}
