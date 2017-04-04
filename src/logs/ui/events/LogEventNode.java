package logs.ui.events;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import logs.model.LogEvent;
import logs.ui.EventInspector;
import logs.ui.UnitConverter;
import logs.utils.ColorScale;

public class LogEventNode extends Group {

	private LogEvent logEvent;
	private static LogEventNode prevActiveNode;
	private int RADIUS = 6;
	private Ellipse item;
	private Color color = Color.BLUE;

	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		item = new Ellipse(UnitConverter.getPosInSceneFromTime(logEvent.getTimeStamp()), 10, RADIUS / 2, RADIUS);
		this.getChildren().add(item);

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
		this.item.setFill(this.color);

	}

	private void highlight(boolean setHighlight) {
		item.setFill(setHighlight ? ColorScale.getEmphasizedColor(color) : color);
		item.setRadiusX(setHighlight ? RADIUS : RADIUS / 2);
		item.setStroke(setHighlight ? Color.RED : Color.BLACK);
		item.setStrokeWidth(setHighlight ? 2 : 1);
	}
}