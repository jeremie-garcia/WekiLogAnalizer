package WekiLogs.logs.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class Ruler extends JPanel implements RangeListener {

	private RangeSelector rangeSelector;

	public Ruler(RangeSelector selector) {
		super();
		this.rangeSelector = selector;
		this.rangeSelector.addRangeListener(this);
		this.setPreferredSize(new Dimension(800, 40));
	}

	public int getPosFromDate(long d) {
		long range = rangeSelector.getSelRange();
		if (range == 0) {
			return 0;
		} else {
			return (int) ((d - rangeSelector.getSelMin()) * getWidth() / range);
		}
	}

	public long getDateFromPos(int x) {
		double range = rangeSelector.getSelRange();
		double x_d = x;
		double value = (x_d / getWidth()) * range;
		return ((long) value) + rangeSelector.getSelMin();

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// create x and y axes
		g2.drawLine(0, getHeight() / 4, getWidth(), getHeight() / 4);

		// Create hatch marks

		int y0 = getHeight() / 4 - 5;
		int y1 = y0 + 10;

		int sep = getWidth() / 5;
		for (int i = 0; i <= getWidth(); i = i + sep) {
			g2.setFont(Configuration.font);
			g2.drawLine(i, y0, i, y1);
			long millis = getDateFromPos(i);
			long ref = rangeSelector.min;
			millis = millis - ref;
			String format = String.format("%d hour, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
					TimeUnit.MILLISECONDS.toSeconds(millis)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
			g2.drawString(format, i, y1 + 12);
		}
	}

	@Override
	public void updateRange(double min, double max) {
		repaint();
	}

	public boolean isVisibleTime(long timeStamps) {
		return timeStamps >= rangeSelector.selMin && timeStamps <= rangeSelector.selMax;
	}

}
