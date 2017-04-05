package logs.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class RulerAndRange extends Pane {

	// these values are expressed in percentage of the rangeSelectionPane width
	private SimpleDoubleProperty visibleMinPercentage = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty visibleMaxPercentage = new SimpleDoubleProperty(1);
	private SimpleDoubleProperty visiblePercentage = new SimpleDoubleProperty(1);

	private Canvas scaleCanvas = new Canvas();
	private Pane rangeSelectionPane = new Pane();

	private Color FILLCOLOR = Color.hsb(90, 0.8, 0.9, 0.4);

	// Interaction state machine
	private boolean drag = false;
	private double prevx, prevy;

	public RulerAndRange() {
		super();
		this.setStyle("-fx-background-color:#eeeeee;");
		this.setPrefHeight(80);
		this.setPrefWidth(600);
		this.setMaxHeight(80);

		VBox vBox = new VBox();

		// time Ruler as a canvas
		scaleCanvas.heightProperty().set(20);
		scaleCanvas.widthProperty().bind(this.widthProperty());
		vBox.getChildren().add(scaleCanvas);
		drawTimeScale(scaleCanvas.getGraphicsContext2D());

		// Range Selection
		rangeSelectionPane = createRangeSelector();
		rangeSelectionPane.setPrefHeight(60);
		rangeSelectionPane.prefWidthProperty().bind(this.widthProperty());
		vBox.getChildren().add(rangeSelectionPane);

		this.getChildren().add(vBox);
		this.selectAll();
	}

	void selectAll() {
		this.updateSelection(0., 1.);
	}

	private void updateSelection(double newMin, double newMax) {
		if (newMin < newMax && ((newMax - newMin) * this.getWidth() > 25)) {
			double selMin = newMin;
			double selMax = newMax;
			if (selMin < 0) {
				selMin = 0;
			}
			if (selMax > 1.) {
				selMax = 1.;
			}
			this.visibleMinPercentage.set(selMin);
			this.visibleMaxPercentage.set(selMax);
			this.visiblePercentage.set(selMax - selMin);
		}
	}

	private void drawTimeScale(GraphicsContext gc) {
		gc.setFill(Color.RED);
		gc.fillRect(0, 0, this.scaleCanvas.getWidth(), this.scaleCanvas.getHeight());
	}

	private Pane createRangeSelector() {
		Pane p = new Pane();
		p.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1) {
					selectAll();
				}

			}
		});

		// central pane for OrthoZoom
		Rectangle zoomRectangle = new Rectangle();
		zoomRectangle.heightProperty().bind(p.heightProperty());
		zoomRectangle.widthProperty()
				.bind(visibleMaxPercentage.subtract(visibleMinPercentage).multiply(this.widthProperty()));
		zoomRectangle.xProperty().bind(visibleMinPercentage.multiply(this.widthProperty()));

		zoomRectangle.setFill(FILLCOLOR);
		zoomRectangle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getX();
				prevy = event.getY();
			}
		});
		zoomRectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getX() - prevx;
					double diffy = event.getY() - prevy;
					if (Math.abs(diffx) > 5) {
						double newMin = visibleMinPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
						double newMax = visibleMaxPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
						prevx = event.getX();
						updateSelection(newMin, newMax);
					} else if (Math.abs(diffy) > 5) {
						// A get the central position
						double sel_range = visibleMaxPercentage.doubleValue() - visibleMinPercentage.doubleValue();
						double center = visibleMinPercentage.doubleValue() + sel_range / 2;

						double scale = 1;
						if (diffy > 0) {
							scale = 1.1;
						} else {
							scale = 1 / 1.1;
						}

						double new_range = sel_range * scale;

						double scaledMin = center - new_range / 2;
						double scaledMax = center + new_range / 2;

						prevy = event.getY();
						updateSelection(scaledMin, scaledMax);
					}
				}
			}
		});
		zoomRectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				drag = false;
			}
		});

		// right rectangle for handles
		Rectangle rightHandle = new Rectangle(10, 20);
		rightHandle.setFill(Color.LIGHTGREY.deriveColor(1, 1, 1, 0.5));
		rightHandle.heightProperty().bind(zoomRectangle.heightProperty());
		rightHandle.xProperty().bind(
				zoomRectangle.xProperty().add(zoomRectangle.widthProperty().subtract(rightHandle.widthProperty())));
		rightHandle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getX();
				prevy = event.getY();
			}
		});
		rightHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getX() - prevx;
					double newMin = visibleMinPercentage.doubleValue();
					double newMax = visibleMaxPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
					prevx = event.getX();
					updateSelection(newMin, newMax);
				}
			}
		});
		rightHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});

		Group rightDotsGroup = new Group();
		rightDotsGroup.translateXProperty().bind(rightHandle.xProperty().add(rightHandle.getWidth() / 2));
		for (int i = 0; i < 4; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(rightHandle.heightProperty()
					.multiply(new SimpleDoubleProperty(1).divide(3).multiply(i + 1)).divide(2));
			rightDotsGroup.getChildren().add(c);
		}

		// left and right rectangle for handles
		Rectangle leftHandle = new Rectangle(10, 20);
		leftHandle.heightProperty().bind(zoomRectangle.heightProperty());
		leftHandle.xProperty().bind(zoomRectangle.xProperty());
		leftHandle.setFill(Color.LIGHTGREY.deriveColor(1, 1, 1, 0.5));
		leftHandle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getX();
				prevy = event.getY();
			}
		});
		leftHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getX() - prevx;
					double newMin = visibleMinPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
					double newMax = visibleMaxPercentage.doubleValue();
					prevx = event.getX();
					updateSelection(newMin, newMax);
				}
			}
		});
		leftHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});

		Group leftDotsGroup = new Group();
		leftDotsGroup.translateXProperty().bind(leftHandle.xProperty().add(leftHandle.widthProperty().divide(2)));
		for (int i = 0; i < 4; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(leftHandle.heightProperty()
					.multiply(new SimpleDoubleProperty(1).divide(3).multiply(i + 1)).divide(2));
			leftDotsGroup.getChildren().add(c);
		}
		p.getChildren().addAll(zoomRectangle, leftHandle, leftDotsGroup, rightHandle, rightDotsGroup);

		return p;
	}

	public SimpleDoubleProperty getVisibleMinPercentage() {
		return visibleMinPercentage;
	}

	public SimpleDoubleProperty getVisibleMaxPercentage() {
		return visibleMaxPercentage;
	}

	public SimpleDoubleProperty getVisiblePercentage() {
		return visiblePercentage;
	}

}
