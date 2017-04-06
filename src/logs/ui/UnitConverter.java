package logs.ui;

import java.util.concurrent.TimeUnit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * This class provides utilities methods to convert from time to scene, scene to
 * time and percentage to scene
 *
 * @author jeremiegarcia
 *
 */
public class UnitConverter {

	private long beginTimeMillis = 0;
	private long endTimeMillis = 1000;
	private long timeRangeMillis = 1000;

	public UnitConverter(long begin, long end) {
		this.beginTimeMillis = (begin < end) ? begin : end;
		this.endTimeMillis = (end > begin) ? end : begin;
		this.timeRangeMillis = endTimeMillis - beginTimeMillis;
	}

	public double getPosInSceneFromTime(long time) {
		return time - beginTimeMillis;
	}

	public double getTimeFromPosInScene(double posX) {
		return posX + beginTimeMillis;
	}

	public long getTimeFromPercentage(double percentage) {
		return (long) (getDurationInMillisFromPercentage(percentage) + beginTimeMillis);
	}

	public double getPosInSceneFromPercentage(double percentage) {
		return percentage * timeRangeMillis;
	}

	public double getDurationInMillisFromPercentage(double percentage) {
		return percentage * timeRangeMillis;
	}

	public String getTimeAsFormattedString(long dateInMillis) {
		String format = String.format("%d hour, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(dateInMillis),
				TimeUnit.MILLISECONDS.toMinutes(dateInMillis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dateInMillis)),
				TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dateInMillis)));
		return format;
	}
}
