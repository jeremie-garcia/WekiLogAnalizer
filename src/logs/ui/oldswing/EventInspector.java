package logs.ui.oldswing;

import java.awt.Dimension;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import logs.model.DiscreteLogEvent;
import wekilogs.trainingdata.ui.SimpleTrainingVizKNN;

public class EventInspector extends JPanel {

	public static EventInspector singleton = null;

	private JTextField dateTxt = new JTextField();
	private JTextField typeTxt = new JTextField();
	private JTextArea argsTxt = new JTextArea();

	private SimpleTrainingVizKNN inputVis;

	private DiscreteLogEvent lastEvent = null;

	private EventInspector() {
		super();
		this.setPreferredSize(new Dimension(200, 0));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Inspector");
		this.add(title);

		JLabel date = new JLabel("Date");
		this.add(date);
		dateTxt.setMaximumSize(new Dimension(200, 40));
		this.add(dateTxt);

		JLabel type = new JLabel("Type");
		this.add(type);
		typeTxt.setMaximumSize(new Dimension(200, 40));
		this.add(typeTxt);

		JLabel args = new JLabel("Args");
		this.add(args);
		argsTxt.setRows(5);
		// argsTxt.setPreferredSize(new Dimension(100, 200));
		argsTxt.setLineWrap(true);
		argsTxt.setWrapStyleWord(true);
		argsTxt.setMaximumSize(new Dimension(200, 100));
		this.add(argsTxt);

		this.add(Box.createGlue());

	}

	public static EventInspector getInstance() {
		if (singleton == null)
			singleton = new EventInspector();
		return singleton;
	}

	public void update(DiscreteLogEvent event) {

		if (event != lastEvent) {

			this.dateTxt.setText(new Date(event.getTimeStamp()).toString());
			this.typeTxt.setText(event.getLabel());
			this.argsTxt.setText(event.getArgs().toString());

			if (inputVis != null) {
				this.remove(inputVis);
				this.validate();
			}

			if (event.getLabel().toLowerCase().contains("model_num")) {
				// create visualisation of input data
				inputVis = new SimpleTrainingVizKNN(event);
				this.add(inputVis);
				this.validate();
			}
			lastEvent = event;
		}

	}
}
