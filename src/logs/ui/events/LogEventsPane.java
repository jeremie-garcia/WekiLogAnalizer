package logs.ui.events;

import java.util.ArrayList;

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
	private ArrayList<LogEventsPane> childrenPanes;
	private boolean expanded = false;

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
		
		this.childrenPanes = new ArrayList<LogEventsPane>();
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

	public ArrayList<LogEventsPane> getChildrenPanes() {
		return childrenPanes;
	}

	public void addChildrenPanes(ArrayList<LogEventsPane> childrenPanes) {
		this.childrenPanes.addAll(childrenPanes);
	}
	
	public void setKey(String str){
		this.key=str;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
}
