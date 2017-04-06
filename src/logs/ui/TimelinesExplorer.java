package logs.ui;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
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
import logs.ui.events.LogEventsPane;
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
	private UnitConverter unitConverter;

	private VBox centralPane;
	private RangeSelector ruler;
	private ArrayList<Group> allPointsGroup = new ArrayList<Group>();
	private Scale horizontalScale = new Scale(1, 1);
	private int VISIBILITY_OFFSET = 10;
	private Insets VISIBILITY_INSETS = new Insets(2 * VISIBILITY_OFFSET);

	private ArrayList<Text> textLabels;

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.setPrefWidth(800);
		this.setPrefHeight(500);

		this.logEventsManager = logManager;
		this.unitConverter = new UnitConverter(0, 1000);

		ruler = new RangeSelector();
		ruler.prefWidthProperty().bind(this.widthProperty());
		ruler.setPadding(VISIBILITY_INSETS);
		this.setBottom(ruler);

		// set the clip area to prevent scene getting out of the bounds
		Rectangle r = new Rectangle();
		r.setX(-VISIBILITY_OFFSET / 2);
		r.setY(-VISIBILITY_OFFSET / 2);
		r.widthProperty().bind(this.widthProperty().add(VISIBILITY_OFFSET));
		r.heightProperty().bind(this.heightProperty().add(VISIBILITY_OFFSET));
		this.setClip(r);

		this.centralPane = new VBox();
		this.centralPane.prefWidthProperty().bind(this.widthProperty());
		this.centralPane.setPadding(VISIBILITY_INSETS);
		this.centralPane.getTransforms().add(horizontalScale);
		this.setCenter(centralPane);

		// scaling and translating functions (mapping between ruler and scene)
		ruler.getVisiblePercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newPercentage) {
				double visibleWidthInPixels = centralPane.getWidth();
				double sceneWidthInSceneUnits = unitConverter.getDurationInMillisFromPercentage(1.0);
				double ratio = visibleWidthInPixels / sceneWidthInSceneUnits;
				horizontalScale.setX(ratio / newPercentage.doubleValue());
			}
		});

		// translate scena and the Text t keep them visible
		ruler.getVisibleMinPercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double scenePos = unitConverter.getPosInSceneFromPercentage(newValue.doubleValue());
				centralPane.setTranslateX(-scenePos * horizontalScale.getX());

				for (Text txt : textLabels) {
					txt.setX(scenePos * horizontalScale.getX());
				}
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
		long begin = this.logEventsManager.getBeginTime();
		long end = this.logEventsManager.getEndTime();

		// add some duration to avoid end being not visible
		long extendedDuration = end + (end - begin) / 20;

		this.unitConverter = new UnitConverter(begin, extendedDuration);

		double beginPosInScene = unitConverter.getPosInSceneFromTime(begin);
		double extendedEndInScene = unitConverter.getPosInSceneFromTime(extendedDuration);
		System.out.println(extendedEndInScene);

		this.centralPane.getChildren().clear();
		this.textLabels = new ArrayList<Text>();

		int index = 0;

		Scale inverseScale = new Scale(1, 1);
		inverseScale.xProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
		for (String key : this.logEventsManager.getLogevents().keySet()) {
			LogEventsPane pane = new LogEventsPane(key, index);
			pane.setPrefHeight(60);
			Text txt = new Text(key);
			txt.setFont(Font.font(8));
			txt.getTransforms().add(inverseScale);
			txt.setTranslateY(6);
			this.textLabels.add(txt);
			pane.getChildren().add(txt);
			Line l = new Line(beginPosInScene, 10, extendedEndInScene, 10);
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logEventsManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp()));
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

		ImageView backgroundImage = this.createImageViewFromScene();
		this.ruler.setBgImageView(backgroundImage);
	}

	private ImageView createImageViewFromScene() {

		WritableImage snapshot = this.centralPane.snapshot(new SnapshotParameters(), null);
		ImageView imgView = new ImageView(snapshot);
		return imgView;
	}

}
