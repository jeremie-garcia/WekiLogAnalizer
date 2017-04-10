package logs.ui.events;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * This is a simple container for logEventsNode It stores a log key and an index
 * for filtering and repositioning purpose.
 *
 * @author jeremiegarcia
 *
 */
public class LogEventsPane extends Pane {

	private String key;
	private int index;
	private Color col;

	public LogEventsPane(String key, int index, Color col) {
		this.index = index;
		this.key = key;
		this.col = col;
	}

}
