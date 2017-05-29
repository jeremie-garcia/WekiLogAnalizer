package logs.ui;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
* This class represents the contextual Menu that has to appear after a selection to 
* permit fusion
*
* @author marie, clement and charlelie
*
*/

public class ContextMenu{
	private Text labelFusion;
	private Text labelNothing;
	private Pane pane;
	private Shape boutonFusion;
	private Shape boutonDoNothing;
	
	public ContextMenu(TimelinesExplorer tlExplorer, Pane pane){
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
		labelFusion=new Text("Fusion");
		labelFusion.setMouseTransparent(true);
		labelFusion.setPickOnBounds(false);
		this.boutonFusion.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tlExplorer.checkFusion();
			}
		});
		this.boutonFusion.setOnMouseEntered( new EventHandler<MouseEvent>() {
			@Override
            public void handle(MouseEvent event) {
				boutonFusion.setOpacity(1);
            };
        });

		this.boutonFusion.setOnMouseExited( new EventHandler<MouseEvent>() {
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
		labelNothing=new Text("Do Nothing");
		labelNothing.setFont(Font.font(9));
		labelNothing.setMouseTransparent(true);
		labelNothing.setPickOnBounds(false);
		this.boutonDoNothing.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
            public void handle(MouseEvent event) {
				boutonDoNothing.setOpacity(1);
            };
        });

		this.boutonDoNothing.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boutonDoNothing.setOpacity(0.5);
			}
		});
		boutonFusion.setVisible(false);
		boutonDoNothing.setVisible(false);
		pane.getChildren().addAll(boutonFusion,boutonDoNothing,labelFusion,labelNothing);
	}
	
	public void afficher(double x,double y){
		System.out.println("blabl");
		boutonFusion.relocate(x-30,y-30);
		labelFusion.relocate(x-17,y-22);
		boutonDoNothing.relocate(x-30, y);
		labelNothing.relocate(x-22, y+7);
		boutonFusion.setVisible(true);
		boutonDoNothing.setVisible(true);
		labelFusion.setVisible(true);
		labelNothing.setVisible(true);
		pane.setVisible(true);
		pane.setMouseTransparent(false);
	}
	public void cacher(){
		pane.setVisible(true);
		boutonFusion.setVisible(false);
		boutonDoNothing.setVisible(false);
		labelFusion.setVisible(false);
		labelNothing.setVisible(false);
	}
	public void cacherControl(){
		pane.setVisible(true);
		boutonFusion.setVisible(false);
		boutonDoNothing.setVisible(false);
		labelFusion.setVisible(false);
		labelNothing.setVisible(false);
		pane.setVisible(false);
		pane.setMouseTransparent(true);
	}
}
