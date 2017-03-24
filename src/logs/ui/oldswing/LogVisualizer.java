package logs.ui.oldswing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;

import logs.config.Configuration;
import logs.model.DiscreteLogEvent;

/**
 * Main container for individual panel representing each event type. </br>
 * It also contains a ruler and a range selector vertically aligned
 *
 * @author jeremiegarcia
 *
 */
public class LogVisualizer extends JPanel implements ComponentListener {

	private long duration;

	Ruler ruler;
	RangeSelector rangeSelector;
	JPanel list = new JPanel();
	JScrollPane contentPane = new JScrollPane(list);
	boolean foldPanels = false;

	public LogVisualizer() {
		super();
		setLayout(new BorderLayout());
		rangeSelector = new RangeSelector();
		ruler = new Ruler(rangeSelector);

		updatePanelsLayoutFolding(Configuration.ISFOLD);

		JPanel scaling = new JPanel();
		scaling.setBackground(Color.green);
		scaling.setLayout(new BoxLayout(scaling, BoxLayout.Y_AXIS));

		scaling.add(ruler);
		scaling.add(rangeSelector);

		add(contentPane, BorderLayout.CENTER);
		add(scaling, BorderLayout.SOUTH);
	}

	public void buildEventsPanelsFromMap(HashMap<String, ArrayList<DiscreteLogEvent>> eventsMap) {

		rangeSelector.setData(eventsMap);

		int index = 0;
		for (String eventType : eventsMap.keySet()) {
			SingleEventTypePanel panel = new SingleEventTypePanel(eventType, eventsMap.get(eventType), this.ruler, this,
					index);
			rangeSelector.addRangeListener(panel);
			list.add(panel);
			index++;
		}
		list.setPreferredSize(new Dimension(0, Configuration.TIMELINE_HEIGHT * list.getComponents().length));
		contentPane.validate();

	}

	public void updatePanelsLayoutFolding(boolean isFold) {
		if (isFold) {
			list.setLayout(new OverlayLayout(list));
		} else {
			list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
		}
		contentPane.validate();
		contentPane.repaint();
	}

	public void resetRuler() {
		rangeSelector.selectAll();
	}

	public Ruler getRuler() {
		return this.ruler;
	}

	public void highlight(LogEventComponent logEventComponent) {

		for (Component panel : this.list.getComponents()) {
			if (panel instanceof SingleEventTypePanel) {
				for (LogEventComponent eventComp : ((SingleEventTypePanel) panel).childrens) {
					eventComp.isHighlight = (eventComp == logEventComponent);
				}
				panel.repaint();
			}
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		repaint();
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
