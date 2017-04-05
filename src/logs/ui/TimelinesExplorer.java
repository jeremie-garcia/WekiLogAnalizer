package logs.ui;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private ScrollPane scrollPane;

	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.logEventsManager = logManager;
		this.setPrefWidth(600);
		ruler = new RulerAndRange();
		ruler.prefWidthProperty().bind(this.widthProperty());
		this.setBottom(ruler);

		this.centralPane = new VBox();
		this.centralPane.setPadding(new Insets(5, 0, 5, 0));
		Group contentGroup = new Group();
		contentGroup.getChildren().add(centralPane);
		scrollPane = new ScrollPane(contentGroup);
		// scrollPane.hvalueProperty().addListener(new ChangeListener<Number>()
		// {
		//
		// @Override
		// public void changed(ObservableValue<? extends Number> observable,
		// Number oldValue, Number newValue) {
		// System.out.println("Offset V " +
		// JavaFXUtils.getVerticalOffset(scrollPane) + " H: "
		// + JavaFXUtils.getHorizontalOffset(scrollPane));
		//
		// }
		// });

		this.setCenter(scrollPane);
		centralPane.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(ruler.getVisiblePercentage()));
		this.ruler.getVisibleMinPercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double hValue = JavaFXUtils.getScrollHValueFromPercentage(scrollPane, newValue.doubleValue());
				scrollPane.setHvalue(hValue);
			}
		});

		this.centralPane.scaleXProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("Scale " + newValue.doubleValue());

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

		for (String key : this.logEventsManager.getLogevents().keySet()) {
			Pane pane = new Pane();
			Text txt = new Text(key);
			txt.setFont(Font.font(10));
			txt.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(this.centralPane.scaleXProperty()));
			txt.xProperty().bind(ruler.getVisibleMinPercentage().multiply(UnitConverter.getTotalLengthInScene()));
			txt.setTranslateY(6);
			pane.getChildren().add(txt);
			Line l = new Line(start, 10, end, 10);
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logEventsManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(this.centralPane.scaleXProperty()));
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
