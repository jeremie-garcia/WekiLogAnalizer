package logs.ui;

import java.util.concurrent.TimeUnit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;

public class UnitConverter {

	private static long beginTimeMillis = 0;
	private static long endTimeMillis = 1000;
	private static long timeRangeMillis = 1000;
	private static double reductionFactor = 10000;

	public static void updateTimeBounds(long begin, long end) {
		beginTimeMillis = begin;
		endTimeMillis = end;
		timeRangeMillis = endTimeMillis - beginTimeMillis;
	}

	public static double getPosInSceneFromTime(long timeStamp) {
		double val = timeStamp - beginTimeMillis;
		return val / reductionFactor;
	}

	public static long getTimeFromPosInScene(int posInScene) {
		long time = (long) (posInScene * reductionFactor);
		return time + beginTimeMillis;
	}

	public static long getDurationFromLengthInScene(double dInPix) {
		return (long) (dInPix * reductionFactor);
	}

	public static long getLengthInSceneFromDuration(double d) {
		return (long) (d / reductionFactor);
	}

	public static String getTimeAsFormattedString(long dateInMillis) {
		String format = String.format("%d hour, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(dateInMillis),
				TimeUnit.MILLISECONDS.toMinutes(dateInMillis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dateInMillis)),
				TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dateInMillis)));
		return format;
	}

	public static double getDurationInMillisFromPercentage(double percentage) {
		return percentage * timeRangeMillis;
	}

	public static double getLengthInSceneFromPercentage(double percentage) {
		return getLengthInSceneFromDuration(getDurationInMillisFromPercentage(percentage));
	}

	public static long getBeginTimeMillis() {
		return beginTimeMillis;
	}

	public static long getEndTimeMillis() {
		return endTimeMillis;
	}

	public static long getTimeRangeMillis() {
		return timeRangeMillis;
	}

	public static double getReductionFactor() {
		return reductionFactor;
	}

	public static double getTotalLengthInScene() {
		return getLengthInSceneFromDuration(timeRangeMillis);
	}
}
