package logs.utils;

import java.util.concurrent.TimeUnit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

/**
 * JavaFx utilities to generate colors, work with scroll pane, get Time as
 * formatted string...
 *
 * @author jeremiegarcia
 *
 */
public class JavaFXUtils {

	private static float goldenRatioConjugate = 0.618033988749895f;

	private static float saturation = 0.5f;
	private static float brightness = 1f;
	private static float alpha = 0.6f;

	public static Color getColorWithGoldenRationByIndex(int index) {
		float hue = index * goldenRatioConjugate;
		hue %= 1;
		hue *= 360;
		Color col = Color.hsb(hue, saturation, brightness, alpha);
		return col;
	}

	public static Color getEmphasizedColor(Color col) {
		return col.deriveColor(0, 1, 1.5, 2);
	}

	public static Color getColorForGrade(double grade) {
		Color col;
		if (grade == -1) {
			col = Color.WHITE;
		} else {
			col = Color.hsb((grade * 0.3) * 360, saturation, brightness);
		}
		col = new Color((float) col.getRed() / 255, (float) col.getGreen() / 255, (float) col.getBlue() / 255, alpha);
		return col;

	}

	public static Color applyAlpha(Color c, float alpha) {
		return new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, alpha);
	}

	public static double getHorizontalOffset(ScrollPane scrollPane) {
		double hmin = scrollPane.getHmin();
		double hmax = scrollPane.getHmax();
		double hvalue = scrollPane.getHvalue();
		double contentWidth = scrollPane.getContent().getLayoutBounds().getWidth();
		double viewportWidth = scrollPane.getViewportBounds().getWidth();

		double hoffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
		return hoffset;
	}

	public static double getVerticalOffset(ScrollPane scrollPane) {
		double vmin = scrollPane.getVmin();
		double vmax = scrollPane.getVmax();
		double vvalue = scrollPane.getVvalue();
		double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
		double viewportHeight = scrollPane.getViewportBounds().getHeight();

		double voffset = Math.max(0, contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);
		return voffset;
	}

	public static double getScrollHValueFromPercentage(ScrollPane scrollPane, double percentage) {
		double contentWidth = scrollPane.getContent().getLayoutBounds().getWidth();
		double viewportWidth = scrollPane.getViewportBounds().getWidth();

		double y = (scrollPane.getContent().getBoundsInParent().getMinX()
				+ scrollPane.getContent().getBoundsInParent().getMaxX()) * percentage;

		double hvalue = scrollPane.getHmax() * ((y - percentage * viewportWidth) / (contentWidth - viewportWidth));
		return hvalue;
	}

	public static double getScrollVValueFromPercentage(ScrollPane scrollPane, double percentage) {
		double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
		double viewportHeight = scrollPane.getViewportBounds().getHeight();

		double y = (scrollPane.getContent().getBoundsInParent().getHeight()) * percentage;

		double vvalue = scrollPane.getVmax() * ((y - percentage * viewportHeight) / (contentHeight - viewportHeight));
		return vvalue;
	}

	/**
	 * returns the inverse scaling transformation to fit node that need to
	 * preserve their aspect ratios
	 *
	 * @return
	 */
	public static DoubleBinding getReversedScaleXBinding(DoubleProperty doubleProperty) {
		return new SimpleDoubleProperty(1).divide(doubleProperty);
	}

	public static String getTimeAsFormattedString(long dateInMillis) {
		String format = String.format("%d hour, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(dateInMillis),
				TimeUnit.MILLISECONDS.toMinutes(dateInMillis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(dateInMillis)),
				TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dateInMillis)));
		return format;
	}

}
