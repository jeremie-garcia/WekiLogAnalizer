package WekiLogs.logs.gui.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import WekiLogs.logs.TrainingDataSet;
import WekiLogs.logs.LogEvent;
import WekiLogs.logs.ModelProcessor;
import WekiLogs.utils.ColorScale;
import WekiLogs.utils.Utils;

/**
 * This class is the main container for comparison tool of a single exercise
 * input strategies Needs: way to evaluate the classifier for the exercise for
 * each trial.
 *
 * @author jeremiegarcia
 *
 */
public class TrainingViz extends JFrame {

	String type;
	ArrayList<LogEvent> events;

	ArrayList<TrainingDataSet> dataSets;

	public int[] globalInputMinValues;
	public int[] globalInputMaxValues;
	public int[] globalInputRangeValues;
	HashMap<Integer, Color> colorMap;

	public TrainingViz(String type, ArrayList<LogEvent> events, double grade) {
		this.type = type;
		this.events = events;
		this.dataSets = new ArrayList<TrainingDataSet>();

		String dir = null;
		for (LogEvent logEvent : this.events) {
			String fileName = Utils.getFileNameForModelFromEvent(logEvent);
			if (dir == null) {
				dir = new File(fileName).getParent();
			}
			TrainingDataSet data = ModelProcessor.extractDataSetFromKNNModelFile(fileName);
			data.setTimeStamp(logEvent.getTimeStamp());
			data.grade = grade;
			// global grade for now but should be individualized
			this.dataSets.add(data);
		}

		this.updateMinMaxValues();
		this.updateColorScale();

		this.setLocation(0, 800);
		this.setTitle("Training for " + dir);
		this.setSize(new Dimension(1200, 100));
		this.setLayout(new BorderLayout());
		this.buildMenuBar();
		this.add(buildCentralPanel(this.dataSets), BorderLayout.CENTER);
		this.setVisible(true);
	}

	public void updateScreenPosition(int x, int y) {
		this.setLocation(x, y);
	}

	private void updateColorScale() {
		this.colorMap = new HashMap<Integer, Color>();
		for (TrainingDataSet set : this.dataSets) {
			for (int out : set.possibleOutputs) {
				if (!colorMap.containsKey(out)) {
					colorMap.put(out, ColorScale.getColorWithGoldenRationByIndex(out));
				}
			}
		}
	}

	private void updateMinMaxValues() {
		if (dataSets.size() > 0) {
			int size = this.dataSets.get(0).numInputs;

			// initialize to first element
			this.globalInputMinValues = this.dataSets.get(0).inputMinValues;
			this.globalInputMaxValues = this.dataSets.get(0).inputMaxValues;

			// then iterate over all dataSets to pudate the min max values

			for (TrainingDataSet set : this.dataSets) {
				for (int k = 0; k < size; k++) {
					if (set.inputMinValues[k] < this.globalInputMinValues[k]) {
						this.globalInputMinValues[k] = set.inputMinValues[k];
					}

					if (set.inputMaxValues[k] > this.globalInputMaxValues[k]) {
						this.globalInputMaxValues[k] = set.inputMaxValues[k];
					}
				}
			}

			globalInputRangeValues = new int[size];
			for (int k = 0; k < size; k++) {
				globalInputRangeValues[k] = globalInputMaxValues[k] - globalInputMinValues[k];
			}
		}
	}

	private JPanel buildCentralPanel(ArrayList<TrainingDataSet> dataSets) {
		JPanel centralPanel = new JPanel();
		centralPanel.setBackground(Color.white);
		// use a box layout first
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.LINE_AXIS));

		for (TrainingDataSet data : dataSets) {
			InputVisKNN vis = new InputVisKNN(data, this);
			centralPanel.add(vis);
		}
		return centralPanel;

	}

	private void buildMenuBar() {

	}

}
