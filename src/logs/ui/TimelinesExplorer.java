package logs.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
 *         with the participation of marie, clement and charlelie
 */
public class TimelinesExplorer extends BorderPane {

	private LogEventsManager logEventsManager;
	private UnitConverter unitConverter;

	// visual elements
	private VBox centralPane;
	private Pane pane;
	private StackPane stackPane;
	private RangeSelector rangeSelector;
	private TimeRuler timeRuler;

	// visibility offset are used to display extended scene portions to avoid
	// masquing elements
	private int VISIBILITY_OFFSET = 10;
	private Insets VISIBILITY_INSETS = new Insets(2 * VISIBILITY_OFFSET);

	// scaling factor for the scene
	private Scale horizontalScale = new Scale(1, 1);
	private ArrayList<Text> textLabels;

	// selection rectangle
	private SelectionRectangle selectionRectangle;
	private boolean dragSelectionRectangleState = false;

	// current selection
	private ArrayList<LogEventNode> selectedNodeList;

	// Contextual Menu to apply fusion algorithms
	private ContextMenu contextMenu;

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

		// the StackPane
		this.stackPane = new StackPane();
		this.setCenter(stackPane);

		this.centralPane = new VBox();
		this.centralPane.setPadding(VISIBILITY_INSETS);
		this.centralPane.getTransforms().add(horizontalScale);

		// The pane that will contain the contextual menu and the selection
		// rectangle overlaid to the
		// centralPane
		this.pane = new Pane();
		this.pane.setPadding(VISIBILITY_INSETS);
		this.pane.prefWidthProperty().bind(this.centralPane.widthProperty());
		this.pane.prefHeightProperty().bind(this.centralPane.heightProperty());
		this.pane.prefHeight(600);
		pane.setVisible(false);

		stackPane.getChildren().add(pane);
		// This method creates a contextual menu but doesn't display it until
		// releasing the mouse
		contextMenu = new ContextMenu(this, pane);

		this.stackPane.getChildren().add(centralPane);

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

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				contextMenu.hide();

