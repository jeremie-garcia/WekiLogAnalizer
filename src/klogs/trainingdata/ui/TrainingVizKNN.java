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
	private int radius = 10;
	private ModelUpdatedLogEvent logEvent;

	public TrainingVizKNN(ModelUpdatedLogEvent event) {
		super();

		this.logEvent = event;
		this.data = event.getTrainingDataSet();
		// draw colored background depending on the grade
		Color col = JavaFXUtils.getColorForGrade(data.grade);
		this.setStyle(JavaFXUtils.getHexWebStringFromColor(col));

		Pane container = new Pane();

		// build scene
		// add grid scale
		double min = 0.;
		double max = 600.;
		double range = max - min;
		double step = range / 10.;

		Group grid = new Group();
		for (double pos = min; pos <= max; pos = pos + step) {
			Line lx = new Line(pos, min, pos, max);
			Line ly = new Line(min, pos, max, pos);
			grid.getChildren().addAll(lx, ly);
		}

		// add points
		Group points = new Group();
		int index = 1;
		for (TrainingData d : data.examples) {
			Color classColor = JavaFXUtils.getColorWithGoldenRationByIndex(d.output);
			classColor = JavaFXUtils.applyAlpha(classColor, 1);
			double x = d.inputs.get(0);
			double y = d.inputs.get(1);
			Circle point = new Circle(x, y, radius);
			Tooltip toolTip = new Tooltip(d.toString());
			Tooltip.install(point, toolTip);
			point.setFill(classColor);
			point.setStroke(Color.BLACK);
			Text label = new Text(x + radius + 1, y + radius / 2, index + "");
			label.setScaleY(-1);
			index++;
			points.getChildren().addAll(point, label);
		}

		container.getChildren().addAll(grid, points);
		container.setScaleY(-1);
		container.setTranslateX(min);
		container.setTranslateY(min);

		this.getChildren().add(container);
	}
}
