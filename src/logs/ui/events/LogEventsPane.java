package logs.ui.events;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import weka.gui.SetInstancesPanel;

/**
 * This is a simple container for logEventsNode It stores a log key and an index
 * for filtering and repositioning purpose.
 *
 * @author jeremiegarcia
 *
 */
public class LogEventsPane extends Pane {

	private String key;
	private int index;
	private Color col;
	private Rectangle bgRectangle = new Rectangle();
	private boolean isSelected = false;

	public LogEventsPane(String key, int index, Color col) {
		super();
		this.setIndex(index);
		this.key = key;
		this.col = col;

		bgRectangle = new Rectangle();
		bgRectangle.widthProperty().bind(this.prefWidthProperty());
		bgRectangle.heightProperty().bind(this.heightProperty());

		this.setSelected(false);
		this.getChildren().add(bgRectangle);
	}

	protected void setSelected(boolean b) {
		this.isSelected = b;
		this.bgRectangle.setFill(isSelected ? col.deriveColor(1, 0.4, 1, 0.7) : Color.TRANSPARENT);

	}
	public String getKey(){
		return key;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	public void setKey(String str){
		this.key=str;
	}
	
}
