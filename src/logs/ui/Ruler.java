package logs.ui;

import javafx.scene.layout.Pane;

public class Ruler extends Pane {

	private RangeSelector rangeSelector;

	public Ruler(RangeSelector selector) {
		super();
		this.rangeSelector = selector;
		this.setPrefSize(800, 40);
	}

	public int getPosFromDate(long d) {
		long range = rangeSelector.getSelRange();
		if (range == 0) {
			return 0;
		} else {
			return (int) ((d - rangeSelector.getSelMin()) * getWidth() / range);
		}
	}

	public long getDateFromPos(int x) {
		double range = rangeSelector.getSelRange();
		double x_d = (double) x;
		double value = (x_d / getWidth()) * range;
		return ((long) value) + rangeSelector.getSelMin();

	}

	public boolean isVisibleTime(long timeStamps) {
		return timeStamps >= rangeSelector.selMinProperty.longValue()
				&& timeStamps <= rangeSelector.selMaxProperty.longValue();
	}

}
