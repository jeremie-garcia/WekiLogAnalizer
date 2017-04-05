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
		zoomRectangle.setOnMousePressed(HandlesPressEventFilter);
		zoomRectangle.setOnMouseReleased(HandlesReleasedEventFilter);
		zoomRectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					double diffy = event.getScreenY() - prevy;

					if (diffx != 0 && (translate || Math.abs(diffx) > 5)) {
						translate = true;
						double newMin = visibleMinPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
						double newMax = visibleMaxPercentage.doubleValue() + diffx / RulerAndRange.this.getWidth();
						updateSelection(newMin, newMax);
						prevx = event.getScreenX();
					}
					if (diffy != 0 && (zoom || Math.abs(diffy) > 5)) {
						zoom = true;
						// A get the central position
						double sel_range = visibleMaxPercentage.doubleValue() - visibleMinPercentage.doubleValue();
						double center = visibleMinPercentage.doubleValue() + sel_range / 2;

						double scale = 1;
						if (diffy > 0) {
							scale = 1.01;
						} else {
							scale = 1 / 1.01;
						}

						double new_range = sel_range * scale;

						double scaledMin = center - new_range / 2;
						double scaledMax = center + new_range / 2;
						updateSelection(scaledMin, scaledMax);
						prevy = event.getScreenY();
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
		Pane rightHandle = new Pane();
		rightHandle.setPrefWidth(10);
		rightHandle.setStyle(
				"-fx-background-color: rgba(220, 220, 220, 0.5);-fx-border-radius: 5; -fx-border-color: rgb(0,0,0);");
		rightHandle.prefHeightProperty().bind(zoomRectangle.heightProperty());
		rightHandle.layoutXProperty().bind(
				zoomRectangle.xProperty().add(zoomRectangle.widthProperty().subtract(rightHandle.widthProperty())));

		rightHandle.addEventFilter(MouseEvent.MOUSE_PRESSED, HandlesPressEventFilter);
		rightHandle.addEventFilter(MouseEvent.MOUSE_RELEASED, HandlesReleasedEventFilter);
		rightHandle.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					event.consume();
					double newMin = initMin;
					double percentage = diffx / RulerAndRange.this.getWidth();
					double newMax = initMax + percentage;
					updateSelection(newMin, newMax);

				}
			}
		});

		Group rightDotsGroup = new Group();
		rightDotsGroup.translateXProperty().bind(rightHandle.widthProperty().divide(2));

		for (int i = 1; i <= 3; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(rightHandle.heightProperty().divide(4).multiply(i));
			rightDotsGroup.getChildren().add(c);
		}
		rightHandle.getChildren().add(rightDotsGroup);

		// left and right rectangle for handles
		Pane leftHandle = new Pane();
		leftHandle.setPrefWidth(10);
		leftHandle.prefHeightProperty().bind(zoomRectangle.heightProperty());
		leftHandle.layoutXProperty().bind(zoomRectangle.xProperty());
		leftHandle.setStyle(
				"-fx-background-color: rgba(220, 220, 220, 0.5);-fx-border-radius: 5; -fx-border-color: rgb(0,0,0);");

		leftHandle.addEventFilter(MouseEvent.MOUSE_PRESSED, HandlesPressEventFilter);
		leftHandle.addEventFilter(MouseEvent.MOUSE_RELEASED, HandlesReleasedEventFilter);
		leftHandle.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					event.consume();
					double percentage = diffx / RulerAndRange.this.getWidth();
					double newMin = initMin + percentage;
					double newMax = initMax;
					updateSelection(newMin, newMax);

				}
			}
		});

		Group leftDotsGroup = new Group();
		leftDotsGroup.translateXProperty().bind(leftHandle.widthProperty().divide(2));
		for (int i = 1; i <= 3; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(rightHandle.heightProperty().divide(4).multiply(i));
			leftDotsGroup.getChildren().add(c);
		}
		leftHandle.getChildren().add(leftDotsGroup);
		p.getChildren().addAll(zoomRectangle, leftHandle, rightHandle);

		return p;
	}

	// Interaction state machine
	private boolean drag = false;
	private boolean zoom = false, translate = false;
	private double prevx, prevy;
	private double initMin, initMax;

	private EventHandler<MouseEvent> HandlesPressEventFilter = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			drag = true;
			prevx = event.getScreenX();
			prevy = event.getScreenY();
			initMin = visibleMinPercentage.doubleValue();
			initMax = visibleMaxPercentage.doubleValue();
			event.consume();
		}
	};

	private EventHandler<MouseEvent> HandlesReleasedEventFilter = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			drag = false;
			zoom = false;
			translate = false;
			event.consume();
		}
	};

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
