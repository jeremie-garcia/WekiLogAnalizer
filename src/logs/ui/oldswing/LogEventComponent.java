package logs.ui.oldswing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.border.StrokeBorder;

import logs.config.Configuration;
import logs.model.DiscreteLogEvent;

public class LogEventComponent extends JComponent implements MouseListener {

	DiscreteLogEvent event;
	SingleEventTypePanel parent;
	long timeStamp = 0;
	boolean isHighlight = false;

	public LogEventComponent(DiscreteLogEvent event, SingleEventTypePanel parent) {
		super();
		this.parent = parent;

		this.event = event;
		this.timeStamp = event.getTimeStamp();
		this.setBounds(0, parent.getHeight() / 2 - Configuration.ITEM_VERTICAL_EXTENT / 2,
				Configuration.ITEM_HORIZONTAL_EXTENT, Configuration.ITEM_VERTICAL_EXTENT);
		this.addMouseListener(this);
		this.updatePos();
	}

	public void updatePos() {
		int posx = parent.ruler.getPosFromDate(timeStamp);
		this.setBounds(posx, parent.getHeight() / 2 - Configuration.ITEM_VERTICAL_EXTENT / 2,
				Configuration.ITEM_HORIZONTAL_EXTENT, Configuration.ITEM_VERTICAL_EXTENT);
	}

	public DiscreteLogEvent getLogEvent() {
		return this.event;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// g2.setColor(Configuration.ITEM_COLOR);
		g2.fillRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, Configuration.ITEM_X_ARC,
				Configuration.ITEM_Y_ARC);

		Stroke s = g2.getStroke();
		if (isHighlight) {
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(2));
		} else {
			g2.setColor(Color.black);
		}

		g2.drawRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, Configuration.ITEM_X_ARC,
				Configuration.ITEM_Y_ARC);
		g2.setStroke(s);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.parent.parent.highlight(this);
		EventInspector.getInstance().update(event);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public long getTimeStamps() {
		return this.timeStamp;
	}

}
