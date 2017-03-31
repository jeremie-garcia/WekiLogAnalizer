package logs.ui.events;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import logs.model.LogEvent;
import logs.ui.EventInspector;
import logs.ui.UnitConverter;

public class LogEventNode extends Group {

	private LogEvent logEvent;
	private Circle prevActive;

	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		Circle circle = new Circle(UnitConverter.getPosFromTime(logEvent.getTimeStamp()), 10, 5);
		circle.setFill(Color.BLUE);
		this.getChildren().add(circle);

		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				EventInspector.getInstance().update(logEvent);
				circle.setFill(Paint.valueOf("green"));
				if (prevActive != null) {
					prevActive.setFill(Paint.valueOf("black"));
				}
				prevActive = circle;
			}
		});
	}
}