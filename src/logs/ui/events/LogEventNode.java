package logs.ui.events;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.CenteredRectangle;
import logs.ui.EventInspector;
import logs.ui.TimelinesExplorer;
import logs.utils.JavaFXUtils;

/**
 * This class represents a logEvent in the scene THe shape is an ellipse
 *
 * @author jeremiegarcia
 *
 *         with the participation of marie, clement and charlelie
 */
public class LogEventNode extends Group {

	// Data
	private LogEvent logEvent;
	private String labelOverlay = "";

	// States
	private boolean isSelected = false;

	// Graphical appearance
	private double RADIUS = 6;
	private CenteredRectangle item;
	private Text text = new Text();
	private Color fillColor = Color.BLUE;

	/**
	 * builds a LogEventNode with a LogEvent
	 *
	 * @param event
	 */
	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		item = new CenteredRectangle(this.logEvent.getTimeStamp(), -2, RADIUS / 2, RADIUS);
		text.setPickOnBounds(false);
		this.getChildren().add(item);

		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				EventInspector.getInstance().update(logEvent);
				LogEventNode.this.inspectorHighlight(true);
			}
		});
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				LogEventNode.this.inspectorHighlight(false);
			}
		});

		this.selectionHighlight(isSelected);
	}

	public void setFillColor(Color color) {
		this.fillColor = color;
		this.item.setFill(this.fillColor);
	}

	/**
	 * This methods sets the visual parameters to highlight when the component
	 * is hovered and the context displayed in the inspector (true or false)
	 *
	 * @param isInspectorHighligh
	 */
	public void inspectorHighlight(boolean isInspectorHighligh) {
		item.setFill(isInspectorHighligh ? JavaFXUtils.getEmphasizedColor(fillColor) : fillColor);
		item.setRadiusX(isInspectorHighligh ? RADIUS : RADIUS / 2);
	}

	/**
	 * This methods sets the visual parameters to highlight when the component
	 * is selected (true or false)
	 *
	 * @param isSelectionHighlight
	 */
	public void selectionHighlight(boolean isSelectionHighlight) {
		item.setFill(isSelectionHighlight ? JavaFXUtils.getEmphasizedColor(fillColor) : fillColor);
		item.setStroke(isSelectionHighlight ? Color.RED : Color.BLACK);
		item.setStrokeWidth(isSelectionHighlight ? 2 : 1);
	}

	/**
	 * This methods sets the visual parameters to highlight when the component
	 * is included in a search pattern (true or false)
	 *
	 * @param isPatternHighligh
	 */
	public void patternHighlight(boolean isPatternHighligh) {
		item.setFill(isPatternHighligh ? JavaFXUtils.getEmphasizedColor(fillColor) : fillColor);
		item.setStroke(isPatternHighligh ? Color.GREEN : Color.BLACK);
		// item.setStrokeWidth(isPatternHighligh ? 2 : 1);
	}

	public void setTailleX(double taille) {
		this.item.radiusXProperty().setValue(taille);
		this.RADIUS = taille;
	}

	/**
	 * This methods sets the horizontal position of the node in the scene
	 *
	 * @param posX
	 */
	public void setPosX(double posX) {
		this.item.setCenterX(posX);
	}

	public double getPosX() {
		return this.item.getCenterX();
	}

	public Text getText() {
		return text;
	}

	public void setTextOverlay(String text) {
		this.labelOverlay = text;
	}

	public void setSelected(Boolean isSelected) {
		this.isSelected = isSelected;
		this.selectionHighlight(this.isSelected);
	}

	public CenteredRectangle getItem() {
		return item;
	}

	public LogEvent getLogEvent() {
		return logEvent;
	}
}