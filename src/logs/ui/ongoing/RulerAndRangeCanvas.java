package logs.ui.ongoing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * This is a simple zoom + range selection </br>
 * Interactions : </br>
 * horizontal drag --> translate </br>
 * Vertical drag --> zoom</br>
 *
 * @author jeremiegarcia
 *
 */
public class RulerAndRangeCanvas extends Canvas {

	long min = 0;
	long max = 1000;

	private SimpleDoubleProperty visibleMinT = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty visibleMaxT = new SimpleDoubleProperty(1000);

	public RulerAndRangeCanvas() {
		super();
		this.prefHeight(40);
		selectAll();
		addEventsHandlers();
	}

	// Interaction state machine
	private boolean drag = false;
	private double prevx, prevy;

	private void addEventsHandlers() {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub

			}
		});

		this.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getX() > getPosFromDate(visibleMinT.get()) && event.getX() < getPosFromDate(visibleMaxT.get())) {
					drag = true;
					prevx = event.getX();
					prevy = event.getY();
				}
			}
		});

		this.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (drag) {
					double diffx = event.getX() - prevx;
					double diffy = event.getY() - prevy;
					long range = max - min;

					if (diffx > 3 || diffx < -3) {
						// translate the selection with temporal diff
						long diff = (diffx * range) / getWidth();
						updateSelection(selMin + diff, selMax + diff);
						prevx = event.getX();
					} else if (diffy > 3 || diffy < -3) {
						// scale the selection

						// A get the central position
						long sel_range = selMax - selMin;
						long center = selMin + sel_range / 2;

						double scale = 1;
						if (diffy > 0) {
							scale = 1.1;
						} else {
							scale = 0.9;
						}

						long new_range = (long) (sel_range * scale);

						long scaledMin = center - new_range / 2;
						long scaledMax = center + new_range / 2;
						updateSelection(scaledMin, scaledMax);
						prevy = event.getY();
					}
				}

			}
		});

		this.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				drag = false;

			}
		});

	}

	public void setRange(long start, long end) {
		this.min = start;
		this.max = end;
		selectAll();
	}

	public void selectAll() {
		updateSelection(min, max);
	}

	private void updateSelection(long newMin, long newMax) {
		if (newMin < newMax && (getPosFromDate(newMax) - getPosFromDate(newMin)) > 10) {
			selMin = newMin;
			selMax = newMax;
			if (selMin < min) {
				selMin = min;
			}
			if (selMax > max) {
				selMax = max;
			}
		}
	}

	// drawing
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw mini view of the data if possible
		this.drawPreview(g2);

		// draw min and max lines to show the range
		int xmin = getPosFromDate(selMin);
		int xmax = getPosFromDate(selMax);

		g2.setColor(new Color(120, 250, 120, 200));
		g2.fillRect(xmin, 0, xmax - xmin, getHeight());
	}

	// utils
	public int getPosFromDate(long t) {
		long range = max - min;
		if (range == 0) {
			return 0;
		} else {
			return (int) ((t - min) * getWidth() / range);
		}
	}

	public long getDateFromPos(int x) {
		long range = max - min;
		return (x / getWidth()) * range + min;
	}
}
