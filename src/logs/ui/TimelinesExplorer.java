package logs.ui;

import java.awt.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import logs.model.LogEvent;
import logs.model.LogEventsManager;
import logs.ui.events.LogEventNode;
import logs.ui.events.LogEventsPane;
import logs.utils.JavaFXUtils;

/**
 * Main container for individual nodes (LogEventsPane) representing each log
 * events by key. It also contains a ruler and a range selector vertically
 * aligned
 *
 * @author jeremiegarcia
 *
 *with the participation of marie, clement and charlelie
 */
public class TimelinesExplorer extends BorderPane {

	private LogEventsManager logEventsManager;
	private UnitConverter unitConverter;

	// visual elements
	private VBox centralPane;
	private Pane pane;
	private StackPane superPane;
	private RangeSelector rangeSelector;
	private TimeRuler timeRuler;
	// visibility offset are used to display extended scene portions to avoid
	// masquing elements
	private int VISIBILITY_OFFSET = 10;
	private Insets VISIBILITY_INSETS = new Insets(2 * VISIBILITY_OFFSET);

	// scaling factor for the scene
	private Scale horizontalScale = new Scale(1, 1);
	private ArrayList<Text> textLabels;
	
	//pour la selection
	private Point2D.Double pointDepSelec;
	private boolean move=false;
	Rectangle rectangleSelec;
	/**
	 * Builds a timelines exploree using a logManager
	 *
	 * @param logManager
	 */
	public TimelinesExplorer(LogEventsManager logManager) {
		super();
		this.setPrefWidth(800);
		this.setPrefHeight(500);

		this.logEventsManager = logManager;
		this.unitConverter = new UnitConverter(0, 1000);
		
		// create the time scale and the ruler
		VBox bottomBox = new VBox();

		// range Selector
		rangeSelector = new RangeSelector();
		rangeSelector.prefWidthProperty().bind(this.widthProperty());
		rangeSelector.setPadding(VISIBILITY_INSETS);

		// ruler
		timeRuler = new TimeRuler();
		timeRuler.prefWidthProperty().bind(this.widthProperty());

		bottomBox.getChildren().addAll(timeRuler, rangeSelector);
		this.setBottom(bottomBox);

		// set the clip area to prevent scene getting out of the bounds
		Rectangle r = new Rectangle();
		r.setX(-VISIBILITY_OFFSET / 2);
		r.setY(-VISIBILITY_OFFSET / 2);
		r.widthProperty().bind(this.widthProperty().add(VISIBILITY_OFFSET));
		r.heightProperty().bind(this.heightProperty().add(VISIBILITY_OFFSET));
		this.setClip(r);

		//the SatckPane
		this.superPane = new StackPane();
		//this.superPane.setPadding(VISIBILITY_INSETS);
		this.setCenter(superPane);
		
		this.centralPane = new VBox();
		// this.centralPane.prefWidthProperty().bind(this.widthProperty());
		this.centralPane.setPadding(VISIBILITY_INSETS);
		this.centralPane.getTransforms().add(horizontalScale);
		//this.setCenter(centralPane);
		
		//The pane
		this.pane = new Pane();
		this.pane.setPadding(VISIBILITY_INSETS);
		this.pane.prefWidth(800);
		this.pane.prefHeight(800);
		//this.setTop(pane);
		
		this.superPane.getChildren().add(centralPane);

		// scaling and translating functions (mapping between ruler and scene)
		rangeSelector.getVisiblePercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newPercentage) {
				updateCentralPaneScaleX();
				updateCentralPaneTranslateX();
				updateTimeRulerRange();
			}
		});

		// translate scene and the Text to keep them visible
		rangeSelector.getVisibleMinPercentage().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateCentralPaneTranslateX();
				updateTimeRulerRange();
			}
		});
	}
		//sélection rectangle
