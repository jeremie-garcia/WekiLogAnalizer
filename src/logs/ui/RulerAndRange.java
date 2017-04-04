package logs.ui;

import javafx.beans.property.SimpleDoubleProperty;
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

	private SimpleDoubleProperty visibleMinTime = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty visibleMaxTime = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty scaleFactorTime = new SimpleDoubleProperty(1);

	private double selMin = 0;
	private double selMax = 100;

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
		vBox.getChildren().add(rangeSelectionPane);

		this.getChildren().add(vBox);
		this.selectAll();
		System.out.println("hey");
	}

	private void selectAll() {
		this.updateSelection(0, 100);
	}

	private void updateSelection(double newMin, double newMax) {

		System.out.println(newMin + " " + newMax);
		if (newMin < newMax && (newMax - newMin > 0.0001)) {
			selMin = newMin;
			selMax = newMax;
			if (selMin < 0) {
				selMin = 0;
			}
			if (selMax > 100) {
				selMax = 100;
			}
		}

		this.visibleMinTime.set((selMin / 100.0) * UnitConverter.getTimeRange());
		this.visibleMaxTime.set((selMax / 100.0) * UnitConverter.getTimeRange());
		double scaleFactor = (selMax - selMin) / 100;
		this.scaleFactorTime.set(scaleFactor);

	}

	private void drawTimeScale(GraphicsContext gc) {
		gc.setFill(Color.AZURE);
		gc.fillRect(0, 0, this.scaleCanvas.getWidth(), this.scaleCanvas.getHeight());
	}

	private Pane createRangeSelector() {
		Pane p = new Pane();
		// central pane for OrthoZoom
		Rectangle zoomRectangle = new Rectangle();
		zoomRectangle.heightProperty().bind(p.heightProperty());
		zoomRectangle.xProperty().bind(visibleMinTime.divide(UnitConverter.getTimeRange()).multiply(100));
		zoomRectangle.widthProperty()
				.bind(visibleMaxTime.subtract(visibleMinTime).divide(UnitConverter.getTimeRange()).multiply(100));
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
					System.out.println(diffx);
					if (Math.abs(diffx) > 3) {
						updateSelection(selMin - diffx, selMax - diffx);
					}
				}
				prevx = event.getX();
				prevy = event.getY();
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
		rightHandle.setFill(Color.LIGHTGREY);
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
					double diffy = event.getY() - prevy;
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
		leftHandle.setFill(Color.LIGHTGREY);
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
					double diffy = event.getY() - prevy;

					System.out.println("left " + diffx + " " + diffy);

				}
				prevx = event.getX();
				prevy = event.getY();
			}
		});
		leftHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});
		Group leftDotsGroup = new Group();
		leftDotsGroup.translateXProperty().bind(leftHandle.widthProperty().divide(2));
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
}
