package logs.model;

/**
 * This is the basic class that represent a log event All other classes should
 * inherit form this one.
 *
 * @author jeremiegarcia
 *
 */
public abstract class LogEvent {

	private String label = "";
	private String source = "";

	public LogEvent(String label2, String source2) {
		this.label = label2;
		this.source = source2;
	}

	public String getLabel() {
		return label;
	}

	public String getSource() {
		return source;
	}

}
