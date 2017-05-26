package logs.ui;

import java.awt.Component;
import java.awt.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
 * with the participation of marie, clement and charlelie
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
	private static Rectangle rectangleSelec;
	public static boolean areInNode=false;
	
	//Liste des nodes
	private ArrayList<LogEventNode> listeNode;
	private double tailleFenetre=0;
	

	/**
	 * Builds a timelines explorer using a logManager
	 *
	 * @param logManager
	 * 
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
		this.pane.prefHeight(600);
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
			
		//Fait pour le projet SITA
		/**
		 * This method creates a selection rectangle.
		 */
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			
			public void handle(MouseEvent event) {
				if (areInNode==false){
				//TimelinesExplorer.animationFusion(event.getScreenX(), event.getScreenY());
				pointDepSelec=new Point2D.Double(event.getX(),event.getY());
				rectangleSelec=createRectangle(pointDepSelec, pointDepSelec);
				move=true;
				pane.getChildren().add(rectangleSelec);

				
				//System.out.println(pane.getChildren().size());

//				Rectangle test = new Rectangle();
//				test.widthProperty().bind(pane.prefWidthProperty());
//				test.heightProperty().bind(pane.prefHeightProperty());
//				test.setFill(Color.RED);
//				pane.setPrefHeight(10000000);
//				pane.setPrefWidth(100000000);
//
//				pane.getChildren().add(test);
				superPane.getChildren().add(pane);
				//centralPane.getChildren().add(pane);
				
			}
			}
			
		});
		
		/**
		 * This method updates the rectangle when the mouse moves.
		 */
		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				if(move){
					Point2D.Double newPoint = new Point2D.Double(event.getX(),event.getY());
					//pointDepSelec.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
					setRectangle(rectangleSelec, pointDepSelec, newPoint);
					updatehighlight();	
				}
					
			}	
		});
		
		/**
		 * This method removes the rectangle when the mouse is released.
		 */
		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				move=false;	
				
				pane.getChildren().remove(rectangleSelec);
				superPane.getChildren().remove(pane);	
			}	
		});
		
		
	}

	/**
	 * Update the visualization of the log events Uses the logEventsManager
	 * database. This should be called when data change in the logEventManager.
	 */
	public void update() {
		long begin = this.logEventsManager.getBeginTime();
		long end = this.logEventsManager.getEndTime();

		this.unitConverter = new UnitConverter(begin, end);
		
		double beginPosInScene = unitConverter.getPosInSceneFromTime(begin);
		double endPosInScene = unitConverter.getPosInSceneFromTime(end);
		tailleFenetre = endPosInScene-beginPosInScene;
		this.centralPane.getChildren().clear();
		this.textLabels = new ArrayList<Text>();
		
		listeNode = new ArrayList<LogEventNode>();
		
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
				//
				listeNode.add(node);
				//
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
	}
	
	public void checkFusion(){
		Map<String, ArrayList> map = logEventsManager.recherchePattern();
		
		if (map == null){
			return ;
		}
		if ((boolean) map.get("fusion").get(0)){
			map.remove("isFusion");
			animationFusion(map);
		}
		else{
			map.remove("isFusion");
			patternFinding(map);
		}
	}
	//Fait pour le projet SITA
	/**This function manages the animation when operating a fusion. It launches the method "recherchePattern"
	 * from the logEventsManager.
	 * 
	 * 
	 */
	public void animationFusion(Map<String, ArrayList> map){
		
		ArrayList<FadeTransition> fades = new ArrayList<FadeTransition>();
		ArrayList<TranslateTransition> translates = new ArrayList<TranslateTransition>();
			
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
		
		HBox textButton = new HBox();
		textButton.getTransforms().add(inverseScale);
		textButton.setTranslateY(-1);
		Text txt = new Text(key);
		txt.setFont(Font.font(8));
		
		this.textLabels.add(txt);		
		
		Button expandButton = new Button();
		expandButton.setMinHeight(1);
		expandButton.setMaxHeight(9);
		
		expandButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				LogEventsPane expandPane = (LogEventsPane) ((Button) arg0.getSource()).getParent().getParent();
				int expandIndex = expandPane.getIndex();
				if(! expandPane.isExpanded()){
					for (LogEventsPane pane : expandPane.getChildrenPanes()){
						expandIndex ++;
		    			pane.setOpacity(expandPane.getOpacity()-0.3);
						insertNewPane(expandIndex, pane);
					}
					expandPane.setExpanded(true);
				}
				else{
					int childrenLastPos = expandIndex + expandPane.getChildrenPanes().size();
					for(int i = childrenLastPos; i>expandIndex; i--){
						deletePane(i);
					}
					expandPane.setExpanded(false);
				}
			}
			
		});
		
		textButton.getChildren().add(expandButton);
		textButton.getChildren().add(txt);

		
		pane.getChildren().add(textButton);
		Line l = new Line(beginPosInScene, 10, endPosInScene, 10);
		l.setStroke(color.deriveColor(0, 1., 0.3, 1.));
		pane.getChildren().add(l);
		double length = l.getBoundsInLocal().getWidth();
		Group points = new Group();
		for (LogEvent logEvent : events) {
			LogEventNode node = new LogEventNode(logEvent);
			listeNode.add(node);
			node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp() + logEvent.getDuration()/2));
			//node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
			node.setTailleX(logEvent.getDuration()/2);
			Text textDuration = node.getText();
			textDuration.setText(String.valueOf(logEvent.getDuration()/1000)+"s");
			textDuration.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
			textDuration.setFill(Color.WHITE);
			textDuration.setStroke(Color.GREY);
			textDuration.setStrokeWidth(0);
			double textW = unitConverter.getDurationInMillisFromPercentage(textDuration.getBoundsInLocal().getWidth()/length);
			double textH = textDuration.getBoundsInLocal().getHeight();
			if (textW>logEvent.getDuration()){
				textDuration.setStrokeWidth(1);
			}
			textDuration.setTranslateX(node.getPosX()-textW/2);
			textDuration.setTranslateY(l.getEndY()+textH/4);
			textDuration.getTransforms().add(inverseScale);
			node.setFillColor(color);
			node.setOpacity(0.6);
			points.getChildren().add(node);
			points.getChildren().add(textDuration);
		}
		
		pane.getChildren().add(points);
		pane.prefWidthProperty().set(endPosInScene);
		pane.setOpacity(0);
		
		FadeTransition lastFadeTransition = new FadeTransition(Duration.millis(3000),pane);
		lastFadeTransition.setFromValue(0);
		lastFadeTransition.setToValue(1);
				
		String maxIndexKey = "NULLISH";
		int maxIndex = Integer.MIN_VALUE;
		
		Map<String, Integer> indexedKeys = new HashMap<String, Integer>();
		
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
				
		indexedKeys.remove(maxIndexKey);

		double height = this.centralPane.getChildren().get(0).getBoundsInLocal().getHeight();

