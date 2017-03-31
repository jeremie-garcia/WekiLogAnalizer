package logs.ui;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import klogs.KLogEventsManager;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.events.LogEventNode;

/**
 * Main container for individual nodes representing each log events. It also
 * contains a ruler and a range selector vertically aligned
 *
 * @author jeremiegarcia
 *
 */
public class TimelinesExplorer extends BorderPane {

	private long duration = 0;

	private double scalingFactorForTimeStamps = 1000;

	private SimpleDoubleProperty scaleX = new SimpleDoubleProperty(1);

	private LogEventsManager logManager;

	VBox centralPane;
	Pane ruler;
	BackgroundImage rulerBgImage;
	ArrayList<Text> labels = new ArrayList<Text>();
	ArrayList<Group> allPointsGroup = new ArrayList<Group>();

	// Ruler ruler;
	// RangeSelector rangeSelector;

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.logManager = logManager;
		this.setPrefWidth(600);
		ruler = this.buildRulerAndScale();
		this.setBottom(ruler);
		this.centralPane = new VBox();
		ScrollPane scrollPane = new ScrollPane(centralPane);
		this.setCenter(scrollPane);
	}

	Circle prevc;

	/**
	 * Update the visualisation of the log events Uses the logEventsManager
	 * database
	 *
	 * @param newEventsMap
	 */
	public void update() {
		UnitConverter.updateTimeBounds(this.logManager.getBeginTime(), logManager.getEndTime());

		this.centralPane.getChildren().clear();
		this.labels.clear();

		double start = UnitConverter.getPosFromTime(this.logManager.getBeginTime());
		double end = UnitConverter.getPosFromTime(this.logManager.getEndTime());
		double dur = end - start;

		for (String key : this.logManager.getLogevents().keySet()) {
			Group group = new Group();
			Text txt = new Text(key);
			txt.setFont(Font.font(10));
			this.labels.add(txt);
			group.getChildren().add(txt);
			Line l = new Line(0, 10, dur, 10);
			group.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logManager.getLogevents().get(key)) {
				points.getChildren().add(new LogEventNode(logEvent));
			}

			this.allPointsGroup.add(points);
			group.getChildren().add(points);
			this.centralPane.getChildren().add(group);
		}
		updateRulerBgImage();
	}

	private void updateRulerBgImage() {
		WritableImage snapshot = this.centralPane.snapshot(new SnapshotParameters(), null);
		ImageView imgView = new ImageView(snapshot);
		imgView.fitWidthProperty().bind(this.ruler.widthProperty());
		imgView.fitHeightProperty().bind(this.ruler.heightProperty());
		this.ruler.getChildren().add(imgView);
	}

	private Pane buildRulerAndScale() {
		Pane pane = new Pane();
		pane.setPrefHeight(80);
		pane.setMaxHeight(80);
		pane.setStyle("-fx-background-color: red;");
		return pane;
	}

}
