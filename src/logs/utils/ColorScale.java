package logs.utils;

import javafx.scene.paint.Color;

public class ColorScale {

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

}
