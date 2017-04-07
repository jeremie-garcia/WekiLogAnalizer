package logs.ui;

import java.util.ArrayList;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logs.utils.JavaFXUtils;

public class TimeRuler extends Pane {

	private SimpleDoubleProperty minT = new SimpleDoubleProperty();
	private SimpleDoubleProperty maxT = new SimpleDoubleProperty();

	private String styleString = "-fx-background-color:white;";
	private int numberOfHatchMark = 5;
	private ArrayList<Text> tickLabels = new ArrayList<Text>();

	public TimeRuler() {
		super();
		this.prefHeight(40);
		this.setStyle(styleString);

		double lineHeight = this.getHeight() / 4;
		double width = this.getWidth();

		Line axis = new Line(0, lineHeight, width, lineHeight);
		this.getChildren().add(axis);

		// Create hatch marks and Text
		double sep = width / numberOfHatchMark;
		double y0 = lineHeight - 5;
		double y1 = y0 + 10;

		for (int i = 0; i <= numberOfHatchMark; i++) {
			Line tickLine = new Line(0, y0, 0, y1);
			tickLine.startXProperty().bind(this.widthProperty().divide(numberOfHatchMark).multiply(i));
			tickLine.endXProperty().bind(this.widthProperty().divide(numberOfHatchMark).multiply(i));
			this.getChildren().add(tickLine);

			if (i != numberOfHatchMark) {
				long dateInMillis = (long) (i * 1 + minT.doubleValue());
				String format = JavaFXUtils.getTimeAsFormattedString(dateInMillis);
				Text text = new Text(format);
				text.setFont(new Font("helvetica", 8));
				text.setY(y1 + 12);
				text.xProperty().bind(this.widthProperty().divide(numberOfHatchMark).multiply(i));
				this.tickLabels.add(text);
				this.getChildren().add(text);
			}
		}

		// call update when there is a change in the properties
		this.maxT.addListener(updateLabelListener());
		this.minT.addListener(updateLabelListener());

	}

	private ChangeListener<Number> updateLabelListener() {
		return new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				long minTime = minT.longValue();
				long maxTime = maxT.longValue();

				long range = maxTime - minTime;
				double width = TimeRuler.this.getWidth();
				double ratio = range / width;

				for (Text text : TimeRuler.this.tickLabels) {
					double scenePos = text.getX();
					long dateInMillis = (long) (scenePos * ratio + minT.doubleValue());
					String format = JavaFXUtils.getTimeAsFormattedString(dateInMillis);
					text.setText(format);
				}

			}
		};
	}

	public SimpleDoubleProperty getMinTimeInMillisProperty() {
		return minT;
	}

	public SimpleDoubleProperty getMaxTimeInMillisProperty() {
		return maxT;
	}

}
