package logs.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class CenteredRectangle extends Rectangle {
	
	private DoubleProperty centerX = new SimpleDoubleProperty();
	private DoubleProperty centerY = new SimpleDoubleProperty();

	private DoubleProperty radiusX = new SimpleDoubleProperty();
	private DoubleProperty radiusY = new SimpleDoubleProperty();

	public CenteredRectangle() {
		super();
	}
	
	public CenteredRectangle(double centerX, double centerY, double radiusX, double radiusY){
		super(centerX - radiusX, centerY + radiusY, 2*radiusX, 2*radiusY);
				
		this.centerX.addListener(new ChangeListener<Object>(){

			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				update();
			}
			
		});
		
		this.centerY.addListener(new ChangeListener<Object>(){

			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				update();
			}
			
		});
		
		this.radiusX.addListener(new ChangeListener<Object>(){

			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				update();
			}
			
		});
		
		this.radiusY.addListener(new ChangeListener<Object>(){

			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				update();
			}
			
		});
		
		setCenterX(centerX);
		setCenterY(centerY);
		setRadiusX(radiusX);
		setRadiusY(radiusY);
		
	}

	private void update(){
		this.setX(getCenterX() - getRadiusX());
		this.setY(getCenterY() + getRadiusY());
		this.setWidth(2*getRadiusX());
		this.setHeight(2*getRadiusY());
		this.setArcWidth(this.getWidth()/80);
		this.setArcHeight(this.getHeight()/2);
		if (this.getArcWidth()<5){
			this.setArcWidth(5);
		}
		if (this.getArcHeight()<5){
			this.setArcHeight(5);
		}
	}
	
	public double getCenterX() {
		return centerX.get();
	}

	public void setCenterX(double centerX) {
		this.centerX.setValue(centerX);
	}

	public double getCenterY() {
		return centerY.get();
	}

	public void setCenterY(double centerY) {
		this.centerY.setValue(centerY);
	}

	public double getRadiusX() {
		return radiusX.get();
	}

	public void setRadiusX(double radiusX) {
		this.radiusX.setValue(radiusX);
	}

	public double getRadiusY() {
		return radiusY.get();
	}

	public void setRadiusY(double radiusY) {
		this.radiusY.setValue(radiusY);
	}
	
	public DoubleProperty centerXProperty(){
		return centerX;
	}
	
	public DoubleProperty centerYProperty(){
		return centerY;
	}
	
	public DoubleProperty radiusXProperty(){
		return radiusX;
	}
	
	public DoubleProperty radiusYProperty(){
		return radiusY;
	}
	
	

}
