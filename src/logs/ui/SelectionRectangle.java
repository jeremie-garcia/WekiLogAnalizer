package logs.ui;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class SelectionRectangle extends Rectangle {

	private Point2D selectionStartPoint;

	public SelectionRectangle(Point2D selectionStartPoint) {
		super(selectionStartPoint.getX(), selectionStartPoint.getY(), 0, 0);
		this.selectionStartPoint = selectionStartPoint;
		this.setStroke(Color.BLACK);
		this.setFill(Color.TRANSPARENT);
		this.setStrokeLineCap(StrokeLineCap.ROUND);
		this.setVisible(true);
	}

	// update the bounds with the new point
	public void updateFromPoint(Point2D newPoint) {
		if (newPoint.getX() > selectionStartPoint.getX()) {
			this.setWidth((newPoint.getX() - selectionStartPoint.getX()));
		} else {
			this.setX(newPoint.getX());
			this.setWidth(selectionStartPoint.getX() - newPoint.getX());
		}

		if (newPoint.getY() > selectionStartPoint.getY()) {
			this.setHeight((newPoint.getY() - selectionStartPoint.getY()));
		} else {
			this.setY(newPoint.getY());
			this.setHeight(selectionStartPoint.getY() - newPoint.getY());
		}
	}

}
