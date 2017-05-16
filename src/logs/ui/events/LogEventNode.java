package logs.ui.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.EventInspector;
import logs.ui.TimelinesExplorer;
import logs.ui.UnitConverter;
import logs.utils.JavaFXUtils;

/**
 * This class represents a logEvent in the scene THe shape is an ellipse
 *
 * @author jeremiegarcia
 *
 */
public class LogEventNode extends Group {

	private LogEvent logEvent;
	private static LogEventNode prevActiveNode;
	private Boolean selected;
	private int RADIUS = 6;
	private Ellipse item;
	private Ellipse item2;
	private double taille = RADIUS/2;
	private Color color = Color.BLUE;

	/**
	 * builds a LogEventNode with a LogEvent
	 *
	 * @param event
	 */
	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
		
		if (event.getDuration()==0){
			item = new Ellipse(this.logEvent.getTimeStamp(), 10, RADIUS / 2, RADIUS);
			this.getChildren().add(item);
		}
		else {
			item2 = new Ellipse(this.logEvent.getTimeStamp()+this.logEvent.getDuration()/2.0, 10, taille, RADIUS);
			this.getChildren().add(item2);
//			Rectangle item2 = new Rectangle(this.logEvent.getTimeStamp(),10, this.logEvent.getTimeStamp()+ this.logEvent.getDuration(), RADIUS);
//			item2.setArcHeight(60);
//			item2.setArcWidth(60);
//			item2.setFill(Color.ORANGE);
//			this.getChildren().add(item2);
		}
		
		selected=false;
		
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				EventInspector.getInstance().update(logEvent);
				LogEventNode.this.highlight(true);
				if (prevActiveNode != null && prevActiveNode != LogEventNode.this && !prevActiveNode.getSelected()) {
					prevActiveNode.highlight(false);
				}
				prevActiveNode = LogEventNode.this;
			}
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(selected){
					LogEventNode.this.highlight(false);
					LogEventNode.this.highlight2(true);
				}
			}
		});
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.isControlDown()){
				EventInspector.getInstance().update(logEvent);
				HashMap<String,ArrayList<LogEvent>> selectedList=LogEventsManager.getSelectedList();
				String key=((LogEventsPane) LogEventNode.this.getParent().getParent()).getKey();
				if(selected){
					selected = false;
					LogEventNode.this.highlight2(false);
					selectedList.get(key).remove(logEvent);
					if (selectedList.get(key).isEmpty()){
						selectedList.remove(key);
					}
				}
				else{
					if (selectedList.containsKey(key)){
						selectedList.get(key).add(logEvent);
					}
					else{
						ArrayList<LogEvent> bla= new ArrayList();
						bla.add(logEvent);
						selectedList.put(key,bla);
					}
					selected=true;
					LogEventNode.this.highlight2(true);
				}
				//System.out.println(selectedList);
			}
			}
		});
		
		highlight(false);
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
			System.out.println("bla");
	        if (evt.getCode() == KeyCode.CONTROL) {
	            evt.consume();
	        }
	});
	}
	
	

	public void setFillColor(Color color) {
		this.color = color;
		if (this.logEvent.getDuration()==0)
			this.item.setFill(this.color);
		else
			this.item2.setFill(Color.RED);
	}

	/**
	 * This methods sets the visual parameters to highlight (true or false)
	 *
	 * @param setHighlight
	 */
	private void highlight(boolean setHighlight) {
		if (this.logEvent.getDuration()==0){
		item.setFill(setHighlight ? JavaFXUtils.getEmphasizedColor(color) : color);
		item.setRadiusX(setHighlight ? RADIUS : RADIUS / 2);
		item.setStroke(setHighlight ? Color.RED : Color.BLACK);
		item.setStrokeWidth(setHighlight ? 2 : 1);}
		
		else{

		}
	}

	private void highlight2(boolean setHighlight) {
		item.setFill(setHighlight ? JavaFXUtils.getEmphasizedColor(color) : color);
		item.setRadiusX(setHighlight ? RADIUS : RADIUS / 2);
		item.setStroke(setHighlight ? Color.BLUE : Color.BLACK);
		item.setStrokeWidth(setHighlight ? 2 : 1);
	}
	
	/**
	 * This methods sets the horizontal position of the node in the scene
	 *
	 * @param posX
	 */
	public void setPosX(double posX) {
		if (this.logEvent.getDuration()==0)
			{this.item.setCenterX(posX);
			System.out.println("unnn"+posX);
			}
		else{
			this.item2.setCenterX(posX);
			System.out.println("deeux" +posX);

		}
	}
	
	public void setTailleX(double time){
		this.item2.radiusXProperty().setValue(time);
	}
	
	public Boolean getSelected(){
		return selected;
	}

}