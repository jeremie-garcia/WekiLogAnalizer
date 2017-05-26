package logs.ui;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
* This class represents the contextual Menu that has to appear after a selection to 
* permit fusion
*
* @author marie, clement and charlelie
*
*/

public class ContextMenu{
	private TimelinesExplorer tlExplorer;
	private String label;
	private Pane pane;
	private Shape boutonFusion;
	private Shape boutonDoNothing;
	private Shape boutonFinal;
	
	public ContextMenu(TimelinesExplorer tlExplorer, Pane pane){
		this.tlExplorer=tlExplorer;
		this.label="Fusion";
		this.pane=pane;
		Ellipse step1 = new Ellipse();
		step1.setCenterX(0);
		step1.setCenterY(0);
		step1.setRadiusX(30);
		step1.setRadiusY(30);
		Rectangle step2 = new Rectangle();
		step2.setX(-30);
		step2.setY(0);
		step2.setHeight(30);
		step2.setWidth(60);
		Shape bla=Shape.subtract(step1, step2);
		Ellipse step3 = new Ellipse();
		step3.setRadiusX(5);
		step3.setRadiusY(5);
		step3.setCenterX(0);
		step3.setCenterY(0);
		boutonFusion=Shape.subtract(bla,step3);
		boutonFusion.setFill(Color.DARKGREY);
		boutonFusion.setStroke(Color.BLACK);
		boutonFusion.setOpacity(0.5);
		this.boutonFusion.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tlExplorer.checkFusion();
			}
		});
		this.boutonFusion.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boutonFusion.setOpacity(1);
			}
		});
		this.boutonFusion.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boutonFusion.setOpacity(0.5);
			}
		});
		step1.setCenterX(15);
		step1.setCenterY(15);
		step1.setRadiusX(30);
		step1.setRadiusY(30);
		step2.setX(-15);
		step2.setY(-15);
		step2.setHeight(30);
		step2.setWidth(60);
		bla=Shape.subtract(step1, step2);
		step3.setRadiusX(5);
		step3.setRadiusY(5);
		step3.setCenterX(15);
		step3.setCenterY(15);
		boutonDoNothing=Shape.subtract(bla,step3);
		boutonDoNothing.setFill(Color.DARKGREY);
		boutonDoNothing.setStroke(Color.BLACK);
		boutonDoNothing.setOpacity(0.5);
		this.boutonDoNothing.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boutonDoNothing.setOpacity(1);
			}
		});
		this.boutonDoNothing.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boutonDoNothing.setOpacity(0.5);
			}
		});
		boutonFusion.setVisible(false);
		boutonDoNothing.setVisible(false);
		pane.getChildren().addAll(boutonFusion,boutonDoNothing);


	}
	
	public void afficher(double x,double y){
		System.out.println("blabl");
		boutonFusion.relocate(x-30,y-30);
		boutonDoNothing.relocate(x-30, y);
		boutonFusion.toFront();
		boutonDoNothing.toFront();
		boutonFusion.setVisible(true);
		boutonDoNothing.setVisible(true);
	}
	public void cacher(){
		boutonFusion.setVisible(false);
		boutonDoNothing.setVisible(false);
	}
}
