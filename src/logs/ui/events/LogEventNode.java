package logs.ui.events;


import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.CenteredRectangle;
import logs.ui.EventInspector;
import logs.ui.TimelinesExplorer;
import logs.utils.JavaFXUtils;

/**
 * This class represents a logEvent in the scene THe shape is an ellipse
 *
 * @author jeremiegarcia
 *
 * with the participation of marie, clement and charlelie
 */
public class LogEventNode extends Group {

	private LogEvent logEvent;
	private static LogEventNode prevActiveNode;
	private Boolean selected;
	private double RADIUS = 6;
//	private Ellipse item;
	private CenteredRectangle item;
	private Text text;
	private Color color = Color.BLUE;

	/**
	 * builds a LogEventNode with a LogEvent
	 *
	 * @param event : The event you want to create a node of.
	 */
	public LogEventNode(LogEvent event) {
		super();
		this.logEvent = event;
//		item = new Ellipse(this.logEvent.getTimeStamp(), 10, RADIUS / 2, RADIUS);
		item = new CenteredRectangle(this.logEvent.getTimeStamp(), -2, RADIUS / 2, RADIUS);
		text = new Text();
		text.setPickOnBounds(false);
		this.getChildren().add(item);
		selected=false;
		
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TimelinesExplorer.setInNode(true);
				EventInspector.getInstance().update(logEvent);
				LogEventNode.this.highlight(true);
				if (prevActiveNode != null && prevActiveNode != LogEventNode.this && !prevActiveNode.getSelected()) {
					prevActiveNode.highlight(false);
				}
				prevActiveNode = LogEventNode.this;
			}
		});
		
		//Fait pour le projet SITA
		/**
		 * This method highlights the node back in blue if selected
		 */
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(selected){
					LogEventNode.this.highlight(false);
					LogEventNode.this.highlight2(true);
				}
				TimelinesExplorer.setInNode(false);
			}
		});
		
		//Fait pour le projet SITA
		/**
		 * This method selects the node and highlights it
		 */
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
							ArrayList<LogEvent> temp= new ArrayList<LogEvent>();
							temp.add(logEvent);
							selectedList.put(key,temp);
						}
						selected=true;
						LogEventNode.this.highlight2(true);
					}
					System.out.println(LogEventsManager.getSelectedList());
				}
				
				if(!TimelinesExplorer.areInNode){
					highlight2(false);
				}
			}
		});
		
		highlight(false);
		
		//Fait pour le projet SITA
		/**
		 * This method starts the selection
		 */
		this.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
			System.out.println("bla");
	        if (evt.getCode() == KeyCode.CONTROL) {
	            evt.consume();
	        }
		});
	}
	
	

	public void setFillColor(Color color) {
		this.color = color;
		this.item.setFill(this.color);

	}

	/**
	 * This methods sets the visual parameters to highlight (true or false)
	 *
	 * @param setHighlight
	 */
	private void highlight(boolean setHighlight) {
		item.setFill(setHighlight ? JavaFXUtils.getEmphasizedColor(color) : color);
		if(color != Color.BLACK){
			item.setRadiusX(setHighlight ? RADIUS : RADIUS / 2);
		}
		item.setStroke(setHighlight ? Color.RED : Color.BLACK);
		item.setStrokeWidth(setHighlight ? 2 : 1);
	}

	//Fait pour le projet SITA
	/**
	 * This method highlights the node in blue
	 * 
	 * @param setHighlight
	 */
	public void highlight2(boolean setHighlight) {
		item.setFill(setHighlight ? JavaFXUtils.getEmphasizedColor(color) : color);
		if(color != Color.BLACK){
			item.setRadiusX(setHighlight ? RADIUS : RADIUS / 2);
		}
		item.setStroke(setHighlight ? Color.BLUE : Color.BLACK);
		item.setStrokeWidth(setHighlight ? 2 : 1);
	}
	
	//Fait pour le projet SITA
	public void setTailleX(double taille){
		this.item.radiusXProperty().setValue(taille);
		this.setRadius(taille);
	}
	
	//Fait pour le projet SITA
	public void setRadius(double radius){
		this.RADIUS = radius;
	}
	
	/**
	 * This methods sets the horizontal position of the node in the scene
	 *
	 * @param posX
	 */
	public void setPosX(double posX) {
		this.item.setCenterX(posX);
	}
	
	public double getPosX(){
		return this.item.getCenterX();
	}
	
	//Fait pour le projet SITA
	public Text getText(){
		return text;
	}
	
	//Fait pour le projet SITA
	/**
	 * This method gets the selection boolean
	 * 
	 * @return true if selected
	 */
	public Boolean getSelected(){
		return selected;
	}
	
	public void setSelected(Boolean bool){
		selected = bool;
	}
	public CenteredRectangle getItem(){
		return item;
	}
	public LogEvent getLogEvent(){
		return logEvent;
	}
}