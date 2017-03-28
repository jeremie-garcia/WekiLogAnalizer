package logs.ui.oldswing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JPanel;

import logs.model.DiscreteLogEvent;
import logs.model.LogEventsLoader;

/**
 * This is a simple zoom + range selection </br>
 * Interactions : </br>
 * horizontal drag --> translate </br>
 * Vertical drag --> zomm</br>
 *
 * It fires a range selected event when changes happen
 *
 * @author jeremiegarcia
 *
 */
public class RangeSelector extends JPanel implements MouseListener, MouseMotionListener {

	private ArrayList<RangeListener> listeners = null;
	// Show all the duration of the timeline
	long min = 0;
	long max = 1000;

	HashMap<String, ArrayList<DiscreteLogEvent>> eventsMap = null;

	// used to place selectors
	long selMin, selMax;

	public RangeSelector() {
		super();
		this.setPreferredSize(new Dimension(100, 40));
		setBackground(Color.lightGray);
		selectAll();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// Listeners related code
	public void addRangeListener(RangeListener listener) {
		if (listeners == null)
			listeners = new ArrayList<RangeListener>();

		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeRangeListener(RangeListener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}

	public void removeAllRangeListeners() {
		if (listeners != null)
			listeners.clear();
	}

	private void fireRangeChanged() {
		if (this.listeners != null)
			for (RangeListener l : this.listeners) {
				l.updateRange(selMin, selMax);
			}
	}

	// behavior
	public void setData(HashMap<String, ArrayList<DiscreteLogEvent>> eventsMap) {
		this.eventsMap = eventsMap;
		long refTime = LogEventsLoader.getFirstTimeFromMap(this.eventsMap) - 5000;
		long endTime = LogEventsLoader.getLastTimeFromMap(this.eventsMap) + 8000;
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
