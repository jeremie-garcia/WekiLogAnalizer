package logs.ui.events;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import logs.model.LogEvent;
import logs.ui.EventInspector;
import logs.ui.UnitConverter;
import logs.utils.ColorScale;

public class LogEventNode extends Group {

	private LogEvent logEvent;
	private static LogEventNode prevActiveNode;
	private int RADIUS = 6;
	private int ACTIVE_RADIUS = 6;
	private Circle circle;
	private Color color = Color.BLUE;

	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		circle = new Circle(UnitConverter.getPosInSceneFromTime(logEvent.getTimeStamp()), 10, RADIUS);
		circle.scaleXProperty().bind(UnitConverter.getReversedScaleXBinding());
		this.getChildren().add(circle);

		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				EventInspector.getInstance().update(logEvent);
				LogEventNode.this.highlight(true);
				if (prevActiveNode != null && prevActiveNode != LogEventNode.this) {
					prevActiveNode.highlight(false);
				}
				prevActiveNode = LogEventNode.this;
			}

		});

		highlight(false);
	}

	public void setFillColor(Color color) {
		this.color = color;
		this.circle.setFill(this.color);

	}

	private void highlight(boolean setHighlight) {
		circle.setFill(setHighlight ? ColorScale.getEmphasizedColor(color) : color);
		circle.setRadius(setHighlight ? ACTIVE_RADIUS : RADIUS);
		circle.setStroke(setHighlight ? Color.RED : Color.BLACK);
		circle.setStrokeWidth(setHighlight ? 2 : 1);
	}
}