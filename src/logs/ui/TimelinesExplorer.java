package logs.ui;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logs.model.LogEvent;
import wekilogs.WekiLogEventsLoader;

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

	HashMap<String, ArrayList<LogEvent>> eventsMap = null;

	VBox centralPane;
	Pane ruler;
	BackgroundImage rulerBgImage;

	// Ruler ruler;
	// RangeSelector rangeSelector;

	public TimelinesExplorer() {
		super();
		this.setPrefWidth(600);
		ruler = this.buildRulerAndScale();
		this.setBottom(ruler);
		this.centralPane = new VBox();
		ScrollPane scrollPane = new ScrollPane(centralPane);
		this.setCenter(scrollPane);
	}

	Circle prevc;

	/**
	 * Set the events Map to populate the visualisation of the log events
	 *
	 * @param newEventsMap
	 */
	public void setEventsMap(HashMap<String, ArrayList<LogEvent>> newEventsMap) {
		if (newEventsMap != this.eventsMap) {
			this.eventsMap = newEventsMap;
			this.centralPane.getChildren().clear();

			double start = WekiLogEventsLoader.getFirstTimeFromMap(eventsMap) / scalingFactorForTimeStamps;
			double end = WekiLogEventsLoader.getLastTimeFromMap(eventsMap) / scalingFactorForTimeStamps;
			double dur = end - start;

			for (String key : this.eventsMap.keySet()) {
				Group group = new Group();
				Text txt = new Text(key);
				txt.setFont(Font.font(10));
				group.getChildren().add(txt);
				Line l = new Line(0, 10, dur, 10);
				group.getChildren().add(l);

				Group points = new Group();
				for (LogEvent logEvent : this.eventsMap.get(key)) {
					Circle c = new Circle(logEvent.getTimeStamp() / scalingFactorForTimeStamps - start, 10, 5);
					c.setOnMouseEntered(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {
							EventInspector.getInstance().update(logEvent);
							c.setFill(Paint.valueOf("green"));
							if (prevc != null)
								prevc.setFill(Paint.valueOf("black"));

							prevc = c;
						}
					});

					points.getChildren().add(c);
				}

				group.getChildren().add(points);
				this.centralPane.getChildren().add(group);
			}
			updateRulerBgImage();
		}

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
