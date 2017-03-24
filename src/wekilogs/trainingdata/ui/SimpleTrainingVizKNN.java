package wekilogs.trainingdata.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import logs.model.DiscreteLogEvent;
import wekilogs.model.ModelProcessor;
import wekilogs.trainingdata.model.TrainingDataSet;
import wekilogs.utils.LogFileUtils;

public class SimpleTrainingVizKNN extends JPanel {

	DiscreteLogEvent evt;
	double xmax = Double.MIN_VALUE;
	double xmin = Double.MAX_VALUE;
	double xrange = xmax - xmin;
	double ymax = Double.MIN_VALUE;
	double ymin = Double.MAX_VALUE;
	double yrange = ymax - xmin;

	ArrayList<Point2D> points;

	public SimpleTrainingVizKNN(DiscreteLogEvent event) {
		this.evt = event;
		this.setBackground(Color.white);
		String fileName = LogFileUtils.getFileNameForModelFromEvent(event);
		points = ModelProcessor.extractPointsFromModel(fileName);
		TrainingDataSet data = ModelProcessor.extractDataSetFromKNNModelFile(fileName);
		System.out.println(data.examples.size());
		updateRangeForPoints(points);
	}

	private void updateRangeForPoints(ArrayList<Point2D> points) {
		for (Point2D p : points) {
			if (p.getX() < xmin) {
				xmin = p.getX();
			} else if (p.getX() > xmax) {
				xmax = p.getX();
			}

			if (p.getY() < ymin) {
				ymin = p.getY();
			} else if (p.getY() > ymax) {
				ymax = p.getY();
			}
		}

		this.xrange = xmax - xmin;
		this.yrange = ymax - ymin;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		for (Point2D p : this.points) {
			Point2D pPixels = getScreenPosFromCoords(p);
			g2.setColor(Color.black);
			g2.drawOval((int) pPixels.getX(), (int) pPixels.getY(), 5, 5);
		}

	}

	public Point2D getScreenPosFromCoords(Point2D p) {
		int w = this.getWidth() - 20;
		int h = this.getHeight() - 20;

		double x = (p.getX() - this.xmin) / xrange;
		x = x * w + 10;
		double y = (p.getY() - this.ymin) / yrange;
		y = y * h + 10;
		return new Point2D.Double(x, y);
	}

}
