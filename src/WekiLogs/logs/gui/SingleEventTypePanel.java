package WekiLogs.logs.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import WekiLogs.logs.LogEvent;
import WekiLogs.utils.ColorScale;

/**
 * This class is a panel that draws Components representing LogEvents according
 * to a global timeline given by the ruler.
 *
 * @author jgarcia
 *
 */
public class SingleEventTypePanel extends JPanel implements RangeListener, ComponentListener {

	String type;
	Date start, end;
	Ruler ruler;
	public boolean highlight = false;
	Random r = new Random();
	Color col = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
	LogVisualizer parent;

	ArrayList<LogEventComponent> childrens = new ArrayList<LogEventComponent>();

	public SingleEventTypePanel(String type, ArrayList<LogEvent> events, Ruler ruler, LogVisualizer logVisualizer,
			int index) {
		super();
		this.parent = logVisualizer;
		this.setLayout(null);
		this.setOpaque(false);
		this.type = type.toLowerCase();
		this.ruler = ruler;
		setPreferredSize(new Dimension(800, Configuration.TIMELINE_HEIGHT));
		setBorder(new LineBorder(Color.DARK_GRAY, 1));
		this.addComponentListener(this);
		this.col = ColorScale.getColorWithGoldenRationByIndex(index);

		// add childrens
		for (LogEvent e : events) {
			LogEventComponent comp = new LogEventComponent(e, this);
			// This is to be able to retrieve mouse events.
			this.add(comp);
			childrens.add(comp);
		}

		updateComponents();
	}

	public void updateComponents() {
		for (LogEventComponent comp : this.childrens) {
			comp.updatePos();
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(Configuration.font);

		// draw horizontal line
		g2.drawLine(0, ImageObserver.HEIGHT / 2, ImageObserver.WIDTH, ImageObserver.HEIGHT / 2);
		g2.drawString(this.type, 10, getHeight() / 2);

		// draw LogEvents
		for (LogEventComponent comp : childrens) {
			if (ruler.isVisibleTime(comp.getTimeStamps())) {
				g2.setColor(this.col);
				comp.paint(g2);
			}
		}
	}

	@Override
	public void updateRange(double min, double max) {
		updateComponents();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateComponents();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

}
