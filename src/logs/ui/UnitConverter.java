package logs.ui;

import javafx.beans.property.SimpleDoubleProperty;

public class UnitConverter {

	private static SimpleDoubleProperty minVisibleT = new SimpleDoubleProperty(0);
	private static SimpleDoubleProperty maxVisibleT = new SimpleDoubleProperty(1000);
	private static long beginTime = 0;
	private static long endTime = 1000;

	private static long timeRange = 1000;

	private static double reductionFactor = 1000;

	public static double getPosFromTime(long timeStamp) {
		double val = timeStamp - beginTime;
		// TODO Auto-generated method stub
		return val / reductionFactor;
	}

	public static long getTimeRange() {
		return timeRange;
	}

	public static void updateTimeBounds(long begin, long end) {
		beginTime = begin;
		endTime = end;
		timeRange = endTime - beginTime;
		minVisibleT.set(beginTime);
		maxVisibleT.set(endTime);
	}

}
