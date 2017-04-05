package logs.ui;

import java.util.ArrayList;

import com.sun.javafx.geom.transform.Affine2D;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.events.LogEventNode;
import logs.utils.JavaFXUtils;

/**
 * Main container for individual nodes representing each log events. It also
 * contains a ruler and a range selector vertically aligned
 *
 * @author jeremiegarcia
 *
 */
public class TimelinesExplorer extends BorderPane {

	private LogEventsManager logEventsManager;

	private VBox centralPane;
	private RulerAndRange ruler;
	private ArrayList<Group> allPointsGroup = new ArrayList<Group>();
	private Scale horizontalScale = new Scale(1, 1);
	private int VISIBILITY_OFFSET = 10;
	private Insets VISIBILITY_INSETS = new Insets(0, VISIBILITY_OFFSET, 0, VISIBILITY_OFFSET);

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.setPrefWidth(600);
		this.setPrefHeight(600);
		this.logEventsManager = logManager;
		ruler = new RulerAndRange();
		ruler.prefWidthProperty().bind(this.widthProperty());
		// ruler.setPadding(new Insets(5, 0, 5, 0));
		this.setBottom(ruler);

		Rectangle r = new Rectangle();
		// r.setX(-10);
		r.widthProperty().bind(this.widthProperty());
		r.heightProperty().bind(this.heightProperty());
		this.setClip(r);

		this.centralPane = new VBox();
		// this.centralPane.setPadding(new Insets(5, 0, 5, 0));
		this.centralPane.getTransforms().add(horizontalScale);
		this.setCenter(centralPane);

		// scaling and translating functions (mapping between ruler and scene)
		ruler.getVisiblePercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newPercentage) {
				double visibleWidthInPixels = centralPane.getWidth();
				double sceneWidthInSceneUnits = centralPane.getBoundsInLocal().getWidth();
				double ratio = visibleWidthInPixels / sceneWidthInSceneUnits;
				horizontalScale.setX(ratio / newPercentage.doubleValue());
			}
		});

		ruler.getVisibleMinPercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double scenePos = newValue.doubleValue() * UnitConverter.getTotalLengthInScene();
				centralPane.setTranslateX(-scenePos * horizontalScale.getX());
			}
		});
	}

	Circle prevc;

	/**
	 * Update the visualization of the log events Uses the logEventsManager
	 * database
	 *
	 * @param newEventsMap
	 */
	public void update() {
		UnitConverter.updateTimeBounds(this.logEventsManager.getBeginTime(), logEventsManager.getEndTime());

		this.centralPane.getChildren().clear();

		double start = UnitConverter.getPosInSceneFromTime(this.logEventsManager.getBeginTime());
		double end = UnitConverter.getPosInSceneFromTime(this.logEventsManager.getEndTime());

		int index = 0;

		Scale inverseScale = new Scale(1, 1);
		inverseScale.xProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
		for (String key : this.logEventsManager.getLogevents().keySet()) {
			Pane pane = new Pane();
			pane.setPrefHeight(60);
			Text txt = new Text(key);
			txt.setFont(Font.font(10));
			txt.getTransforms().add(inverseScale);
			txt.setTranslateY(6);
			pane.getChildren().add(txt);
			Line l = new Line(start, 10, end, 10);
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logEventsManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
				node.setFillColor(JavaFXUtils.getColorWithGoldenRationByIndex(index));
				points.getChildren().add(node);
			}

			this.allPointsGroup.add(points);
			pane.getChildren().add(points);
			this.centralPane.getChildren().add(pane);
			index++;
		}

		this.ruler.selectAll();
	}

}
