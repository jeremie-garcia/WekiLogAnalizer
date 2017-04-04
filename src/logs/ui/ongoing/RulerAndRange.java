package logs.ui.ongoing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class RulerAndRange extends Pane {

	private ImageView bgImageView = null;
	private Pane rangeSelectionPane = new Pane();

	private Color FILLCOLOR = Color.hsb(90, 0.8, 0.9, 0.4);

	// Interaction state machine
	private boolean drag = false;
	private double prevx, prevy;

	public RulerAndRange() {
		super();
		this.setPrefHeight(80);
		this.setMaxHeight(80);

		VBox vBox = new VBox();

		// time Ruler
		Pane ruler = new Pane();
		ruler.setPrefHeight(10);
		vBox.getChildren().add(ruler);

		// Range Selection
		rangeSelectionPane = createRangeSelector();
		rangeSelectionPane.setPrefHeight(40);
		vBox.getChildren().add(rangeSelectionPane);

		this.getChildren().add(vBox);
	}

	private Pane createRangeSelector() {
		Pane p = new Pane();

		// visible zone for orthozoom
		AnchorPane anchor = new AnchorPane();
		anchor.setPrefHeight(100);
		anchor.setPrefWidth(200);

		// central pane for OrthoZoom
		Rectangle zoomRectangle = new Rectangle();
		zoomRectangle.setFill(FILLCOLOR);
		zoomRectangle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getScreenX();
				prevy = event.getScreenY();
			}
		});
		zoomRectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					double diffy = event.getScreenY() - prevy;
				}

				prevx = event.getScreenX();
				prevy = event.getScreenY();

			}
		});
		zoomRectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});

		AnchorPane.setRightAnchor(zoomRectangle, 0.);
		AnchorPane.setLeftAnchor(zoomRectangle, 0.);
		zoomRectangle.widthProperty().bind(anchor.widthProperty());
		zoomRectangle.heightProperty().bind(anchor.heightProperty());
		anchor.getChildren().add(zoomRectangle);

		// right rectangle for handles
		Rectangle rightHandle = new Rectangle(10, 20);
		anchor.getChildren().add(rightHandle);
		rightHandle.setFill(Color.LIGHTGREY);
		AnchorPane.setRightAnchor(rightHandle, 0.);
		rightHandle.heightProperty().bind(anchor.heightProperty());
		rightHandle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getScreenX();
				prevy = event.getScreenY();
			}
		});
		rightHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					double diffy = event.getScreenY() - prevy;

					System.out.println("right " + diffx + " " + diffy);
					anchor.setTranslateX(anchor.getTranslateX() + diffx);
				}
				prevx = event.getScreenX();
				prevy = event.getScreenY();

			}
		});
		rightHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});
		for (int i = 0; i < 4; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(rightHandle.heightProperty()
					.multiply(new SimpleDoubleProperty(1).divide(4).multiply(i + 1)).divide(2));
			AnchorPane.setRightAnchor(c, 2.);
			anchor.getChildren().add(c);
		}

		// left and right rectangle for handles
		Rectangle leftHandle = new Rectangle(10, 20);
		anchor.getChildren().add(leftHandle);
		leftHandle.heightProperty().bind(anchor.heightProperty());
		leftHandle.setFill(Color.LIGHTGREY);
		AnchorPane.setLeftAnchor(leftHandle, 0.);
		leftHandle.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = true;
				prevx = event.getScreenX();
				prevy = event.getScreenY();
			}
		});
		leftHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getScreenX() - prevx;
					double diffy = event.getScreenY() - prevy;

					System.out.println("left " + diffx + " " + diffy);
					anchor.setTranslateX(anchor.getTranslateX() + diffx);

				}
				prevx = event.getScreenX();
				prevy = event.getScreenY();
			}
		});
		leftHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});
		for (int i = 0; i < 4; i++) {
			Circle c = new Circle(0, 0, 3);
			c.setFill(Color.DARKGREY);
			c.translateYProperty().bind(leftHandle.heightProperty()
					.multiply(new SimpleDoubleProperty(1).divide(4).multiply(i + 1)).divide(2));
			AnchorPane.setLeftAnchor(c, 2.);
			anchor.getChildren().add(c);
		}

		p.getChildren().add(anchor);

		return p;
	}

	public void updateZoom() {
		// double visibleDuration = UnitConverter.getVisibleRange();
		// double zoomWidth = UnitConverter.getPixFromDuration(visibleDuration);
		// this.zoomRectangle.setWidth(zoomWidth);
		// double zoomPosX =
		// UnitConverter.getPosFromTime(UnitConverter.getMinVisibleTime());
	}

	public void setBgImageView(ImageView imgView) {
		if (this.bgImageView != null) {
			rangeSelectionPane.getChildren().remove(this.bgImageView);
		}
		this.bgImageView = imgView;
		this.bgImageView.fitWidthProperty().bind(this.widthProperty());
		this.bgImageView.fitHeightProperty().bind(this.heightProperty());
		this.rangeSelectionPane.getChildren().add(0, imgView);
	}

}
