package logs.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.LongProperty;
import javafx.scene.layout.Pane;
import logs.model.LogEvent;
import wekilogs.WekiLogEventsLoader;

/**
 * This is a simple zoom + range selection </br>
 * Interactions : </br>
 * horizontal drag --> translate </br>
 * Vertical drag --> zomm</br>
 *
 * @author jeremiegarcia
 */
public class RangeSelector extends Pane implements MouseListener, MouseMotionListener {

	long min = 0;
	long max = 0;

	HashMap<String, ArrayList<LogEvent>> eventsMap = null;

	// used to place selectors
	LongProperty selMinProperty, selMaxProperty;

	public RangeSelector() {
		super();
		this.setMinHeight(20);
		selectAll();
		this.getOnMouseClicked();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// behavior
	public void setData(HashMap<String, ArrayList<LogEvent>> eventsMap) {
		this.eventsMap = eventsMap;
		long refTime = WekiLogEventsLoader.getFirstTimeFromMap(this.eventsMap) - 5000;
		long endTime = WekiLogEventsLoader.getLastTimeFromMap(this.eventsMap) + 8000;
		this.setRange(refTime, endTime);
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
			this.fireRangeChanged();
			repaint();
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

	private void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub

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

	public long getSelMin() {
		return selMin;
	}

	public long getSelMax() {
		return selMax;
	}

	public long getSelRange() {
		return selMax - selMin;
	}

	// Interaction state machine
	private boolean drag = false;
	private int prevx, prevy;

	@Override
	public void mouseDragged(MouseEvent e) {
		if (drag) {
			int diffx = (int) (e.getX() - prevx);
			int diffy = (int) (e.getY() - prevy);
			long range = max - min;

			if (diffx > 3 || diffx < -3) {
				// translate the selection with temporal diff
				long diff = (diffx * range) / getWidth();
				updateSelection(selMin + diff, selMax + diff);
				prevx = e.getX();
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
				prevy = e.getY();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			selectAll();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getX() > getPosFromDate(selMin) && e.getX() < getPosFromDate(selMax)) {
			drag = true;
			prevx = e.getX();
			prevy = e.getY();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		drag = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
