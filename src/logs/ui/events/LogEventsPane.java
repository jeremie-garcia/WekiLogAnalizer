package logs.ui.events;

import javafx.scene.layout.Pane;

public class LogEventsPane extends Pane {

	private String key;
	private int index;

	public LogEventsPane(String key, int index) {
		this.index = index;
		this.key = key;
	}

}
