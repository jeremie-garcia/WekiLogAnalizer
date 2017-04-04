package logs.ui;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.events.LogEventNode;
import logs.ui.ongoing.RulerAndRange;
import logs.utils.ColorScale;

/**
 * Main container for individual nodes representing each log events. It also
 * contains a ruler and a range selector vertically aligned
 *
 * @author jeremiegarcia
 *
 */
public class TimelinesExplorer extends BorderPane {

	private SimpleDoubleProperty scaleX = new SimpleDoubleProperty(1);
	private LogEventsManager logManager;

	VBox centralPane;
	// RulerAndRange ruler;
	BackgroundImage rulerBgImage;
	ArrayList<Text> labels = new ArrayList<Text>();
	ArrayList<Group> allPointsGroup = new ArrayList<Group>();

	// Ruler ruler;
	// RangeSelector rangeSelector;

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.logManager = logManager;
		this.setPrefWidth(600);
		// ruler = new RulerAndRange();
		// this.setBottom(ruler);
		this.centralPane = new VBox();
		this.centralPane.setPadding(new Insets(5, 0, 5, 0));
		ScrollPane scrollPane = new ScrollPane(centralPane);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		scrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaX() != 0) {
					// event.consume();
				}
			}
		});

		this.centralPane.scaleXProperty().bind(UnitConverter.getScaleXProperty());
		this.centralPane.translateXProperty().bind(UnitConverter.getTranslateXProperty());
		this.setCenter(scrollPane);

	}

	Circle prevc;

	/**
	 * Update the visualization of the log events Uses the logEventsManager
	 * database
	 *
	 * @param newEventsMap
	 */
	public void update() {
		UnitConverter.updateTimeBounds(this.logManager.getBeginTime(), logManager.getEndTime());

		this.centralPane.getChildren().clear();
		this.labels.clear();

		double start = UnitConverter.getPosInSceneFromTime(this.logManager.getBeginTime());
		double end = UnitConverter.getPosInSceneFromTime(this.logManager.getEndTime());
		double dur = end - start;

		int index = 0;
		for (String key : this.logManager.getLogevents().keySet()) {
			Pane pane = new Pane();
			Text txt = new Text(key);
			txt.setFont(Font.font(10));
			txt.scaleXProperty().bind(UnitConverter.getReversedScaleXBinding());
			txt.translateXProperty().bind(UnitConverter.getTranslateXProperty());
			txt.setTranslateY(6);
			this.labels.add(txt);
			pane.getChildren().add(txt);
			Line l = new Line(0, 10, dur, 10);
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.setFillColor(ColorScale.getColorWithGoldenRationByIndex(index));
				points.getChildren().add(node);
			}

			this.allPointsGroup.add(points);
			pane.getChildren().add(points);
			this.centralPane.getChildren().add(pane);
			index++;
		}
		// updateRulerBgImage();
	}
	//
	// private void updateRulerBgImage() {
	// WritableImage snapshot = this.centralPane.snapshot(new
	// SnapshotParameters(), null);
	// ImageView imgView = new ImageView(snapshot);
	// this.ruler.setBgImageView(imgView);
	// }

}
