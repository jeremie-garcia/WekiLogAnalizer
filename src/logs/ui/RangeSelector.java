package logs.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * This class is a range selector to select a portion of a scene (handles on
 * borders and orthozoom style selection rectangle). </br>
 * Other Components can use these DoubleProperties:</br>
 * visibleMinPercentage : the minimum value that it selected in percentage</br>
 * visibleMaxPercentage: the max value that is selected in percentage</br>
 * visiblePercentage: the selected range in percentage</br>
 *
 * A background image can be set (usually a scene screen capture)
 *
 * @author jeremiegarcia
 *
 */
public class RangeSelector extends Pane {

	private static final double SCALE_DELTA = 1.01;
	private SimpleDoubleProperty visibleMinPercentage = new SimpleDoubleProperty();
	private SimpleDoubleProperty visibleMaxPercentage = new SimpleDoubleProperty();
	private SimpleDoubleProperty visiblePercentage = new SimpleDoubleProperty();

	// background (could try something different with cameras looking at the
	// scene)
	private ImageView bgImageView = null;
	private double componentMinimalSizeInPixels = 10;

	public RangeSelector() {
		super();
		this.setStyle("-fx-background-color:#eeeeee;");
		this.setPrefHeight(80);
		this.setPrefWidth(600);

		// Range Selection
		this.createRangeSelector();

		// Select All if double click (or more)
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() > 1) {
					selectAll();
				}
			}
		});
	}

	/**
	 * Selects all the available range. It first change the selection to ensure
	 * all bindings will be activated event if the value is similar to the
	 * previous one.
	 */
	void selectAll() {
		this.updateSelection(0.001, 0.999);
		this.updateSelection(0, 1);
	}

	/**
	 * this methods updates visibleMin, visibleMax and Visible Range according
	 * to new values. It performs simple verifications to ensure validity and
	 * minimal Width of the component (componentMinimalSizeInPixels)
	 *
	 * @param newMin
	 * @param newMax
	 */
	private void updateSelection(double newMin, double newMax) {

		if (newMin != this.visibleMinPercentage.doubleValue() || newMax != this.visibleMaxPercentage.doubleValue()) {

			if (newMin < newMax && ((newMax - newMin) * this.getWidth() > this.componentMinimalSizeInPixels)) {
				double selMin = newMin;
				double selMax = newMax;

				if (selMin < 0) {
					selMin = 0;
				}
				if (selMax > 1.) {
					selMax = 1.;
				}
				double range = selMax - selMin;

				this.visibleMinPercentage.set(selMin);
				this.visibleMaxPercentage.set(selMax);
				this.visiblePercentage.set(range);
			}
		}
	}

	/**
	 * create the scene graph to build the component and define the interactions
	 */
	private void createRangeSelector() {

		// central pane for OrthoZoom
		SelectionRectangle zoomRectangle = new SelectionRectangle();
		zoomRectangle.heightProperty().bind(this.heightProperty());
		zoomRectangle.widthProperty()
				.bind(visibleMaxPercentage.subtract(visibleMinPercentage).multiply(this.widthProperty()));
		zoomRectangle.xProperty().bind(visibleMinPercentage.multiply(this.widthProperty()));

		// right handle
		Handle rightHandle = new Handle(true);
		rightHandle.prefHeightProperty().bind(zoomRectangle.heightProperty());
		rightHandle.layoutXProperty().bind(
				zoomRectangle.xProperty().add(zoomRectangle.widthProperty().subtract(rightHandle.widthProperty())));

		// left handle
		Handle leftHandle = new Handle(false);
		leftHandle.prefHeightProperty().bind(zoomRectangle.heightProperty());
		leftHandle.layoutXProperty().bind(zoomRectangle.xProperty());

		this.getChildren().addAll(zoomRectangle, leftHandle, rightHandle);
	}

	public void setBgImageView(ImageView imgView) {
		if (this.bgImageView != null) {
			this.getChildren().remove(this.bgImageView);
		}
		this.bgImageView = imgView;
		this.bgImageView.fitWidthProperty().bind(this.widthProperty());
		this.bgImageView.fitHeightProperty().bind(this.heightProperty());
		this.getChildren().add(0, imgView);
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

	/**
	 * Selection rectangle with orthozoom features
	 *
	 * @author jeremiegarcia
	 *
	 */
	public class SelectionRectangle extends Rectangle {

		// Interaction state machine
		private boolean drag = false;
		private boolean zoom = false, translate = false;
		private double prevx, prevy;

		public SelectionRectangle() {
			super();

			this.setStroke(Paint.valueOf("black"));
			this.setStrokeWidth(1);
			this.setFill(Color.TRANSPARENT);

			this.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					drag = true;
					prevx = event.getScreenX();
					prevy = event.getScreenY();
					setCursor(Cursor.H_RESIZE);
					event.consume();
				}
			});

			this.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					drag = false;
					zoom = false;
					translate = false;
					setCursor(Cursor.DEFAULT);
					SelectionRectangle.this.setStrokeWidth(1);
					event.consume();
				}
			});

			this.setOnMouseDragged(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (drag) {
						double diffx = event.getScreenX() - prevx;
						double diffy = event.getScreenY() - prevy;

						if (diffx != 0 && (translate || Math.abs(diffx) > 5)) {
							setCursor(Cursor.H_RESIZE);
							translate = true;
							zoom = false;
							double newMin = visibleMinPercentage.doubleValue() + diffx / RangeSelector.this.getWidth();
							double newMax = visibleMaxPercentage.doubleValue() + diffx / RangeSelector.this.getWidth();
							updateSelection(newMin, newMax);
							prevx = event.getScreenX();
						} else if (diffy != 0 && (zoom || Math.abs(diffy) > 5)) {
							setCursor(Cursor.V_RESIZE);
							zoom = true;
							translate = false;
							// A get the central position
							double sel_range = visibleMaxPercentage.doubleValue() - visibleMinPercentage.doubleValue();
							double center = visibleMinPercentage.doubleValue() + sel_range / 2;

							double scale = SCALE_DELTA;
							if (diffy < 0) {
								scale = 1 / SCALE_DELTA;
								;
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

			// use event filter to bypass dots group event capture
			this.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					SelectionRectangle.this.setStrokeWidth(2);
				}

			});

			// use event filter to bypass dots group event capture
			this.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (!drag)
						SelectionRectangle.this.setStrokeWidth(1);
				}

			});

		}
	}

	/**
	 * Class to represent handles for the Range selector
	 *
	 * @author jeremiegarcia
	 *
	 */
	public class Handle extends Pane {

		private boolean isRightHandle = true;
		private int prefWidth = 10;
		// style options
		private String handleStyleString = "-fx-background-color: rgba(220, 220, 220, 0.5);";
		private String handleStyleStringFocus = "-fx-background-color: rgba(207, 242, 4, 0.5);";
		private Color DOTS_FILL_COLOR = Color.DARKGREY;

		// Interaction state machine
		private boolean drag = false;
		private double prevx;
		private double initMin, initMax;

		public Handle(boolean isRight) {
			super();
			this.isRightHandle = isRight;
			this.setPrefWidth(prefWidth);
			this.setStyle(handleStyleString);

			Group dotsGroup = new Group();
			dotsGroup.translateXProperty().bind(this.widthProperty().divide(2));

			for (int i = 1; i <= 3; i++) {
				Circle c = new Circle(0, 0, 3);
				c.setFill(DOTS_FILL_COLOR);
				c.translateYProperty().bind(this.heightProperty().divide(4).multiply(i));
				dotsGroup.getChildren().add(c);
			}
			this.getChildren().add(dotsGroup);

			// event listeners
			this.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					drag = true;
					prevx = event.getScreenX();
					initMin = visibleMinPercentage.doubleValue();
					initMax = visibleMaxPercentage.doubleValue();
					setCursor(Cursor.H_RESIZE);
					event.consume();
				}
			});

			this.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					drag = false;
					setCursor(Cursor.DEFAULT);
					Handle.this.setStyle(handleStyleString);
					event.consume();
				}
			});

			this.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (drag) {
						double diffx = event.getScreenX() - prevx;
						if (diffx != 0) {
							double percentage = diffx / RangeSelector.this.getWidth();
							double newMin = isRightHandle ? initMin : initMin + percentage;
							double newMax = isRightHandle ? initMax + percentage : initMax;
							updateSelection(newMin, newMax);
						}
						event.consume();
					}
				}
			});

			// use event filter to bypass dots group event capture
			this.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					Handle.this.setStyle(handleStyleStringFocus);
				}
			});

			// use event filter to bypass dots group event capture
			this.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (!drag)
						Handle.this.setStyle(handleStyleString);
				}

			});
		}
	}

}
