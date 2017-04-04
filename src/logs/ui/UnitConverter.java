package logs.ui;

import java.util.concurrent.TimeUnit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;

public class UnitConverter {

	private static SimpleDoubleProperty scaleX = new SimpleDoubleProperty(1);
	private static SimpleDoubleProperty translateX = new SimpleDoubleProperty(0);

	private static long beginTime = 0;
	private static long endTime = 1000;

	private static long timeRange = 1000;

	private static double reductionFactor = 1000;

	public static double getPosInSceneFromTime(long timeStamp) {
		double val = timeStamp - beginTime;
		return val / reductionFactor;
	}

	public static long getTimeFromPosInScene(int posInPix) {
		long time = (long) (posInPix * reductionFactor);
		return time + beginTime;
	}

	public static String getTimeAsFormattedString(long dateInMillis) {
		String format = String.format("%d hour, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(dateInMillis),
				TimeUnit.MILLISECONDS.toMinutes(dateInMillis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dateInMillis)),
				TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dateInMillis)));
		return format;
	}

	public static long getTimeRange() {
		return timeRange;
	}

	public static void updateTimeBounds(long begin, long end) {
		beginTime = begin;
		endTime = end;
		timeRange = endTime - beginTime;
	}

	public static SimpleDoubleProperty getScaleXProperty() {
		return scaleX;
	}

	public static SimpleDoubleProperty getTranslateXProperty() {
		return translateX;
	}

	/**
	 * returns the inverse scaling transformation to fit node that need to
	 * preserve their aspect ratios
	 *
	 * @return
	 */
	public static DoubleBinding getReversedScaleXBinding() {
		return new SimpleDoubleProperty(1).divide(UnitConverter.getScaleXProperty());
	}

}
