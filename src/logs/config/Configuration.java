package logs.config;

import java.awt.Color;
import java.awt.Font;

import logs.utils.ColorScale;

public class Configuration {

	////////////// FILES AND FOLDERS PREFERENCES

	public static final String TMP_DIR = "./tmp";

	////////////// GRAPHICAL PREFERENCES
	public static int TIMELINE_HEIGHT = 20;
	public static Font font = new Font("Helvetica", Font.PLAIN, 10);

	public static final boolean ISFOLD = false;

	// Dimensions
	public static int ITEM_VERTICAL_EXTENT = TIMELINE_HEIGHT - TIMELINE_HEIGHT / 10;
	public static int ITEM_HORIZONTAL_EXTENT = 5;
	public static int ITEM_X_ARC = 5;
	public static int ITEM_Y_ARC = 5;

	// Colors
	public static Color ITEM_COLOR = ColorScale.applyAlpha(Color.getHSBColor(0.05f, 0.6f, 0.9f), 0.5f);
	public static Color ITEM_HIGHLIGHT_COLOR = ColorScale.applyAlpha(Color.getHSBColor(0.1f, 0.8f, 0.9f), 0.5f);
	public static Color ITEM_BORDER_COLOR = ITEM_COLOR.darker();
}
