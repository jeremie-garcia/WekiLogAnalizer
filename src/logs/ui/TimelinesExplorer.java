package logs.ui;

import java.util.ArrayList;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
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
import logs.utils.ColorScale;

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
	private ArrayList<Text> labels = new ArrayList<Text>();
	private ArrayList<Group> allPointsGroup = new ArrayList<Group>();
	private ScrollPane scrollPane;

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		System.out.println("heyyyy");
		this.logEventsManager = logManager;
		this.setPrefWidth(600);
		ruler = new RulerAndRange();
		System.out.println(ruler);
		this.setBottom(ruler);

		this.centralPane = new VBox();
		this.centralPane.setPadding(new Insets(5, 0, 5, 0));
		Group contentGroup = new Group();
		contentGroup.getChildren().add(centralPane);
		scrollPane = new ScrollPane(contentGroup);
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
		UnitConverter.updateTimeBounds(this.logEventsManager.getBeginTime(), logEventsManager.getEndTime());

		this.centralPane.getChildren().clear();
		this.labels.clear();

		double start = UnitConverter.getPosInSceneFromTime(this.logEventsManager.getBeginTime());
		double end = UnitConverter.getPosInSceneFromTime(this.logEventsManager.getEndTime());

		int index = 0;

		for (String key : this.logEventsManager.getLogevents().keySet()) {
			Pane pane = new Pane();
			Text txt = new Text(key);
			txt.setFont(Font.font(10));
			txt.scaleXProperty().bind(this.getReversedScaleXBinding());
			txt.translateXProperty().bind(scrollPane.hvalueProperty());

			txt.setTranslateY(6);
			this.labels.add(txt);
			pane.getChildren().add(txt);
			Line l = new Line(start, 10, end, 10);
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logEventsManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.scaleXProperty().bind(getReversedScaleXBinding());
				node.setFillColor(ColorScale.getColorWithGoldenRationByIndex(index));
				points.getChildren().add(node);
			}

			this.allPointsGroup.add(points);
			pane.getChildren().add(points);
			this.centralPane.getChildren().add(pane);
			index++;
		}
	}

	/**
	 * returns the inverse scaling transformation to fit node that need to
	 * preserve their aspect ratios
	 *
	 * @return
	 */
	public DoubleBinding getReversedScaleXBinding() {
		return new SimpleDoubleProperty(1).divide(this.centralPane.scaleXProperty());
	}

}
