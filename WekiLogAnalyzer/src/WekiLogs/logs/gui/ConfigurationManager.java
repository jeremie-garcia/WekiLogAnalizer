package WekiLogs.logs.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigurationManager extends JPanel {

	private LogVisualizer visualizer;

	public ConfigurationManager(LogVisualizer visualizer) {
		super();
		this.visualizer = visualizer;

		this.setPreferredSize(new Dimension(200, 0));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Configuration Manager");
		this.add(title);

		JCheckBox checkBox = new JCheckBox("Fold/unfold");
		checkBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				boolean sel = box.isSelected();
				visualizer.updatePanelsLayoutFolding(sel);

			}
		});
		this.add(checkBox);
		this.add(Box.createGlue());

	}

	public void update(Configuration config) {

	}
}
