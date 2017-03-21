package WekiLogs.logs;

import java.util.ArrayList;

public class LogEvent {

	private String label = "";
	// get the file, used to retrieve the folder and other data
	private String source = "";
	private long timeStamp = 0;
	private ArrayList<String> args;

	public LogEvent(String label, long timeStamps, ArrayList args, String source) {
		super();
		this.label = label;
		this.source = source;
		this.timeStamp = timeStamps;
		this.args = args;
	}

	public String getLabel() {
		return label;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public String getSource() {
		return source;
	}

	public ArrayList<String> getArgs() {
		return args;
	}

}
