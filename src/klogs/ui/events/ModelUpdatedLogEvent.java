package klogs.ui.events;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import logs.model.LogEvent;

public class ModelUpdatedLogEvent extends LogEvent {

	public ModelUpdatedLogEvent(String label, long timeStamps, long duration, ArrayList<String> args, String source) {
		super(label, timeStamps, duration, args, source);
	}

	@Override
	public boolean hasInspectorNode() {
		return true;
	}

	@Override
	public Node getInspectorNode() {
		Rectangle rect = new Rectangle(30, 50);
		rect.setFill(Color.RED);
		return rect;
	}

}