//		this.setOnMousePressed(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//			
//				//TimelinesExplorer.animationFusion(event.getScreenX(), event.getScreenY());
//				pointDepSelec=new Point2D.Double(event.getX(),event.getY());
//				rectangleSelec=createRectangle(pointDepSelec, pointDepSelec);
//				move=true;
//				pane.getChildren().add(rectangleSelec);
//
//				
//				System.out.println(move);
//				System.out.println(pane.getChildren().size());
//
////				Rectangle test = new Rectangle();
////				test.widthProperty().bind(pane.prefWidthProperty());
////				test.heightProperty().bind(pane.prefHeightProperty());
////				test.setFill(Color.RED);
////				pane.setPrefHeight(10000000);
////				pane.setPrefWidth(100000000);
////
////				pane.getChildren().add(test);
//				superPane.getChildren().add(pane);
//				//centralPane.getChildren().add(pane);
//			}
//
//			
//		});
//		
//		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				//TimelinesExplorer.animationFusion(event.getScreenX(), event.getScreenY());
//				System.out.println(move);
//				if(move){
//					Point2D.Double newPoint = new Point2D.Double(event.getX(),event.getY());
//					//pointDepSelec.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
//					setRectangle(rectangleSelec, pointDepSelec, newPoint);
//					System.out.println("drag");
//					
//				}
//					
//			}	
//		});
//		
//		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				
//				//TimelinesExplorer.animationFusion(event.getScreenX(), event.getScreenY());
//				move=false;	
//				System.out.println(move);
//				pane.getChildren().remove(rectangleSelec);
//				superPane.getChildren().remove(pane);	
//			}	
//		});
//	}

	/**
	 * Update the visualization of the log events Uses the logEventsManager
	 * database. This should be called when data change in the logEventManager.
	 *
	 * @param newEventsMap
	 */
	public void update() {
		long begin = this.logEventsManager.getBeginTime();
		long end = this.logEventsManager.getEndTime();

		this.unitConverter = new UnitConverter(begin, end);

		double beginPosInScene = unitConverter.getPosInSceneFromTime(begin);
		double endPosInScene = unitConverter.getPosInSceneFromTime(end);

		this.centralPane.getChildren().clear();
		this.textLabels = new ArrayList<Text>();
				
		int index = 0;

		Scale inverseScale = new Scale(1, 1);
		inverseScale.xProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
		for (String key : this.logEventsManager.getLogevents().keySet()) {
			Color color = JavaFXUtils.getColorWithGoldenRationByIndex(index);
			LogEventsPane pane = new LogEventsPane(key, index, color);
			pane.setPrefHeight(60);
			Text txt = new Text(key);
			txt.setFont(Font.font(8));
			txt.getTransforms().add(inverseScale);
			txt.setTranslateY(6);
			this.textLabels.add(txt);
			pane.getChildren().add(txt);
			Line l = new Line(beginPosInScene, 10, endPosInScene, 10);
			l.setStroke(color.deriveColor(0, 1., 0.3, 1.));
			pane.getChildren().add(l);

			Group points = new Group();
			for (LogEvent logEvent : this.logEventsManager.getLogevents().get(key)) {
				LogEventNode node = new LogEventNode(logEvent);
				node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp()));
				node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
				node.setFillColor(color);
				points.getChildren().add(node);
			}

			pane.getChildren().add(points);
			pane.prefWidthProperty().set(endPosInScene);
			this.centralPane.getChildren().add(pane);
			index++;
		}		
		
		this.rangeSelector.selectAll();

		ImageView backgroundImage = this.createImageViewFromScene();
		this.rangeSelector.setBgImageView(backgroundImage);
		
		//animationFusion(4000, 400);

	}
	
	//Fait pour le projet SITA
	/**This function manage the animation when operating a fusion
	 * 
	 * @param x
	 * @param y
	 */
	public void animationFusion(){
		
		Map<String, ArrayList> map = logEventsManager.recherchePattern();
			
		ArrayList<LogEvent> events = map.get("eventList");
		ArrayList<String> keys = map.get("keyList");
		
		long begin = this.logEventsManager.getBeginTime();
		long end = this.logEventsManager.getEndTime();

		this.unitConverter = new UnitConverter(begin, end);

		double beginPosInScene = unitConverter.getPosInSceneFromTime(begin);
		double endPosInScene = unitConverter.getPosInSceneFromTime(end);
		
		Scale inverseScale = new Scale(1, 1);
		inverseScale.xProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
		
		String key = new String();
		Color color = Color.BLACK;
		
		for (String k : keys){
			key += k;
		}
		
		LogEventsPane pane = new LogEventsPane(key, 0, color);
		pane.setPrefHeight(60);
		Text txt = new Text(key);
		txt.setFont(Font.font(8));
		txt.getTransforms().add(inverseScale);
		txt.setTranslateY(6);
		this.textLabels.add(txt);
		pane.getChildren().add(txt);
		Line l = new Line(beginPosInScene, 10, endPosInScene, 10);
		l.setStroke(color.deriveColor(0, 1., 0.3, 1.));
		pane.getChildren().add(l);

		Group points = new Group();
		for (LogEvent logEvent : events) {
			LogEventNode node = new LogEventNode(logEvent);
			node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp()));
			node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
			node.setFillColor(color);
			points.getChildren().add(node);
		}

		pane.getChildren().add(points);
		pane.prefWidthProperty().set(endPosInScene);
		
		String maxIndexKey = "NULLISH";
		int maxIndex = Integer.MIN_VALUE;
		
		Map<String, Integer> indexedKeys = new HashMap();
		
		for (String currentKey : keys){
			for (Node node : this.centralPane.getChildren()){
				if (((LogEventsPane) node).getKey().equals(currentKey)){
					indexedKeys.put(currentKey, ((LogEventsPane) node).getIndex());
				
					if (((LogEventsPane) node).getIndex()>maxIndex){
						maxIndexKey = currentKey;
						maxIndex = ((LogEventsPane) node).getIndex();
					};
				}
			}
		}
		
		System.out.println("Index = " + maxIndex + " and key = " + maxIndexKey);
		
		indexedKeys.remove(maxIndexKey);
		ArrayList<FadeTransition> fades = new ArrayList<FadeTransition>();
		ArrayList<TranslateTransition> translates = new ArrayList<TranslateTransition>();

		double height = this.centralPane.getChildren().get(0).getBoundsInLocal().getHeight();
		
		final int maxIndexFinal = maxIndex;
		
		for(int index : indexedKeys.values()){
			
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000),this.centralPane.getChildren().get(index));
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0.1);
			
			fades.add(fadeTransition);
			
			TranslateTransition translateTransition = new TranslateTransition();
			translateTransition.setDuration(Duration.millis(3000));
			translateTransition.setNode(this.centralPane.getChildren().get(index));
			translateTransition.setByY(height*(maxIndexFinal - index));
			
			translates.add(translateTransition);
			
		}
				
		ParallelTransition parallelTransition = new ParallelTransition();
	    parallelTransition.getChildren().addAll(fades);
	    parallelTransition.getChildren().addAll(translates);
	    parallelTransition.setCycleCount(1);
	    parallelTransition.play();
			    
	    parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {

	        @Override
	        public void handle(ActionEvent event) {
	        	System.out.println("PLAYED");
	        	
	        	deletePane(maxIndexFinal);
	    		insertNewPane(maxIndexFinal, pane);	
	    		
	    		Collection<Integer> collection = indexedKeys.values();
	    		
	    		ArrayList<Integer> indexes = new ArrayList<Integer>(collection);
	    		
	    		Collections.sort(indexes);
	    		Collections.reverse(indexes);
	    		
	    		for(int index : indexes){
	    			deletePane(index);
	    		}
	        }
	    });

	}
	
	//Fait pour le projet SITA
	public void insertNewPane(int position, LogEventsPane pane){
		
		//PART1 : Update position of following panes (+1)
		
		int size = this.centralPane.getChildren().size();
		
		for(Node nodePane : this.centralPane.getChildren().subList(position, size)){
			((LogEventsPane) nodePane).setIndex(((LogEventsPane) nodePane).getIndex()+1);
		}
		
		//PART2 : Add the new pane at position
		
		pane.setIndex(position);
		this.centralPane.getChildren().add(position, pane);
		
	}
	
	//Fait pour le projet SITA
	public void deletePane(int position){
		
		this.centralPane.getChildren().remove(position);
		
		int size = this.centralPane.getChildren().size();
		
		for(Node nodePane : this.centralPane.getChildren().subList(position, size)){
			((LogEventsPane) nodePane).setIndex(((LogEventsPane) nodePane).getIndex()-1);
		}
	}

	/**
	 * This method updates the ruler according to the current VisibleMin and
	 * Visible Max percentages of the range selector
	 */
	private void updateTimeRulerRange() {
		long minTime = unitConverter
				.getDurationInMillisFromPercentage(rangeSelector.getVisibleMinPercentage().doubleValue());
		long maxTime = unitConverter
				.getDurationInMillisFromPercentage(rangeSelector.getVisibleMaxPercentage().doubleValue());
		timeRuler.getMinTimeInMillisProperty().set(minTime);
		timeRuler.getMaxTimeInMillisProperty().set(maxTime);
	}

	/**
	 * This methods updates the horizontal scale ratio according to visible
	 * range and the scene size
	 */
	private void updateCentralPaneScaleX() {
		double visibleWidthInPixels = centralPane.getWidth();
		double sceneWidthInSceneUnits = unitConverter.getLengthInSceneFromPercentage(1.0);
		double ratio = visibleWidthInPixels / sceneWidthInSceneUnits;
		double visiblePercentage = rangeSelector.getVisiblePercentage().doubleValue();
		horizontalScale.setX(ratio / visiblePercentage);
	}

	/**
	 * This method updates the translateX of the central pane and the labels
	 * according to the Visible Min percentage
	 */
	private void updateCentralPaneTranslateX() {
		double minPercentage = rangeSelector.getVisibleMinPercentage().doubleValue();
		double scenePos = unitConverter.getPosInSceneFromPercentage(minPercentage);
		centralPane.setTranslateX(-scenePos * horizontalScale.getX());

		for (Text txt : textLabels) {
			txt.setX(scenePos * horizontalScale.getX());
		}
	}

	/**
	 * this methods creates an ImageView from the scene (screen snapshot)
	 *
	 * @return
	 */
	private ImageView createImageViewFromScene() {

		WritableImage snapshot = this.centralPane.snapshot(new SnapshotParameters(), null);
		ImageView imgView = new ImageView(snapshot);
		return imgView;
	}
	
	
	public Rectangle createRectangle(Point2D.Double a, Point2D.Double b){
		double x = a.getX();
		double y = b.getY();
		double width = Math.abs(b.getX()-a.getX());
		double height = Math.abs(b.getY()-a.getY());
		Rectangle rectangle = new Rectangle(x,y,width,height);
		rectangle.setStroke(Color.RED);
		rectangle.setFill(Color.TRANSPARENT);
		rectangle.setVisible(true);
		return rectangle;
	}
	
	public void setRectangle(Rectangle rectangle, Point2D.Double a, Point2D.Double b){
		if (b.getX()>a.getX() && b.getY() > a.getY()){
			rectangle.setWidth((b.getX()-a.getX()));
			rectangle.setHeight((b.getY()-a.getY()));
		}
		
		else if (b.getX()<a.getX() && b.getY() > a.getY()){
			rectangle.setX(b.getX());
			rectangle.setWidth(a.getX()-b.getX());
			rectangle.setHeight((b.getY()-a.getY()));
		}
		
		else if (b.getX()<a.getX() && b.getY() < a.getY()){
			rectangle.setX(b.getX());
			rectangle.setY(b.getY());
			rectangle.setWidth(a.getX()-b.getX());
			rectangle.setHeight(a.getY()-b.getY());
		}
		
		else {
			rectangle.setY(b.getY());
			rectangle.setWidth(b.getX()-a.getX());
			rectangle.setHeight(a.getY()-b.getY());
		}
		

	}

}
