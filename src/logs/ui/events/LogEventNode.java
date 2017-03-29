package logs.ui.events;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import logs.model.LogEvent;

public class LogEventNode extends Group {

	private LogEvent logEvent;

	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		Circle circle = new Circle(10);
		circle.setFill(Color.BLUE);
		this.translateXProperty().setValue(this.logEvent.getTimeStamp());
		this.getChildren().add(circle);
	}
}