//		int o = 0;
//		
//		for (Node node : this.centralPane.getChildren()){
//			System.out.println(("height " + o + " = " + ((LogEventsPane) node).getBoundsInLocal().getHeight()));
//			o++;
//		}
		
		final int maxIndexFinal = maxIndex;
		
		// Interpolation LOG plutôt que linéaire
		
		for(int index : indexedKeys.values()){
			
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000),this.centralPane.getChildren().get(index));
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0.1);
			
			fades.add(fadeTransition);
			
			TranslateTransition translateTransition = new TranslateTransition();
			translateTransition.setDuration(Duration.millis(3000));
			translateTransition.setNode(this.centralPane.getChildren().get(index));
			translateTransition.setByY(height*(maxIndexFinal - index));
			translateTransition.setInterpolator(Interpolator.EASE_OUT);
			
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
	        	
	    		ArrayList<LogEventsPane> childrenPanes = new ArrayList<LogEventsPane>();
	    		
	    		LogEventsPane maxIndexPane = (LogEventsPane) centralPane.getChildren().get(maxIndexFinal);
	    		maxIndexPane.setOpacity(0.7);

	    		childrenPanes.add(maxIndexPane);
	    		
	        	deletePane(maxIndexFinal);
	        	
	    		insertNewPane(maxIndexFinal, pane);
	    		
	    		lastFadeTransition.play();
	    		
	    		Collection<Integer> collection = indexedKeys.values();
	    		
	    		ArrayList<Integer> indexes = new ArrayList<Integer>(collection);
	    		
	    		Collections.sort(indexes);
	    		Collections.reverse(indexes);
	    			    		
	    		for(int index : indexes){
	    			LogEventsPane childPane = (LogEventsPane) centralPane.getChildren().get(index);
	    			
	    			
	    			//Apply inverted animation
	    			childPane.setTranslateY(childPane.getTranslateY()-height*(maxIndexFinal - index));
	    			
	    			childrenPanes.add(childPane);
	    			deletePane(index);
	    		}
	    		
	    		Collections.reverse(childrenPanes);
	    		pane.addChildrenPanes(childrenPanes);
	        }
	    });

	}
	
	//Fait pour le projet SITA
	/**
	 * This method inserts the new line of aggregators when the fusion is impossible (simple pattern matching)
	 */
	public void patternFinding(Map<String, ArrayList> map){
				
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
		
		HBox textButton = new HBox();
		textButton.getTransforms().add(inverseScale);
		textButton.setTranslateY(-1);
		Text txt = new Text(key);
		txt.setFont(Font.font(8));
		
		this.textLabels.add(txt);
		
		textButton.getChildren().add(txt);
		
		pane.getChildren().add(textButton);
		Line l = new Line(beginPosInScene, 10, endPosInScene, 10);
		l.setStroke(color.deriveColor(0, 1., 0.3, 1.));
		pane.getChildren().add(l);

		double length = this.centralPane.getWidth();
		
		Group points = new Group();
		for (LogEvent logEvent : events) {
			LogEventNode node = new LogEventNode(logEvent);
			listeNode.add(node);
			node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp() + logEvent.getDuration()/2));
			//node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
			node.setTailleX(logEvent.getDuration()/2);
			Text textDuration = node.getText();
			textDuration.setText(String.valueOf(logEvent.getDuration()/1000)+"s");
			textDuration.getTransforms().add(inverseScale);
			textDuration.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
			textDuration.setFill(Color.WHITE);
			textDuration.setStroke(Color.GREY);
			textDuration.setStrokeWidth(0);
			double textW = unitConverter.getDurationInMillisFromPercentage(textDuration.getBoundsInLocal().getWidth()/length);
			double textH = textDuration.getBoundsInLocal().getHeight();
			if (textW>logEvent.getDuration()){
				textDuration.setStrokeWidth(1);
			}
			textDuration.setTranslateX(node.getPosX()-textW/2);
			textDuration.setTranslateY(l.getEndY()+textH/4);
			node.setFillColor(color);
			points.getChildren().add(node);
			points.getChildren().add(textDuration);
		}
		
		pane.getChildren().add(points);
		pane.prefWidthProperty().set(endPosInScene);
		pane.setOpacity(0);
		
		FadeTransition lastFadeTransition = new FadeTransition(Duration.millis(3000),pane);
		lastFadeTransition.setFromValue(0);
		lastFadeTransition.setToValue(1);
				
		int maxIndex = Integer.MIN_VALUE;
		
		Map<String, Integer> indexedKeys = new HashMap<String, Integer>();
		
		for (String currentKey : keys){
			for (Node node : this.centralPane.getChildren()){
				if (((LogEventsPane) node).getKey().equals(currentKey)){
					
					indexedKeys.put(currentKey, ((LogEventsPane) node).getIndex());
				
					if (((LogEventsPane) node).getIndex()>maxIndex){
						maxIndex = ((LogEventsPane) node).getIndex();
					};
				}
			}
		}
		
		insertNewPane(maxIndex+1, pane);
		
		lastFadeTransition.play();
	}
	
	//Fait pour le projet SITA
	/**
	 * This method inserts the given pane at the given position in the centralPane.
	 * 
	 * @param position
	 * @param pane
	 */
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
	/**
	 * This method deletes the pane at the given position.
	 * 
	 * @param position
	 */
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
	 * @return imgView
	 */
	private ImageView createImageViewFromScene() {

		WritableImage snapshot = this.centralPane.snapshot(new SnapshotParameters(), null);
		ImageView imgView = new ImageView(snapshot);
		return imgView;
	}
	
	//Fait pour le projet SITA
	/**
	 * This method creates the selection rectangle, with a and b describing it.
	 * 
	 * In a practical way, a will be the origin of the rectangle and b the current position of the mouse.
	 * 
	 * @param a
	 * @param b
	 * @return rectangle
	 */
	public Rectangle createRectangle(Point2D.Double a, Point2D.Double b){
		double x = a.getX();
		double y = b.getY();
		double width = Math.abs(b.getX()-a.getX());
		double height = Math.abs(b.getY()-a.getY());
		Rectangle rectangle = new Rectangle(x,y,width,height);
		rectangle.setStroke(Color.BLACK);
		rectangle.setFill(Color.TRANSPARENT);
		ObservableList<Double> patron = rectangle.getStrokeDashArray();
		patron.add(20.);
		patron.add(10.);
		rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
		rectangle.setVisible(true);
		return rectangle;
	}
	
	//Fait pour le projet SITA
	/**
	 * This method sets the given rectangle with the new points a and b.
	 * 
	 * In a practical way, it keeps the selection rectangle updated with the mouse movements.
	 * 
	 * @param rectangle
	 * @param a
	 * @param b
	 */
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
	
	//Fait pour le projet SITA
	/**
	 * This methods returns the boolean areInNode.
	 * 
	 * It checks whether the mouse is on a node or not.
	 * @return areInNode
	 */
	public boolean areInNode(){
		return areInNode;
	}
	
	//Fait pour le projet SITA
	/**
	 * This method changes the state of areInNode, according to the boolean b.
	 * 
	 * @param b
	 */
	public static void setInNode(boolean b){
		areInNode=b;
	}
	
	//Fait pour le projet SITA
	/**
	 * This method returns the rectangle.
	 * 
	 * @return rectangleSelec
	 */
	public static Rectangle getRectangle (){
		return rectangleSelec;
	}
	
	private void updatehighlight(){
		logEventsManager.getSelectedList().clear();
		ArrayList<LogEvent> listeEvent = logEventsManager.getTimeSortedLogEventsAsArrayList();
		for(LogEvent event : listeEvent){
			
		}
		
		for (int i=0;i<listeNode.size();i++){
			if(areInRectangle(listeNode.get(i), rectangleSelec, i)){
				String key=((LogEventNode) listeNode.get(i)).getLogEvent().getLabel();
				LogEvent logEvent=listeNode.get(i).getLogEvent();
				if(logEventsManager.getSelectedList().containsKey(key)){
					logEventsManager.getSelectedList().get(key).add(logEvent);

				}
				else{

					ArrayList<LogEvent> bla=new ArrayList();
					bla.add(logEvent);
					logEventsManager.getSelectedList().put(key,bla);
				}
				listeNode.get(i).highlight2(true);
				listeNode.get(i).setSelected(true);
			}
			else{
				listeNode.get(i).highlight2(false);
				listeNode.get(i).setSelected(false);
			}
		}
	}
	
	private boolean areInRectangle(LogEventNode node, Rectangle rectangle,int position){
		double x_node = node.getPosX();
		double y_node = posNodeinPane(position);
		double x_node_pane = conversionFenPaneX(x_node);
		Bounds sizeRec = rectangle.getBoundsInLocal();
		if (x_node_pane>=sizeRec.getMinX() && x_node_pane <= sizeRec.getMaxX() && y_node >=sizeRec.getMinY() && y_node<=sizeRec.getMaxY()){
			return true;
		}
		else
			return false;
	}
	
	private double conversionFenPaneX(double fenX){
		return fenX*this.centralPane.getWidth()/tailleFenetre; // 800 ets la largeur de la fenetre en pixel
	}
	
	private double posNodeinPane(int indice){
		double posY=0;
		LogEventsPane superPane = (LogEventsPane)listeNode.get(indice).getParent().getParent();
		int max = superPane.getIndex();
		for (int i=0; i<max;i++){
			LogEventsPane pane = (LogEventsPane)listeNode.get(i).getParent().getParent();
			posY+=pane.getBoundsInLocal().getHeight();
		}
		
//		System.out.println(listeNode.get(indice).getItem().getCenterY());
		return posY+((Line) superPane.getChildren().get(2)).getEndY()+listeNode.get(indice).getItem().getHeight()/2;
	}

}