				if (event.isSecondaryButtonDown()) {
					if (selectedNodeList.size() > 1) {
						contextMenu.showAtPosition(event.getX(), event.getY());
					}
				} else {

					Node parentNode = event.getPickResult().getIntersectedNode().getParent();
					if (parentNode instanceof LogEventNode) {
						LogEventNode eventNode = (LogEventNode) parentNode;
						if (event.isShiftDown()) {
							if (selectedNodeList.contains(eventNode)) {
								selectedNodeList.remove(eventNode);
								eventNode.setSelected(false);
							} else {
								selectedNodeList.add(eventNode);
								eventNode.setSelected(true);
							}
						} else {
							for (LogEventNode node : selectedNodeList) {
								node.setSelected(false);
							}
							selectedNodeList.add(eventNode);
							eventNode.setSelected(true);
						}
					} else {
						selectionRectangle = new SelectionRectangle(new Point2D(event.getX(), event.getY()));
						pane.getChildren().add(selectionRectangle);
						dragSelectionRectangleState = true;
					}
				}
			}
		});

		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (dragSelectionRectangleState) {
					for (LogEventNode node : selectedNodeList) {
						node.setSelected(false);
					}
					selectedNodeList.clear();
					selectionRectangle.updateFromPoint(new Point2D(event.getX(), event.getY()));
					ArrayList<LogEventNode> nodes = findNodesInSelectionRectangle();
					selectedNodeList.addAll(nodes);
					for (LogEventNode node : selectedNodeList) {
						node.setSelected(true);
					}
				}
			}
		});

		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragSelectionRectangleState = false;
				pane.getChildren().remove(selectionRectangle);
			}
		});
	}

	private ArrayList<LogEventNode> findNodesInSelectionRectangle() {
		ArrayList<LogEventNode> nodes = new ArrayList<LogEventNode>();
		for (Node pane : this.centralPane.getChildren()) {
			if (pane instanceof LogEventsPane) {
				LogEventsPane eventsPane = (LogEventsPane) pane;
				if (eventsPane.getBoundsInParent().intersects(selectionRectangle.getBoundsInParent())) {
					this.selectedNodeList.addAll(eventsPane
							.findNodesInBounds(selectionRectangle.localToScene(selectionRectangle.getBoundsInLocal())));
				}
			}
		}
		return nodes;
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
		this.centralPane.getChildren().clear();
		this.textLabels = new ArrayList<Text>();

		selectedNodeList = new ArrayList<LogEventNode>();

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
				selectedNodeList.add(node);
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

	/**
	 * This method inserts the given pane at the given position in the
	 * centralPane.
	 *
	 * @param index
	 * @param pane
	 */
	public void insertPaneAtIndex(int index, LogEventsPane pane) {

		// PART1 : Update position of following panes (+1)
		int size = this.centralPane.getChildren().size();

		for (Node nodePane : this.centralPane.getChildren().subList(index, size)) {
			((LogEventsPane) nodePane).setIndex(((LogEventsPane) nodePane).getIndex() + 1);
		}

		// PART2 : Add the new pane at position
		pane.setIndex(index);
		this.centralPane.getChildren().add(index, pane);

	}

	/**
	 * This method deletes the pane at the given position.
	 *
	 * @param index
	 */
	public void deletePaneAtIndex(int index) {

		this.centralPane.getChildren().remove(index);

		int size = this.centralPane.getChildren().size();

		for (Node nodePane : this.centralPane.getChildren().subList(index, size)) {
			((LogEventsPane) nodePane).setIndex(((LogEventsPane) nodePane).getIndex() - 1);
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

	public void checkFusion() {
		Map<String, ArrayList> map = logEventsManager.recherchePattern();

		if (map == null) {
			return;
		}
		if ((boolean) map.get("fusion").get(0)) {
			map.remove("isFusion");
			animationFusion(map);
			LogEventsManager.getSelectedList().clear();
		} else {
			map.remove("isFusion");
			patternFinding(map);
			LogEventsManager.getSelectedList().clear();
		}
	}

	/**
	 * This function manages the animation when operating a fusion. It launches
	 * the method "recherchePattern" from the logEventsManager.
	 */
	public void animationFusion(Map<String, ArrayList> map) {

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

		for (String k : keys) {
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

		expandButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				LogEventsPane expandPane = (LogEventsPane) ((Button) arg0.getSource()).getParent().getParent();
				int expandIndex = expandPane.getIndex();
				if (!expandPane.isExpanded()) {
					for (LogEventsPane pane : expandPane.getChildrenPanes()) {
						expandIndex++;
						pane.setOpacity(expandPane.getOpacity() - 0.3);
						insertPaneAtIndex(expandIndex, pane);
					}
					expandPane.setExpanded(true);
				} else {
					int childrenLastPos = expandIndex + expandPane.getChildrenPanes().size();
					for (int i = childrenLastPos; i > expandIndex; i--) {
						deletePaneAtIndex(i);
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
			selectedNodeList.add(node);
			node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp() + logEvent.getDuration() / 2));
			node.setTailleX(logEvent.getDuration() / 2);
			Text textDuration = node.getText();
			textDuration.setText(String.valueOf(logEvent.getDuration() / 1000) + "s");
			textDuration.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
			textDuration.setFill(Color.WHITE);
			textDuration.setStroke(Color.GREY);
			textDuration.setStrokeWidth(0);
			double textW = unitConverter
					.getDurationInMillisFromPercentage(textDuration.getBoundsInLocal().getWidth() / length);
			double textH = textDuration.getBoundsInLocal().getHeight();
			if (textW > logEvent.getDuration()) {
				textDuration.setStrokeWidth(1);
			}
			textDuration.setTranslateX(node.getPosX() - textW / 2);
			textDuration.setTranslateY(l.getEndY() + textH / 4);
			textDuration.getTransforms().add(inverseScale);
			node.setFillColor(color);
			node.setOpacity(0.6);
			points.getChildren().add(node);
			points.getChildren().add(textDuration);
		}

		pane.getChildren().add(points);
		pane.prefWidthProperty().set(endPosInScene);
		pane.setOpacity(0);

		FadeTransition lastFadeTransition = new FadeTransition(Duration.millis(3000), pane);
		lastFadeTransition.setFromValue(0);
		lastFadeTransition.setToValue(1);

		String maxIndexKey = "NULLISH";
		int maxIndex = Integer.MIN_VALUE;

		Map<String, Integer> indexedKeys = new HashMap<String, Integer>();

		for (String currentKey : keys) {
			for (Node node : this.centralPane.getChildren()) {
				if (((LogEventsPane) node).getKey().equals(currentKey)) {

					indexedKeys.put(currentKey, ((LogEventsPane) node).getIndex());

					if (((LogEventsPane) node).getIndex() > maxIndex) {
						maxIndexKey = currentKey;
						maxIndex = ((LogEventsPane) node).getIndex();
					}
					;
				}
			}
		}

		indexedKeys.remove(maxIndexKey);

		double height = this.centralPane.getChildren().get(0).getBoundsInLocal().getHeight();
		final int maxIndexFinal = maxIndex;

		// Interpolation LOG plutôt que linéaire

		for (int index : indexedKeys.values()) {

			FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000),
					this.centralPane.getChildren().get(index));
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0.1);

			fades.add(fadeTransition);

			TranslateTransition translateTransition = new TranslateTransition();
			translateTransition.setDuration(Duration.millis(3000));
			translateTransition.setNode(this.centralPane.getChildren().get(index));
			translateTransition.setByY(height * (maxIndexFinal - index));
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

				deletePaneAtIndex(maxIndexFinal);

				insertPaneAtIndex(maxIndexFinal, pane);

				lastFadeTransition.play();

				Collection<Integer> collection = indexedKeys.values();

				ArrayList<Integer> indexes = new ArrayList<Integer>(collection);

				Collections.sort(indexes);
				Collections.reverse(indexes);

				for (int index : indexes) {
					LogEventsPane childPane = (LogEventsPane) centralPane.getChildren().get(index);

					// Apply inverted animation
					childPane.setTranslateY(childPane.getTranslateY() - height * (maxIndexFinal - index));

					childrenPanes.add(childPane);
					deletePaneAtIndex(index);
				}

				Collections.reverse(childrenPanes);
				pane.addChildrenPanes(childrenPanes);
			}
		});
	}

	/**
	 * This method inserts the new line of aggregators when the fusion is
	 * impossible (simple pattern matching)
	 */
	public void patternFinding(Map<String, ArrayList> map) {

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

		for (String k : keys) {
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
			selectedNodeList.add(node);
			node.setPosX(unitConverter.getPosInSceneFromTime(logEvent.getTimeStamp() + logEvent.getDuration() / 2));
			// node.scaleXProperty().bind(JavaFXUtils.getReversedScaleXBinding(horizontalScale.xProperty()));
			node.setTailleX(logEvent.getDuration() / 2);
			Text textDuration = node.getText();
			textDuration.setText(String.valueOf(logEvent.getDuration() / 1000) + "s");
			textDuration.getTransforms().add(inverseScale);
			textDuration.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
			textDuration.setFill(Color.WHITE);
			textDuration.setStroke(Color.GREY);
			textDuration.setStrokeWidth(0);
			double textW = unitConverter
					.getDurationInMillisFromPercentage(textDuration.getBoundsInLocal().getWidth() / length);
			double textH = textDuration.getBoundsInLocal().getHeight();
			if (textW > logEvent.getDuration()) {
				textDuration.setStrokeWidth(1);
			}
			textDuration.setTranslateX(node.getPosX() - textW / 2);
			textDuration.setTranslateY(l.getEndY() + textH / 4);
			node.setFillColor(color);
			points.getChildren().add(node);
			points.getChildren().add(textDuration);
		}

		pane.getChildren().add(points);
		pane.prefWidthProperty().set(endPosInScene);
		pane.setOpacity(0);

		FadeTransition lastFadeTransition = new FadeTransition(Duration.millis(3000), pane);
		lastFadeTransition.setFromValue(0);
		lastFadeTransition.setToValue(1);

		int maxIndex = Integer.MIN_VALUE;

		Map<String, Integer> indexedKeys = new HashMap<String, Integer>();

		for (String currentKey : keys) {
			for (Node node : this.centralPane.getChildren()) {
				if (((LogEventsPane) node).getKey().equals(currentKey)) {

					indexedKeys.put(currentKey, ((LogEventsPane) node).getIndex());

					if (((LogEventsPane) node).getIndex() > maxIndex) {
						maxIndex = ((LogEventsPane) node).getIndex();
					}
					;
				}
			}
		}

		insertPaneAtIndex(maxIndex + 1, pane);

		lastFadeTransition.play();
	}

}
