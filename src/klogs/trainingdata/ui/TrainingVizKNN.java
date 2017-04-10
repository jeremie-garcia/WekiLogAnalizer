package klogs.trainingdata.ui;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import klogs.trainingdata.model.TrainingData;
import klogs.trainingdata.model.TrainingDataSet;
import klogs.ui.events.ModelUpdatedLogEvent;
import logs.utils.JavaFXUtils;

public class TrainingVizKNN extends Pane {

	TrainingDataSet data;
	private int radius = 3;
	private ModelUpdatedLogEvent logEvent;

	public TrainingVizKNN(ModelUpdatedLogEvent event) {
		super();

		// TODO:find bugs in this class (propably loadding issue with event)
		this.logEvent = event;
		this.data = event.getTrainingDataSet();

		// draw colored background depending on the grade
		Color col = JavaFXUtils.getColorForGrade(data.grade);
		this.setStyle(JavaFXUtils.getHexWebStringFromColor(col));

		Pane container = new Pane();

		// build scene

		// add grid scale
		int xmin = data.inputMinValues[0];
		int xmax = data.inputMaxValues[0];
		int xrange = xmax - xmin;
		double step = xrange / 10;

		int ymin = data.inputMinValues[1];
		int ymax = data.inputMaxValues[1];
		int yrange = ymax - ymin;
		double step2 = yrange / 10;

		Group grid = new Group();

		for (double pos = xmin; pos <= xmax; pos = pos + step) {
			Line l = new Line(pos, ymin, pos, ymax);
			grid.getChildren().add(l);
		}

		for (double pos2 = ymin; pos2 <= ymax; pos2 = pos2 + step2) {
			Line l = new Line(xmin, pos2, xmax, pos2);
			grid.getChildren().add(l);
		}

		// add points
		Group points = new Group();
		int index = 1;
		for (TrainingData d : data.examples) {
			Color classColor = JavaFXUtils.getColorWithGoldenRationByIndex(d.output);
			double x = d.inputs.get(0);
			double y = d.inputs.get(1);
			Circle point = new Circle(x, y, radius);
			Tooltip toolTip = new Tooltip(d.toString());
			Tooltip.install(point, toolTip);
			point.setFill(classColor);
			point.setStroke(Color.BLACK);
			Text label = new Text(x + 2, y, index + "");
			label.setScaleY(-1);
			index++;
			points.getChildren().addAll(point, label);
		}

		container.getChildren().addAll(grid, points);
		container.setScaleY(-1);
		container.setTranslateX(-xmin);
		container.setTranslateY(ymin);

		this.getChildren().add(container);
	}
}
