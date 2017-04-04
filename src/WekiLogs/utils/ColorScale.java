package WekiLogs.utils;

import java.awt.Color;

public class ColorScale {

	private static float goldenRatioConjugate = 0.618033988749895f;

	private static float saturation = 0.8f;
	private static float brightness = 1f;
	private static float alpha = 0.4f;

	public static Color getColorWithGoldenRationByIndex(int index) {
		float hue = index * goldenRatioConjugate;
		hue %= 1;
		Color col = new Color(Color.HSBtoRGB(hue, saturation, brightness));
		// col = new Color(col.getRed() / 255, col.getGreen() / 255,
		// col.getBlue() / 255, alpha);
		return col;
	}

	public static Color getColorForGrade(double grade) {
		Color col;
		if (grade == -1) {
			col = Color.white;
		} else {
			col = new Color(Color.HSBtoRGB((float) (grade * 0.3), saturation, brightness));
		}
		col = new Color((float) col.getRed() / 255, (float) col.getGreen() / 255, (float) col.getBlue() / 255, alpha);
		return col;

	}

}
