package wekilogs.trainingdata.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import logs.utils.ColorScale;
import wekilogs.trainingdata.model.TrainingData;
import wekilogs.trainingdata.model.TrainingDataSet;

public class TrainingVizKNN extends JPanel {

	TrainingVizContainerFrame parent;
	TrainingDataSet data;

	private int offset = 20;

	private int diameter = 5;

	public TrainingVizKNN(TrainingDataSet data, TrainingVizContainerFrame parent) {
		super();
		this.parent = parent;
		this.data = data;

		// draw colored background depending on the grade
		Color col = ColorScale.getColorForGrade(data.grade);
		this.setBackground(col);

		this.setPreferredSize(new Dimension(50, 50));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawScale(g2);
		drawInputData(g2);
	}

	private void drawInputData(Graphics2D g2) {
		int index = 1;
		for (TrainingData d : data.examples) {
			Color c = ColorScale.getColorWithGoldenRationByIndex(d.output);
			g2.setColor(c);
			double[] coords = getScreenPosFromCoords(d.inputs.get(0), d.inputs.get(1));
			g2.fillOval((int) coords[0] - diameter / 2, (int) coords[1] - diameter / 2, diameter, diameter);
			g2.setColor(Color.black);
			g2.drawOval((int) coords[0] - diameter / 2, (int) coords[1] - diameter / 2, diameter, diameter);
			g2.setFont(g2.getFont().deriveFont(9f));
			g2.drawString(index + "", (int) coords[0] + 2, (int) coords[1]);
			index++;
		}
	}

	private void drawScale(Graphics2D g2) {

		// draw axis and grid
		int xrange = parent.globalInputRangeValues[0];
		int xmin = parent.globalInputMinValues[0];
		int xmax = parent.globalInputMaxValues[0];

		int yrange = parent.globalInputRangeValues[1];
		int ymin = parent.globalInputMinValues[1];
		int ymax = parent.globalInputMaxValues[1];

		int step = xrange / 10;

		for (int pos = xmin; pos <= xmax; pos = pos + step) {
			double posPix = getScreenPosXFromCoords(pos);
			g2.drawLine((int) posPix, offset / 2, (int) posPix, this.getHeight() - offset / 2);
		}

		int step2 = yrange / 10;

		for (int pos2 = ymin; pos2 <= ymax; pos2 = pos2 + step2) {
			double posPix = getScreenPosYFromCoords(pos2);
			g2.drawLine(offset / 2, (int) posPix, this.getWidth() - offset / 2, (int) posPix);
		}
	}

	public double[] getScreenPosFromCoords(int x, int y) {
		double[] posPix = new double[2];
		posPix[0] = getScreenPosXFromCoords(x);
		posPix[1] = getScreenPosYFromCoords(y);
		return posPix;
	}

	public double getScreenPosXFromCoords(int x) {
		double posPix = 0;

		int w = this.getWidth() - offset;
		int xrange = parent.globalInputRangeValues[0];
		int xmin = parent.globalInputMinValues[0];

		posPix = (double) (x - xmin) / xrange;
		posPix = posPix * w + offset / 2;
		return posPix;
	}

	public double getScreenDistanceFromXDistance(int dist) {
		double posPix = 0;
		int w = this.getWidth() - offset;
		int xrange = parent.globalInputRangeValues[0];
		posPix = (double) (dist) / xrange;
		posPix = posPix * w + offset / 2;
		return posPix;
	}

	public double getScreenDistanceFromYDistance(int dist) {
		double posPix = 0;
		int h = this.getHeight() - offset;
		int yrange = parent.globalInputRangeValues[1];
		posPix = (double) (dist) / yrange;
		posPix = posPix * h + offset / 2;
		return posPix;
	}

	public double getScreenPosYFromCoords(int y) {
		double posPix = 0;
		int h = this.getHeight() - offset;
		int yrange = parent.globalInputRangeValues[1];
		int ymin = parent.globalInputMinValues[1];
		posPix = (double) (y - ymin) / yrange;
		posPix = posPix * h + offset / 2;
		return posPix;
	}
}
